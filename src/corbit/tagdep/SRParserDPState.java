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
package corbit.tagdep;

import corbit.commons.ml.IntFeatVector;
import corbit.commons.transition.PDAction;
import corbit.commons.util.Pair;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SRParserDPState extends SRParserState {

    public SRParserDPState(DepTreeSentence sent, DepTree[] stack, int curidx, int idbgn, int idend, double scprf, double scins, double scdlt, List<IntFeatVector> fvins, IntFeatVector fvdlt,
            Set<SRParserState> preds, SRParserState pred0, Map<SRParserState, Pair<IntFeatVector, Double>> trans, int[] heads, String[] pos,String[] dependency, List<String> fvdelay, List<PDAction> lstact, boolean gold,
            long states) {
        super(sent, stack, curidx, idbgn, idend, scprf, scins, scdlt, fvins, fvdlt, preds, pred0, trans, heads, pos, dependency, fvdelay, lstact, gold, states);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SRParserState)) {
            return false;
        }
        return this.atoms.equals(((SRParserState) obj).atoms);
    }

    @Override
    public int hashCode() {
        return atoms.hashCode();
    }
}
