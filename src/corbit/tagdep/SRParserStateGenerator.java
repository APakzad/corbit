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
import corbit.commons.util.Statics;
import corbit.tagdep.handler.SRParserHandler;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class SRParserStateGenerator {

    private static final int m_szStack = 3;
    private SRParserHandler m_fhandler;
    private SRParserParameters m_params;

    public SRParserStateGenerator(SRParserHandler fhandler, SRParserParameters params) {
        m_fhandler = fhandler;
        m_params = params;
    }

    public void setFeatureHandler(SRParserHandler fhandler) {
        m_fhandler = fhandler;
    }

    public SRParserState create(DepTreeSentence sent) {
        SRParserState s = m_params.m_bDP
                ? new SRParserDPState(
                sent,
                new DepTree[m_szStack],
                0, -1, -1, 0.0d, 0.0d, 0.0d,
                new LinkedList<IntFeatVector>(),
                new IntFeatVector(),
                new TreeSet<SRParserState>(), null,
                new LinkedHashMap<SRParserState, Pair<IntFeatVector, Double>>(),
                new int[sent.size()],
                new String[sent.size()],
                new String[sent.size()],
                m_params.m_bEvalDelay ? new LinkedList<String>() : null,
                new LinkedList<PDAction>(),
                true, 1)
                : new SRParserState(
                sent,
                new DepTree[m_szStack],
                0, -1, -1, 0.0d, 0.0d, 0.0d,
                new LinkedList<IntFeatVector>(),
                new IntFeatVector(),
                new TreeSet<SRParserState>(), null,
                new LinkedHashMap<SRParserState, Pair<IntFeatVector, Double>>(),
                new int[sent.size()],
                new String[sent.size()],
                new String[sent.size()],
                m_params.m_bEvalDelay ? new LinkedList<String>() : null,
                new LinkedList<PDAction>(),
                true, 1);

        s.pstck[0] = new DepTree(s.sent, -1, "ROOT", "ROOT","ROOT", -2,"ROOT");
        for (int i = 0; i < sent.size(); ++i) {
            // s.pos[i] = sent.get(i).pos;
            s.heads[i] = sent.get(i).head;
            s.dependency[i] = sent.get(i).dependency;
        }
        s.calcAtomicFeatures(m_fhandler);
        return s;
    }

    public SRParserState copy(SRParserState s) {
        SRParserState sNew = m_params.m_bDP
                ? new SRParserDPState(
                s.sent,
                s.cloneStack(),
                s.curidx, s.idbgn, s.idend, s.scprf, s.scins, s.scdlt,
                s.fvins != null ? new LinkedList<>(s.fvins) : null,
                s.fvdlt != null ? new IntFeatVector(s.fvdlt) : null,
                new TreeSet<>(s.preds), s.pred0,
                new LinkedHashMap<>(s.trans),
                Arrays.copyOf(s.heads, s.heads.length),
                Arrays.copyOf(s.pos, s.pos.length),
                Arrays.copyOf(s.dependency, s.dependency.length),        
                m_params.m_bEvalDelay ? new LinkedList<>(s.fvdelay) : null,
                new LinkedList<>(s.lstact),
                s.gold,
                s.nstates)
                : new SRParserState(
                s.sent,
                s.cloneStack(),
                s.curidx, s.idbgn, s.idend, s.scprf, s.scins, s.scdlt,
                s.fvins != null ? new LinkedList<>(s.fvins) : null,
                s.fvdlt != null ? new IntFeatVector(s.fvdlt) : null,
                new TreeSet<>(s.preds), s.pred0,
                new LinkedHashMap<>(s.trans),
                Arrays.copyOf(s.heads, s.heads.length),
                Arrays.copyOf(s.pos, s.pos.length),
                Arrays.copyOf(s.dependency, s.dependency.length),
                m_params.m_bEvalDelay ? new LinkedList<>(s.fvdelay) : null,
                new LinkedList<>(s.lstact),
                s.gold,
                s.nstates);

        sNew.atoms = s.atoms;
        return sNew;
    }

    public SRParserState generate(
            DepTreeSentence sent,
            DepTree[] stack,
            int curidx,
            int idbgn,
            int idend,
            double scprf,
            double scins,
            double scdlt,
            List<IntFeatVector> fvins,
            IntFeatVector fvdlt,
            Set<SRParserState> preds,
            SRParserState pred0,
            Map<SRParserState, Pair<IntFeatVector, Double>> trans,
            int[] heads,
            String[] pos,
            String[] dependency,
            List<String> fvdelay,
            List<PDAction> lstact,
            boolean gold,
            long states) {
        SRParserState s = m_params.m_bDP
                ? new SRParserDPState(
                sent, stack,
                curidx, idbgn, idend, scprf, scins, scdlt,
                fvins, fvdlt, preds, pred0, trans, heads, pos,dependency, fvdelay,
                lstact, gold, states)
                : new SRParserState(
                sent, stack,
                curidx, idbgn, idend, scprf, scins, scdlt,
                fvins, fvdlt, preds, pred0, trans, heads, pos,dependency, fvdelay,
                lstact, gold, states);
        s.calcAtomicFeatures(m_fhandler);
        return s;
    }

    public SRParserState merge(SRParserState ps1, SRParserState ps2) {
        // if goldpos
        // Debug.Assert(ps1.pstck[0].Children.size() == 0 ||
        // Statics.SetEquals<SRParserState>(ps1.preds, ps2.preds));

        assert (ps1.atoms.equals(ps2.atoms));
        assert (new TreeSet<>(ps1.atoms.fvdelay)).equals(new TreeSet<>(ps2.atoms.fvdelay));

        if (SRParserState.isBetter(ps2, ps1)) {
            SRParserState _ps = ps1;
            ps1 = ps2;
            ps2 = _ps;
        }

        SRParserState ps = copy(ps1);

        for (SRParserState _ps : ps2.preds) {
            if (!ps.preds.contains(_ps)) {
                ps.preds.add(_ps);
            }
        }

        for (Entry<SRParserState, Pair<IntFeatVector, Double>> p : ps2.trans.entrySet()) {
            SRParserState sk = p.getKey();
            if (!ps.trans.containsKey(sk)) {
                ps.trans.put(sk, p.getValue());
            }
        }

        if (m_params.m_bStrictStop) {
            ps.gold = ps1.gold || ps2.gold && Statics.arrayEquals(ps1.heads, ps2.heads);
        } else {
            ps.gold = ps1.gold || ps2.gold;
        }

        ps.nstates = ps1.nstates + ps2.nstates;

        // Console.writeLine("Merged: ");
        // Console.writeLine(ps1.stackToString());
        // Console.writeLine(ps2.stackToString());

        return ps;
    }
}
