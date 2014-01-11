/*
 * Corbit, a text analyzer
 * 
 * Copyright (c) 2010-2012, Jun Hatori
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names of the authors nor the names of its contributors
 *       may be used to endorse or promote products derived from this
 *       software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package corbit.commons.util;

import java.lang.Thread.State;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public abstract class Generator<T> implements Iterator<T>, Iterable<T> {

    private static final Object NULL = new Object();
    private static final Object END_OF_QUEUE = new Object();
    private static final Object NOT_PREPARED = new Object();
    private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>(1);
    private final Semaphore lock = new Semaphore(0);
    private RuntimeException thrown = null;
    private Object nextValue = NOT_PREPARED;
    private final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Generator.this.iterate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                thrown = e;
            } finally {
                queue.add(END_OF_QUEUE);
            }
        }
    });

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            shutdown();
        }
    }

    @Override
    public boolean hasNext() {
        prepareNext();
        return nextValue != END_OF_QUEUE;
    }

    @Override
    public T next() {
        if (thread == Thread.currentThread()) {
            throw new IllegalStateException("Illegal call");
        }

        prepareNext();

        if (thrown != null) {
            RuntimeException t = thrown;
            thrown = null;
            throw t;
        }

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        @SuppressWarnings("unchecked")
        T val = (T) nextValue;

        nextValue = NOT_PREPARED;

        return val;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract void iterate() throws InterruptedException;

    public void shutdown() {
        thread.interrupt();
    }

    protected void yieldReturn(T value) throws InterruptedException {
        if (thread != Thread.currentThread()) {
            throw new IllegalStateException("Illegal call");
        }

        if (!queue.offer(value != null ? value : NULL)) {
            throw new AssertionError();
        }

        lock.acquire();
    }

    protected void yieldBreak() throws InterruptedException {
        throw new UnsupportedOperationException("to be implemented");
    }

    private void prepareNext() {
        if (nextValue != NOT_PREPARED) {
            return;
        }

        if (thread.getState() == State.NEW) {
            thread.start();
        } else {
            lock.release();
        }

        Object value;
        boolean interrupted = false;
        while (true) {
            try {
                value = queue.take();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        nextValue = (value != NULL) ? value : null;
    }
}