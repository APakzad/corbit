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

import corbit.commons.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SentenceBuilder {

    ParsedSentence sent = new ParsedSentence();
    StringBuilder sb = new StringBuilder();
    List<Integer> lBounds = new ArrayList<>();
    int curidx = 0;
    final Set<String> posSet;
    final Set<String> labelSet;

    public SentenceBuilder(Set<String> posSet, Set<String> labelSet) {
        this.posSet = posSet;
        this.labelSet = labelSet;
    }

    public SentenceBuilder() {
        this.posSet = null;
        this.labelSet = null;
    }

    /**
     * This method is based on the Penn Treebank style POS tag set, and should
     * be modified if you want to another tag set.
     *
     * @param iIndex
     * @param sForm
     * @param sPos
     * @param iHead
     * @param label
     */
    public void addWord(int iIndex, String sForm, String sPos, int iHead, ArcLabel label) {
        if (lBounds.size() != iIndex || iIndex == iHead) {
            throw new IllegalArgumentException();
        }

        if (posSet != null) {
            if (!sPos.startsWith("-") && sPos.contains("-")) {
                sPos = sPos.split("-")[0];
            }
            if (!sPos.startsWith("/") && sPos.contains("/")) {
                sPos = sPos.split("/")[0];
            }
            switch (sPos) {
                case "X":
                    sPos = "M";
                    break;
                case "NP":
                    sPos = "NN";
                    break;
                case "VP":
                    sPos = "PU";
                    break;
            }

            if (!posSet.contains(sPos)) {
                Console.writeLine("Unknown POS: " + sPos);
                if (sPos.contains("-")) {
                    sPos = sPos.split("-")[0];
                } else if (sPos.contains("/")) {
                    sPos = sPos.split("/")[0];
                } else {
                    sPos = "NONE";
                }
            }
        }

        if (labelSet != null && label != null && !labelSet.contains(label.toString())) {
            Console.writeLine("Unknown arc label: " + label);
        }

        sent.add(new IndexWord(sent, lBounds.size(), iHead, sForm, sPos, label));
        sb.append(sForm);

        curidx += sForm.length();
        lBounds.add(curidx);
    }

    public ParsedSentence compile() {
        sent.sequence = new UnsegmentedSentence(sb.toString().toCharArray());
        sent.createIndex();
        return sent;
    }

    public static ParsedSentence chunkedToParsedSentence(ChunkedSentence sent) {
        return chunkedToParsedSentence(sent);
    }

    public static ParsedSentence chunkedToParsedSentence(List<DepChunk> chunks, UnsegmentedSentence sent) {
        ParsedSentence s = new ParsedSentence();
        Map<Span, Integer> indices = new HashMap<>();
        for (int i = 0; i < chunks.size(); ++i) {
            DepChunk c = chunks.get(i);
            s.add(new IndexWord(s, i, -2, c.form, c.tag, c.arcLabel));
            indices.put(new Span(c.begin, c.end), i);
        }
        for (int i = 0; i < chunks.size(); ++i) {
            DepChunk c = chunks.get(i);
            s.get(i).head = c.headBegin >= 0 ? indices.get(new Span(c.headBegin, c.headEnd)) : c.headBegin;
        }
        assert (chunks.size() > 0);
        s.sequence = sent;
        s.createIndex();
        return s;
    }

    public static ChunkedSentence parsedToChunkedSentence(ParsedSentence s) {
        ChunkedSentence chunks = new ChunkedSentence(s.sequence);
        int iBegin = 0;
        for (int i = 0; i < s.size(); ++i) {
            int iEnd = s.bounds[i];
            int iHead = s.get(i).head;
            int iHeadBegin = iHead == -1 ? iHead : iHead == 0 ? 0 : s.bounds[iHead - 1];
            int iHeadEnd = iHead == -1 ? iHead : s.bounds[iHead];
            ArcLabel label = s.get(i).arclabel;
            chunks.add(new DepChunk(s.sequence, iBegin, iEnd, s.get(i).tag, iHeadBegin, iHeadEnd, label));
            iBegin = iEnd;
        }
        return chunks;
    }
}