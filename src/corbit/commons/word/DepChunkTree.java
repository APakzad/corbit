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


import java.util.LinkedList;
import java.util.List;

public class DepChunkTree extends DepChunk {

    public final List<DepChunkTree> children;

    public DepChunkTree(UnsegmentedSentence sent, int begin, int end, String tag, int headBegin, int headEnd, ArcLabel label) {
        super(sent, begin, end, tag, headBegin, headEnd, label);
        children = new LinkedList<>();
    }

    public DepChunkTree(DepChunkTree t) {
        super(t);
        children = new LinkedList<>(t.children);
    }

    public DepChunkTree(DepChunk w) {
        super(w);
        children = new LinkedList<>();
    }

    public DepChunkTree(DepChunk w, List<DepChunkTree> children) {
        super(w);
        this.children = children;
    }

    public boolean isRoot() {
        return (form.equals(rootForm) && tag.equals(rootTag));
    }

    public int getSpanBeginIndex() {
        return getSpanBeginIndex(this);
    }

    private static int getSpanBeginIndex(DepChunkTree t) {
        int idx = t.begin;
        for (DepChunkTree c : t.children) {
            int cidx = getSpanBeginIndex(c);
            if (cidx < idx) {
                idx = cidx;
            }
        }
        return idx;
    }

    public DepChunkTree findSubtree(int begin, int end) {
        if (this.begin == begin && this.end == end) {
            return this;
        }
        for (DepChunkTree dt : children) {
            DepChunkTree _dt;
            if (dt.begin == begin && dt.end == end) {
                return dt;
            } else if ((_dt = dt.findSubtree(begin, end)) != null) {
                return _dt;
            }
        }
        return null;
    }

    public DepChunkTree findSubtreeEndsAt(int end) {
        if (this.end == end) {
            return this;
        }
        for (DepChunkTree dt : children) {
            DepChunkTree _dt;
            if (dt.end == end) {
                return dt;
            } else if ((_dt = dt.findSubtreeEndsAt(end)) != null) {
                return _dt;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DepChunkTree)) {
            return false;
        }
        DepChunkTree dt = (DepChunkTree) obj;
        return super.equals(dt) && children.equals(dt.children);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        int k = 31;
        hash = hash * k + children.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" ");
        if (children != null && children.size() > 0) {
            sb.append("{ ");
            for (int i = 0; i < children.size(); ++i) {
                sb.append(children.get(i).toString());
                if (i < children.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("} ");
        }
        return sb.toString();
    }
}
