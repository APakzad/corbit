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
import corbit.tagdep.dict.CTBTagDictionary;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MaltReader extends ParseReader {

    String m_sFile;

    public MaltReader(String sFile) 
            throws FileNotFoundException, 
                   UnsupportedEncodingException {
        m_sFile = sFile;
    }

    @Override
    protected void iterate() 
            throws InterruptedException {
        int iSentence = 0;
        FileEnum fe = new FileEnum(m_sFile);
        Set<String> posSet = CTBTagDictionary.copyTagSet();

        try {
            while (fe.hasNext()) {
                List<String> l = new ArrayList<>();
                String sLine;
                while (!(sLine = fe.next()).equals("")) {
                    l.add(sLine);
                }

                ++iSentence;
                DepTreeSentence s = new DepTreeSentence();
                for (int i = 0; i < l.size(); ++i) {
                    s.add(new DepTree());
                }
                for (int i = 0; i < l.size(); ++i) {
                    String[] r = l.get(i).split("\t");
                    int iIndex = i;
                    int iHead = Integer.parseInt(r[2]) - 1;
                    String sForm = Normalizer.normalize(r[0], Normalizer.Form.NFKC);
                    String sPos = r[1];

                    if (!sPos.startsWith("-") && sPos.contains("-")) {
                        sPos = sPos.split("-")[0];
                    } else if (sPos.equals("-NONE-")) {
                        sPos = "NONE";
                    } else if (sPos.equals("PU/")) {
                        sPos = "PU";
                    }
                    if (!posSet.contains(sPos)) {
                        Console.writeLine("Unknown POS: " + sPos);
                    }

                    DepTree dw = s.get(i);
                    dw.sent = s;
                    dw.index = iIndex;
                    dw.form = sForm;
                    dw.pos = sPos;
                    dw.head = iHead;

                    if (iHead < -1 || iHead >= s.size() || iHead == i) {
                        Console.writeLine(String.format(
                                "Error found at line %d. Skipping.", iSentence));
                        s = null;
                        break;
                    }

                    if (iHead != -1) {
                        s.get(iHead).children.add(dw);
                    }
                }
                yieldReturn(s);
            }
        } finally {
            fe.shutdown();
        }
    }
}