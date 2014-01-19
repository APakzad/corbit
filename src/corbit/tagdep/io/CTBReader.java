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
package corbit.tagdep.io;

import corbit.commons.io.Console;
import corbit.commons.io.FileEnum;
import corbit.commons.util.Statics;
import corbit.tagdep.dict.CTBTagDictionary;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;
import java.text.Normalizer;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTBReader extends ParseReader {

    private final String m_sFile;

    public CTBReader(String sFile) {
        this.m_sFile = sFile;
    }

    @Override
    protected void iterate() throws InterruptedException {
        int iSentence = 0;
        FileEnum fe = new FileEnum(m_sFile);
        Set<String> posSet = CTBTagDictionary.copyTagSet();

        try {
            for (String l : fe) {
                l = Statics.trimSpecial(l);
                if (l.length() == 0) {
                    continue;
                }
                ++iSentence;
                DepTreeSentence s = new DepTreeSentence();
//                String[] ss = l.split(" ");
                String[] ss = l.split("\\) ");
                for (int i = 0; i < ss.length; ++i) {
                    s.add(new DepTree());
                }

                for (int i = 0; i < ss.length; ++i) {
//                    Pattern re = Pattern.compile("(.*?):\\((.*?)\\)_\\((.*?)\\)_\\((.*?)\\)");
                    Pattern re = Pattern.compile("(.*?):\\((.*?)\\)_\\((.*?)\\)_\\((.*?)\\)?");
                    Matcher mc = re.matcher(ss[i]);

                    if (!mc.matches() || mc.groupCount() < 4) {
                        Console.writeLine(String.format("Format error at line %d. Skipping.", iSentence));
                        s = null;
                        break;
                    }

                    String sForm = Normalizer.normalize(mc.group(3), Normalizer.Form.NFKC);
                    String sPos = mc.group(4);

                    if (!sPos.startsWith("-") && sPos.contains("-")) {
                        sPos = sPos.split("-")[0];
                    }
                    if (!sPos.startsWith("/") && sPos.contains("/")) {
                        sPos = sPos.split("/")[0];
                    }
                    // a noisy tag assigned to 'x', as in '130 x 130'
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
//                        break;
                    }

                    int iIndex = Integer.parseInt(mc.group(1));
                    int iHead = Integer.parseInt(mc.group(2));
                    DepTree dw = s.get(i);
                    dw.sent = s;
                    dw.index = iIndex;
//                    dw.form = sForm;
                    dw.form = sForm.replace(" ", "‌");
                    dw.pos = sPos;
                    dw.head = iHead;

                    // if (i != iIndex || iHead < -1 || iHead >= s.size() || iHead == i)
                    if (i != iIndex) {
                        Console.writeLine(String.format("Illegal index at line %d. Skipping.", iSentence));
                        s = null;
                        break;
                    }

                    if (iHead >= 0) {
                        s.get(iHead).children.add(dw);
                    }
                }
                yieldReturn(s);
            }
        } catch (NumberFormatException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            fe.shutdown();
        }
    }
}