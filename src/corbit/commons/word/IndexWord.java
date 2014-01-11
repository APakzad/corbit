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

public class IndexWord {

    public final ParsedSentence sent;
    public final int index;
    public int head;
    public final String form;
    public final String tag;
    public final ArcLabel arclabel;

    public IndexWord(ParsedSentence sent, int index, int head, String form, String tag, ArcLabel arclabel) {
        this.sent = sent;
        this.index = index;
        this.head = head;
        this.form = form;
        this.tag = tag;
        this.arclabel = arclabel;
    }

    public IndexWord(IndexWord w) {
        this.sent = w.sent;
        this.index = w.index;
        this.head = w.head;
        this.form = w.form;
        this.tag = w.tag;
        this.arclabel = w.arclabel;
    }

    public DepChunk toDepChunk() {
        int headBegin = head >= 0 ? sent.getBeginIndex(head) : head;
        int headEnd = head >= 0 ? sent.getEndIndex(head) : head;
        return new DepChunk(sent.sequence, sent.getBeginIndex(index), sent.getEndIndex(index), tag, headBegin, headEnd, sent.get(index).arclabel);
    }

    @Override
    public String toString() {
        return index + ":" + head + ":" + (arclabel != null ? arclabel + ":" : "") + form + ":" + tag;
    }
}