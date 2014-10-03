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


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParsedSentence extends LinkedList<IndexWord> {

    private static final long serialVersionUID = 1L;
    UnsegmentedSentence sequence = null;
    List<List<IndexWord>> childs = null;
    /**
     * denote the ending character index of the i-th word
     */
    int[] bounds = null;

    ParsedSentence() {
    }

    public IndexTree getWordWithChild(int idx) {
        return new IndexTree(get(idx), childs.get(idx));
    }

    public IndexWord findWord(int begin, int end) {
        int idx = getWordIndex(begin, end);
        return idx >= 0 ? get(idx) : null;
    }

    public IndexWord findWord(int begin) {
        if (begin == 0) {
            return get(0);
        }

        int idx = Arrays.binarySearch(bounds, begin);
        if (idx >= 0) {
            return get(idx + 1);
        } else {
            return null;
        }
    }

    public IndexWord findWordEndingAt(int end) {
        int idx = Arrays.binarySearch(bounds, end);
        if (idx >= 0) {
            return get(idx);
        } else {
            return null;
        }
    }

    public int getNumChild(int idx) {
        return childs.get(idx).size();
    }

    public int getBeginIndex(int idx) {
        return idx < 0 ? idx : idx > 0 ? bounds[idx - 1] : 0;
    }

    public int getEndIndex(int idx) {
        return idx < 0 ? idx : bounds[idx];
    }

    public IndexWord getWord(int begin, int end) {
        int idx = getWordIndex(begin, end);
        if (idx >= 0) {
            return get(idx);
        } else {
            return null;
        }
    }

    public int getWordIndex(int begin, int end) {
        if (bounds == null) {
            throw new RuntimeException("Create index first.");
        }

        if (begin < 0 && begin == end) {
            return begin;
        }

        int idx = Arrays.binarySearch(bounds, end);
        if (idx < 0) {
            return -2;
        }
        int _begin = idx == 0 ? 0 : bounds[idx - 1];
        if (idx >= 0 && _begin == begin) {
            return idx;
        } else {
            return -2;
        }
    }

    void createIndex() {
        final int size = size();
        bounds = new int[size];
        childs = new LinkedList<>();
        int curidx = 0;
        for (int i = 0; i < size; ++i) {
            curidx += get(i).form.length();
            bounds[i] = curidx;
            childs.add(new LinkedList<IndexWord>());
        }
        for (int i = 0; i < size; ++i) {
            IndexWord dw = get(i);
            if (dw.head >= 0) {
                childs.get(dw.head).add(dw);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); ++i) {
            if (i < size() - 1) {
                sb.append(get(i).toString()).append(' ');
            } else {
                sb.append(get(i).toString());
            }
        }
        return sb.toString();
    }

    public UnsegmentedSentence toUnsegmentedSentence(List<String> tags, List<Integer> indices, List<Boolean> bounds) {
        StringBuilder builder = new StringBuilder();

        int curidx = 0;
        for (int i = 0; i < size(); ++i) {
            IndexWord dw = get(i);
            builder.append(dw.form);
            if (bounds != null) {
                for (int j = 0; j < dw.form.length() - 1; ++j) {
                    bounds.add(false);
                }
                bounds.add(true);
            }
            if (indices != null) {
                curidx += dw.form.length();
                indices.add(curidx);
            }
            if (tags != null) {
                tags.add(dw.tag);
            }
        }

        return new UnsegmentedSentence(builder.toString().toCharArray());
    }
}
