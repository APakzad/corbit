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

public class DepChunk extends TaggedChunk {

    public int headBegin;
    public int headEnd;
    public ArcLabel arcLabel;

    public DepChunk(UnsegmentedSentence sent, int begin, int end, String tag, int headBegin, int headEnd, ArcLabel arcLabel) {
        super(sent, begin, end, tag);
        this.headBegin = headBegin;
        this.headEnd = headEnd;
        this.arcLabel = arcLabel;
    }

    public DepChunk(DepChunk dw) {
        this(dw.sent, dw.begin, dw.end, dw.tag, dw.headBegin, dw.headEnd, dw.arcLabel);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DepChunk)) {
            return false;
        }
        DepChunk dw = (DepChunk) obj;
        return (super.equals(dw) && headBegin == dw.headBegin && headEnd == dw.headEnd && arcLabel == dw.arcLabel);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        int k = 31;
        hash = hash * k + headBegin;
        hash = hash * k + headEnd;
        hash = hash * k + arcLabel.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return super.toString() + ":" + headBegin + ":" + headEnd;
    }
}