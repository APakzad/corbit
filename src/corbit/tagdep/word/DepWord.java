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
package corbit.tagdep.word;

public class DepWord extends Word {

    public int index;
    public int head;
    public DepTreeSentence sent;
    public String dependency;

    public DepWord(DepTreeSentence sent, int index, String form, String pos,String lemm, int head, String dependency) {
        super(form, pos, lemm);
        this.sent = sent;
        this.index = index;
        this.head = head;
        this.dependency=dependency;
    }

    public DepWord(DepWord dw) {
        this(dw.sent, dw.index, dw.form, dw.pos,dw.lemm, dw.head,dw.dependency);
    }

    public DepWord() {
        this(null, -1, null, null,null, -1,null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DepWord)) {
            return false;
        }
        DepWord dw = (DepWord) obj;
        return index == dw.index && 
               head == dw.head && 
               sent == dw.sent && 
               super.equals((Word) dw);
    }

    @Override
    public int hashCode() {
        return (index << head | index >> (32 - head)) ^ sent.hashCode() ^ super.hashCode();
    }

    @Override
    public String toString() {
        return index + ":" + super.toString() + "/" + (head != -2 ? head : "");
    }
}
