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

import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ParseWriter {

    private PrintWriter sw;

    public ParseWriter(String sFile) 
            throws FileNotFoundException, 
                   UnsupportedEncodingException {
        sw = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(sFile), "UTF-8"));
    }

    public void writeParse(DepTreeSentence sent, DepTreeSentence gsent, 
                           boolean bGoldPos, boolean bGoldHead) {
        assert (sent.size() == gsent.size());
        for (int i = 0; i < sent.size(); ++i) {
            DepTree dt = sent.get(i);
            DepTree dg = gsent.get(i);
            sw.print(String.format("%d:(%d)_(%s)_(%s) ", i, 
                bGoldHead 
                    ? dg.head 
                    : dt.head, 
                dt.form, 
                bGoldPos 
                    ? dg.pos 
                    : dt.pos));
        }
        sw.println();
    }

    public void dispose() {
        sw.close();
        sw = null;
    }
}