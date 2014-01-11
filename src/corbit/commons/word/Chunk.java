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
package corbit.commons.word;

public class Chunk {

    public final UnsegmentedSentence sent;
    public final int begin;
    public final int end;
    public final String form;
    public static final String rootForm = "(ROOT)";

    public Chunk(UnsegmentedSentence sent, int begin, int end) {
        this.sent = sent;
        this.begin = begin;
        this.end = end;
        this.form = (begin == -1 && end == -1) ? rootForm : sent.substring(begin, end);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Chunk)) {
            return false;
        }
        Chunk c = (Chunk) obj;
        return (sent == c.sent && begin == c.begin && end == c.end);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        int k = 31;
        hash = hash * k + sent.hashCode();
        hash = hash * k + begin;
        hash = hash * k + end;
        return hash;
    }

    @Override
    public String toString() {
        return begin + ":" + end + ":" + form;
    }
}