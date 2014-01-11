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
package corbit.commons.ml;

import corbit.commons.Vocab;
import corbit.commons.io.Console;
import java.util.HashMap;
import java.util.Iterator;

public class IntFeatVector extends HashMap<Integer, Double> {

    private static final long serialVersionUID = 1L;

    public IntFeatVector(IntFeatVector v) {
        super(v);
    }

    public IntFeatVector() {
        super();
    }

    public void append(IntFeatVector v) {
        Iterator<Integer> it = v.keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            double val = containsKey(i) ? get(i) : 0.0d;
            put(i, val + v.get(i));
        }
    }

    public void subtract(IntFeatVector v) {
        Iterator<Integer> it = v.keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            double val = containsKey(i) ? get(i) : 0.0d;
            put(i, val - v.get(i));
        }
    }

    public static IntFeatVector append(IntFeatVector v1, IntFeatVector v2) {
        IntFeatVector v = new IntFeatVector(v1);
        v.append(v2);
        return v;
    }

    public static IntFeatVector subtract(IntFeatVector v1, IntFeatVector v2) {
        IntFeatVector v = new IntFeatVector(v1);
        v.subtract(v2);
        return v;
    }

    public static IntFeatVector multiply(IntFeatVector v, double d) {
        IntFeatVector r = new IntFeatVector(v);
        Iterator<Integer> it = v.keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            r.put(i, r.get(i) * d);
        }
        return r;
    }

    public static IntFeatVector divide(IntFeatVector v, double d) {
        IntFeatVector r = new IntFeatVector(v);
        Iterator<Integer> it = v.keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            r.put(i, r.get(i) / d);
        }
        return r;
    }

    public void multiplyBy(double d) {
        Iterator<Integer> it = keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            put(i, get(i) * d);
        }
    }

    public void divideBy(double d) {
        Iterator<Integer> it = keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            put(i, get(i) / d);
        }
    }

    public void print(Vocab voc) {
        Iterator<Integer> it = keySet().iterator();
        while (it.hasNext()) {
            int i = it.next();
            double d = this.get(i);
            if (d != 0.0d) {
                Console.writeLine(voc.get(i) + "\t" + d);
            }
        }
        Console.writeLine();
    }
}