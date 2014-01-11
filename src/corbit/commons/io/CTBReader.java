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
package corbit.commons.io;

import corbit.commons.dict.TagDictionary;
import corbit.commons.util.Statics;
import corbit.commons.word.ArcLabel;
import corbit.commons.word.ParsedSentence;
import corbit.commons.word.SentenceBuilder;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTBReader extends ParseReader {

    public CTBReader(String sFile, TagDictionary dict) {
        super(sFile, dict.generateTagSet(), new TreeSet<>(Arrays.asList(dict.getArcLabels())));
    }

    public ParsedSentence readFromString(String l) {
        String[] ss = l.split(" ");
        SentenceBuilder sb = new SentenceBuilder(m_posSet, m_labelSet);

        for (int i = 0; i < ss.length; ++i) {
            Pattern re = Pattern.compile("(.*?):\\((.*?)\\)_\\((.*?)\\)_\\((.*?)\\)(?:_\\((.*?)\\))?");
            Matcher mc = re.matcher(ss[i]);

            if (!mc.matches() || mc.groupCount() < 4) {
                return null;
            }

            int iIndex = Integer.parseInt(mc.group(1));
            int iHead = Integer.parseInt(mc.group(2));
            String sForm = Normalizer.normalize(mc.group(3), Normalizer.Form.NFKC);
            String sPos = mc.group(4);
            String sLabel = mc.groupCount() == 5 ? mc.group(5) : null;
            ArcLabel label = sLabel != null ? ArcLabel.getLabel(sLabel) : null;
            sb.addWord(iIndex, sForm, sPos, iHead, label);
        }

        return sb.compile();
    }

    @Override
    protected void iterate() 
            throws InterruptedException {
        int iSentence = 0;
        FileEnum fe = new FileEnum(m_sFile);

        try {
            for (String l : fe) {
                l = Statics.trimSpecial(l);
                if (l.length() == 0) {
                    continue;
                }
                ParsedSentence sent = readFromString(l);
                ++iSentence;

                if (sent == null) {
                    System.err.println(String.format("Error found at line %d. Skipping.", iSentence));
                    yieldBreak();
                } else {
                    yieldReturn(sent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fe.shutdown();
        }
    }
}