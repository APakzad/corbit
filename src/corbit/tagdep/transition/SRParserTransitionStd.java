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
package corbit.tagdep.transition;

import corbit.commons.io.Console;
import corbit.commons.ml.IntFeatVector;
import corbit.commons.ml.WeightVector;
import corbit.commons.transition.PDAction;
import corbit.commons.util.Pair;
import corbit.tagdep.SRParserState;
import corbit.tagdep.SRParserStateGenerator;
import corbit.tagdep.dict.TagDictionary;
import corbit.tagdep.handler.SRParserHandler;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SRParserTransitionStd extends SRParserTransition {

    public boolean m_bAssignPosFollowsShift;
    boolean m_bAssignGoldPos;
    boolean m_bIndepSPActions;
    int m_iLookaheadDepth = 0;
    
    public String DepTag=null;

    public SRParserTransitionStd(SRParserStateGenerator sg, SRParserHandler fh, WeightVector w, TagDictionary d,
            boolean bParse, boolean bReduceFollowsShift, boolean bAssignGoldPos, boolean bIndepSPActions) {
        super(sg, fh, w, d, bParse);
        this.m_bAssignPosFollowsShift = bReduceFollowsShift;
        this.m_bAssignGoldPos = bAssignGoldPos;
        this.m_bIndepSPActions = bIndepSPActions;
    }

    @Override
    public Pair<PDAction, SRParserState> moveNextGold(SRParserState s, DepTreeSentence gsent, boolean bAdd) {
        PDAction act = getGoldAction(s, gsent);
        List<SRParserState> ls = moveNext(s, gsent, act, true, bAdd);
        SRParserState sGold = null;

        for (SRParserState sNext : ls) {
            assert (!sNext.gold || sGold == null);
            if (sNext.gold) {
                sGold = sNext;
            }
        }

        // might not be true if pruning is disabled, because states are sorted during pruning process
        assert (sGold != null && sGold == ls.get(0));

        return new Pair<>(act, sGold);
    }

    @Override
    public List<Pair<PDAction, SRParserState>> moveNext(SRParserState s, DepTreeSentence gsent, boolean bAdd) {
        List<Pair<PDAction, SRParserState>> l = new LinkedList<>();
        PDAction goldAct = gsent != null ? getGoldAction(s, gsent) : PDAction.NOT_AVAILABLE;

        for (PDAction act : getNextActions(s, m_bAssignGoldPos ? gsent : null)) {
            if( act != null){
                for (SRParserState sNext : moveNext(s, gsent, act, act.shallowEquals(goldAct), bAdd)) // TODO: shallow unnecessary
                {
                    l.add(new Pair<>(act, sNext));
                }
            }
            
        }
        return l;
    }

    PDAction getGoldAction(SRParserState s, DepTreeSentence gsent) {
        if (s.gold == false) {
            return PDAction.NOT_AVAILABLE;
        }
//		if (!s.gold) throw new IllegalArgumentException("Cannot get the gold action for non-gold state.");

        if (isEnd(s)) {
            return PDAction.END_STATE;
        }

        DepTree ws0 = s.pstck[0];
        DepTree ws1 = s.pstck[1];

        if (!m_bAssignPosFollowsShift
                && ws0.index != -1
                && s.pos[ws0.index] == null) // if m_bUseGoldPos == true, this pos is already assigned and shift action is omitted.
        {
            return PDAction.getPosAction(gsent.get(ws0.index).pos);
        } else {
            if (m_bParse && ws1 != null) {
                if ((ws1.index != -1 || s.curidx == s.sent.size())
                        && gsent.get(ws0.index).head == ws1.index
                        && gsent.get(ws0.index).children.size() == ws0.children.size()) {
                    
                    DepTag = gsent.get(ws0.index).dependency;//افزوده شده
                    
                    return PDAction.getRRAction(DepTag);// تغییر داده شده
                    
                } else if (ws1.index != -1 && ws0.index == gsent.get(ws1.index).head) {
                    
                    DepTag = gsent.get(ws1.index).dependency;// افزوده شده
                    
                    return PDAction.getRLAction(DepTag);
                }
            }

            if (s.curidx == s.sent.size()) {
                //Console.writeLine("No gold action found: " + s);
                return PDAction.NOT_AVAILABLE;
            }

            if (m_bAssignPosFollowsShift) {
                return PDAction.getShiftPosAction(gsent.get(s.curidx).pos);
            } else {
                return PDAction.SHIFT;
            }
        }
    }

    List<PDAction> getNextActions(SRParserState s, DepTreeSentence gsent) {
        assert (m_bAssignGoldPos || gsent == null);

        List<PDAction> l = new LinkedList<>();
        
        if (isEnd(s)) {
            l.add(PDAction.END_STATE);
            return l;
        }

        DepTree ws0 = s.pstck[0];
        DepTree ws1 = s.pstck[1];
        String str=null;
        if (!m_bAssignPosFollowsShift
                && ws0.index != -1
                && s.pos[ws0.index] == null) // if m_bUseGoldPos == true, this pos is already assigned and shift action is omitted.
        {
            if (m_bAssignGoldPos) {
                l.add(PDAction.getPosAction(gsent.get(ws0.index).pos));
            } else {
                for (String sPos : m_dict.getTagCandidates(ws0.form)) {
                    l.add(PDAction.getPosAction(sPos));
                }
            }
        } else {
            if (s.curidx < s.sent.size()) {
                if (m_bAssignPosFollowsShift) {
                    if (m_bAssignGoldPos) {
                        l.add(PDAction.getShiftPosAction(gsent.get(s.curidx).pos));
                    } else {
                        str= s.sent.get(s.curidx).form; 
                        //System.out.println("form1:"+str);
                        for (String spqf1 : m_dict.getTagCandidates(str)) {
                            l.add(PDAction.getShiftPosAction(spqf1));
                        }
                    }
                } else {
                    l.add(PDAction.SHIFT);
                }
            }
            //if(s.sent.size() > s.curidx){
            if (m_bParse && ws1 != null ) {
                System.out.println("form:"+ws0.form);
                String tmp[] = m_dict.getDepTagCandidates(ws0);
                String depTag=null;
                for(int count=0; count<tmp.length; count++ ){
                    depTag=tmp[count];
                    //l.add(PDAction.getRLAction(depTag));
                    l.add(PDAction.getRRAction(depTag));
                }
                
                //*******************************************
                for(SRParserState p : s.preds){
                    if(p.pstck[0].index == -1){
                        continue;
                    }
                    System.out.println("form:"+p.pstck[0].form);
                    tmp = m_dict.getDepTagCandidates(p.pstck[0]);
                    depTag = null;
                     for(int count=0; count<tmp.length; count++ ){
                    depTag=tmp[count];
                    //l.add(PDAction.getRLAction(depTag));
                    l.add(PDAction.getRLAction(depTag));
                    }
                    
                }
            }
           //}
           
        }
        
        assert (!l.contains(null));
        return l;
    }

    @Override
    public void clear() {
//		m_trans.clear();
    }

    List<SRParserState> moveNext(SRParserState s, DepTreeSentence gsent, PDAction act, boolean isGoldAct, boolean bAdd) {
        List<SRParserState> l;
        String depTag = null;
        
        if (act.isReduceRight()) {
            depTag=act.toString().substring(act.toString().indexOf("-")+1);
            l = reduceRight(s, depTag, isGoldAct, bAdd);
        } else if (act.isReduceLeft()) {
            depTag=act.toString().substring(act.toString().indexOf("-")+1);
            l = reduceLeft(s, depTag, isGoldAct, bAdd);
        } else {
            l = new LinkedList<>();
            if (act == PDAction.END_STATE || act == PDAction.PENDING) {
                l.add(s);
            } else if (act == PDAction.SHIFT) {
                l.add(shift(s, isGoldAct, bAdd));
            } else if (act.isPosAction()) {
                l.add(assignPos(s, act.getPos(), isGoldAct, bAdd));
            } else if (act.isShiftPosAction()) {
                String sPos = act.getPos();
                SRParserState ss = m_bIndepSPActions
                        ? shiftWithPos(s, sPos, isGoldAct, bAdd)
                        : assignPos(shift(s, isGoldAct, bAdd), sPos, isGoldAct, bAdd);
                l.add(ss);
            }
        }
        return l;
    }

    SRParserState shiftWithPos(SRParserState s, String sPos, boolean bGoldAct, boolean bAdd) {
        assert (!(s.curidx == s.sent.size() || s.pstck[0].index != -1 && s.pos[s.pstck[0].index] == null));

        List<String> _fvdelay = s.fvdelay != null ? new LinkedList<>(s.fvdelay) : null;
        IntFeatVector vs = m_fhandler.getFeatures(s, PDAction.getShiftPosAction(sPos), _fvdelay, bAdd);
        double scdlt = m_weight.score(vs);
        if (!bAdd) {
            vs = null;
        }
        List<IntFeatVector> _fvins = bAdd ? new LinkedList<IntFeatVector>() : null;
        double _scprf = s.scprf + scdlt;
        double _scins = 0.0d;

        DepTree[] _pstck = s.pushStack(new DepTree(s.sent.get(s.curidx)));
        Set<SRParserState> _preds = new HashSet<>();
        _preds.add(s);
        Map<SRParserState, Pair<IntFeatVector, Double>> _trans = new LinkedHashMap<>();
        _trans.put(s, new Pair<>(vs, scdlt));
        int[] _heads = new int[s.sent.size()];
        Arrays.fill(_heads, -2);
        String[] _pos = new String[s.sent.size()];
        _pos[_pstck[0].index] = sPos;
        _pstck[0].pos = sPos;

        List<PDAction> _lstact = new LinkedList<>();
        _lstact.add(PDAction.getShiftPosAction(sPos));

        return m_generator.generate(s.sent, _pstck, s.curidx + 1, s.idend + 1, s.idend + 1,
                _scprf, _scins, scdlt, _fvins, vs, _preds, s, _trans, _heads, _pos,s.dependency, _fvdelay,
                _lstact, s.gold && bGoldAct, s.nstates);
    }

    SRParserState shift(SRParserState s, boolean bGoldAct, boolean bAdd) {
        assert (!(s.curidx == s.sent.size() || s.pstck[0].index != -1 && s.pos[s.pstck[0].index] == null));

        List<String> _fvdelay = s.fvdelay != null ? new LinkedList<>(s.fvdelay) : null;
        IntFeatVector vs = m_fhandler.getFeatures(s, PDAction.SHIFT, _fvdelay, bAdd);
        double scdlt = m_weight.score(vs);
        if (!bAdd) {
            vs = null;
        }
        List<IntFeatVector> _fvins = bAdd ? new LinkedList<IntFeatVector>() : null;
        double _scprf = s.scprf + scdlt;
        double _scins = 0.0;

        DepTree[] _pstck = s.pushStack(new DepTree(s.sent.get(s.curidx)));
        Set<SRParserState> _preds = new HashSet<>();
        _preds.add(s);
        Map<SRParserState, Pair<IntFeatVector, Double>> _trans = new LinkedHashMap<>();
        _trans.put(s, new Pair<>(vs, scdlt));
        int[] _heads = new int[s.sent.size()];
        Arrays.fill(_heads, -2);
        String[] _pos = new String[s.sent.size()];

        // remove the POS information if already defined (possibly with --gold-pos option)
        // if (_pstck[0].pos != null) _pstck[0].pos = null;
        if (_pstck[0].pos != null) {
            _pos[s.curidx] = _pstck[0].pos;
        }

        List<PDAction> _lstact = new LinkedList<>();
        _lstact.add(PDAction.SHIFT);

        return m_generator.generate(s.sent, _pstck, s.curidx + 1, s.idend + 1, s.idend + 1,
                _scprf, _scins, scdlt, _fvins, vs, _preds, s, _trans, _heads, _pos,s.dependency, _fvdelay,
                _lstact, s.gold && bGoldAct, s.nstates);
    }

    SRParserState assignPos(SRParserState s, String sPos, boolean bGoldAct, boolean bAdd) {
        assert (s.pstck[0].index == -1 || s.pos[s.pstck[0].index] == null);

        // update feature vector and scores

        List<String> _fvdelay = s.fvdelay != null ? new LinkedList<>(s.fvdelay) : null;
        IntFeatVector vr = m_fhandler.getFeatures(s, PDAction.getPosAction(sPos), _fvdelay, bAdd);
        double scdlt = m_weight.score(vr);
        if (!bAdd) {
            vr = null;
        }
        List<IntFeatVector> _fvins = bAdd ? new LinkedList<>(s.fvins) : null;
        if (bAdd) {
            _fvins.add(vr);
        }
        double _scprf = s.scprf + scdlt;
        double _scins = s.scins + scdlt;

        // update pos
        String[] _pos = Arrays.copyOf(s.pos, s.pos.length);
        _pos[s.pstck[0].index] = sPos;

        DepTree[] _pstck = s.cloneStack();
        _pstck[0].pos = sPos;

        List<PDAction> _lstact = new LinkedList<>(s.lstact);
        _lstact.add(PDAction.getPosAction(sPos));

        return m_generator.generate(s.sent, _pstck, s.curidx, s.idbgn, s.idend,
                _scprf, _scins, scdlt, _fvins, vr, s.preds, s.pred0, s.trans, s.heads, _pos,s.dependency, _fvdelay,
                _lstact, s.gold && bGoldAct, s.nstates);
    }

    List<SRParserState> reduceRight(SRParserState s,String DepTag, boolean bGoldAct, boolean bAdd) {
        List<SRParserState> l = new LinkedList<>();
        if (s.pstck[0].index != -1 && s.pos[s.pstck[0].index] == null) {
            return l;
        }

        List<String> _fvdelay = s.fvdelay != null ? new LinkedList<>(s.fvdelay) : null;
        IntFeatVector vr = m_fhandler.getFeatures(s, PDAction.getRRAction(DepTag), _fvdelay, bAdd);
        double sr = m_weight.score(vr);
        if (!bAdd) {
            vr = null;
        }

        for (SRParserState p : s.preds) {
            if (p.pstck[0].index == -1 && s.curidx < s.sent.size()) {
                continue;
            }

            assert (s.trans.containsKey(p));
            Pair<IntFeatVector, Double> t = s.trans.get(p);

            double scdlt = t.second + sr;

            List<IntFeatVector> _fvins = null;
            if (bAdd) {
                _fvins = new LinkedList<>(s.fvins);
                _fvins.addAll(p.fvins);
                _fvins.add(t.first);
                _fvins.add(vr);
            }
            double _scprf = p.scprf + s.scins + scdlt;
            double _scins = p.scins + s.scins + scdlt;

            DepTree[] _pstck = p.cloneStack();
            DepTree h = new DepTree(p.pstck[0]);
            DepTree c = new DepTree(s.pstck[0]);
            _pstck[0] = h;
            
            h.children.add(c);
            c.head = h.index;
           
            
            int[] _heads = Arrays.copyOf(s.heads, s.heads.length);
            for (int i = 0; i < p.heads.length; ++i) {
                if (p.heads[i] != -2) {
                    _heads[i] = p.heads[i];
                }
            }
            _heads[c.index] = h.index;

            String[] _pos = Arrays.copyOf(s.pos, s.pos.length);
            for (int i = 0; i < p.pos.length; ++i) {
                if (p.pos[i] != null) {
                    _pos[i] = p.pos[i];
                }
            }
            
            String[] _dep = new String[s.sent.size()];
            _dep[c.index] =DepTag;
            c.dependency = DepTag;
             
            List<PDAction> _lstact = new LinkedList<>(p.lstact);
            _lstact.addAll(s.lstact);
            _lstact.add(PDAction.getRRAction(DepTag));
            
            m_generator.generate(s.sent, _pstck, s.curidx, Math.max(p.idbgn, 0), s.idend,
                    _scprf, _scins, scdlt, _fvins, vr, p.preds, p.pred0, p.trans, _heads, _pos,_dep, _fvdelay,
                    _lstact, s.gold && p.gold && bGoldAct, s.nstates);
            
            l.add(m_generator.generate(s.sent, _pstck, s.curidx, Math.max(p.idbgn, 0), s.idend,
                    _scprf, _scins, scdlt, _fvins, vr, p.preds, p.pred0, p.trans, _heads, _pos,_dep, _fvdelay,
                    _lstact, s.gold && p.gold && bGoldAct, s.nstates));
        }
        return l;
    }

    List<SRParserState> reduceLeft(SRParserState s,String DepTag, boolean bGoldAct, boolean bAdd) {
        List<SRParserState> l = new LinkedList<>();
        if (s.pstck[0].index != -1 && s.pos[s.pstck[0].index] == null) {
            return l;
        }

        List<String> _fvdelay = s.fvdelay != null ? new LinkedList<>(s.fvdelay) : null;
        IntFeatVector vr = m_fhandler.getFeatures(s, PDAction.getRLAction(DepTag), _fvdelay, bAdd);
        double sr = m_weight.score(vr);
        if (!bAdd) {
            vr = null;
        }

        for (SRParserState p : s.preds) {
            if (p.pstck[0].index == -1) {
                continue;
            }

            Pair<IntFeatVector, Double> t = s.trans.get(p);

            double scdlt = t.second + sr;
            List<IntFeatVector> _fvins = null;
            if (bAdd) {
                _fvins = new LinkedList<>(s.fvins);
                _fvins.addAll(p.fvins);
                _fvins.add(t.first);
                _fvins.add(vr);
            }
            double _scprf = p.scprf + s.scins + scdlt;
            double _scins = p.scins + s.scins + scdlt;

            DepTree[] _pstck = p.cloneStack();
            DepTree h = new DepTree(s.pstck[0]);
            DepTree c = new DepTree(p.pstck[0]);
            _pstck[0] = h;

           
            h.children.add(c);
            c.head = h.index;

            int[] _heads = Arrays.copyOf(s.heads, s.heads.length);
            for (int i = 0; i < p.heads.length; ++i) {
                if (p.heads[i] != -2) {
                    _heads[i] = p.heads[i];
                }
            }
            _heads[c.index] = h.index;

            String[] _pos = Arrays.copyOf(s.pos, s.pos.length);
            for (int i = 0; i < p.pos.length; ++i) {
                if (p.pos[i] != null) {
                    _pos[i] = p.pos[i];
                }
            }
            
            String[] _dep = new String[s.sent.size()];
            _dep[c.index] =DepTag;
            c.dependency = DepTag;
            
            List<PDAction> _lstact = new LinkedList<>(p.lstact);
            _lstact.addAll(s.lstact);
            _lstact.add(PDAction.getRLAction(DepTag));
            
            m_generator.generate(s.sent, _pstck, s.curidx, Math.max(p.idbgn, 0), s.idend,
                    _scprf, _scins, scdlt, _fvins, vr, p.preds, p.pred0, p.trans, _heads, _pos,_dep, _fvdelay,
                    _lstact, s.gold && p.gold && bGoldAct, s.nstates);
                    
            l.add(m_generator.generate(s.sent, _pstck, s.curidx, Math.max(p.idbgn, 0), s.idend,
                    _scprf, _scins, scdlt, _fvins, vr, p.preds, p.pred0, p.trans, _heads, _pos,_dep, _fvdelay,
                    _lstact, s.gold && p.gold && bGoldAct, s.nstates));
        }
        return l;
    }

    @Override
    public IntFeatVector getPrefixFeatures(SRParserState s, DepTreeSentence gsent) {
        // if (s.fvprf != null) return s.fvprf; // cache
        //
        // IntFeatVector fvprf1 = new IntFeatVector();
        // SRParserState s1 = s;
        // for (SRParserState s2 = s1; s2 != null; s2 = s2.preds.size() > 0 ? s2.pred0 : null)
        // {
        // for (IntFeatVector v : s2.fvins)
        // fvprf1.append(v);
        // if (s2 != s1)
        // fvprf1.append(s1.trans.get(s2).key);
        // s1 = s2;
        // }

        IntFeatVector fvprf2 = new IntFeatVector();
        List<PDAction> lAct = s.getActionSequence();
        SRParserState s3 = m_generator.create(s.sent);
        for (PDAction act : lAct) {
            moveNext(s3, gsent, act, false, true);
            s3 = moveNext(s3, gsent, act, false, true).get(0);
        }
        SRParserState sss1 = s3;
        for (SRParserState sss2 = sss1; sss2 != null; sss2 = sss2.pred0) {
            for (IntFeatVector v : sss2.fvins) {
                fvprf2.append(v);
            }
            if (sss2 != sss1) {
                fvprf2.append(sss1.trans.get(sss2).first);
            }
            sss1 = sss2;
        }

        // assert (fvprf1.equals(fvprf2));

        return fvprf2;
        // return fvprf1;
    }

    public boolean isEnd(SRParserState s) {
        return s.curidx == s.sent.size()
                && (m_bParse && s.pstck[1] == null || !m_bParse && s.pos[s.pstck[0].index] != null);
    }

    public static boolean isJustShifted(SRParserState s) {
        return s.fvins.isEmpty();
    }
}
