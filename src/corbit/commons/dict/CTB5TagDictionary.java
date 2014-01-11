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
package corbit.commons.dict;

import java.util.Set;
import java.util.TreeSet;

public class CTB5TagDictionary extends TagDictionary {

    private static final long serialVersionUID = 6692431815764780778L;
    public static final String[] ssCtbTags = {"AD", "AS", "BA", "CC", "CD", "CS",
        "DEC", "DEG", "DER", "DEV", "DT", "ETC", "FW", "IJ", "JJ", "LB",
        "LC", "M", "MSP", "NN", "NR", "NT", "OD", "ON", "P", "PN", "PU",
        "SB", "SP", "VA", "VC", "VE", "VV"};
    public static final String[] ssOpenTags = {"AD", "CD", "FW", "JJ", "M", "MSP", "NN", "NR", "NT", "OD", "VA", "VV"};
    public static final String[] ssClosedTags = {"AS", "BA", "CC", "CS", "DEC", "DEG",
        "DER", "DEV", "DT", "ETC", "IJ", "LB", "LC", "ON", "P", "PN", "PU",
        "SB", "SP", "VC", "VE"};
    public static final String[] arcLabels = {"AMOD", "DEP", "NMOD", "OBJ", "P", "PMOD", "PRD", "ROOT", "SBAR", "SUB", "VC", "VMOD"};

    public CTB5TagDictionary(boolean bUseClosedTags) {
        super(
                bUseClosedTags ? ssOpenTags : ssCtbTags,
                bUseClosedTags ? ssClosedTags : null,
                ssCtbTags,
                arcLabels);
    }

    @Override
    public String[] getArcLabels() {
        return arcLabels;
    }

    @Override
    public Set<String> generateTagSet() {
        Set<String> ss = new TreeSet<>();
        for (int i = 0; i < ssCtbTags.length; ++i) {
            ss.add(ssCtbTags[i]);
        }
        return ss;
    }
}