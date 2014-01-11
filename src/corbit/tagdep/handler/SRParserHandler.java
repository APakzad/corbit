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
package corbit.tagdep.handler;

import corbit.commons.Vocab;
import corbit.commons.ml.IntFeatVector;
import corbit.commons.transition.PDAction;
import corbit.commons.util.Statics;
import corbit.tagdep.SRParserParameters;
import corbit.tagdep.SRParserState;
import corbit.tagdep.dict.TagDictionary;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepWord;
import java.util.List;
import java.util.Set;

public abstract class SRParserHandler {
    
    /*
     * parameters
     */
    protected Vocab m_vocab;
    protected Set<String> m_flist;
    protected TagDictionary m_dict;
    protected SRParserParameters m_params;
    protected static final String SEP = "-";
    protected static final String OOR = "$";
    protected static final String NA = "NA";

    /*
     * getter/setter and constructor
     */
    public Vocab getVocabulary() {
        return m_vocab;
    }

    public void setFeatureList(Set<String> v) {
        m_flist = v;
    }

    public void setTagDictionary(TagDictionary t) {
        m_dict = t;
    }

    public SRParserHandler(Vocab vocab, SRParserParameters params) {
        m_vocab = vocab;
        m_flist = null;
        m_dict = null;
        m_params = params;
    }

    /*
     * abstract section
     */
    public abstract AtomicFeatures getAtomicFeatures(SRParserState s);

    public abstract IntFeatVector getFeatures(SRParserState s, PDAction act, List<String> vd, boolean bAdd);

    /*
     * utility functions
     */
    protected static String getPosArgString(int idx) {
        return "@@@POS[" + idx + "]";
    }

    protected static String getPosArgPrefix() {
        return "@@@POS[";
    }

    protected void evaluateDelayedFeatures(IntFeatVector vn, List<String> vd, int curidx, String sPos, boolean bAdd) {
        String sArg = SRParserHandler.getPosArgString(curidx - 1);
        String sPref = SRParserHandler.getPosArgPrefix();
        for (int i = vd.size() - 1; i >= 0; --i) {
            String sTemplate = vd.get(i);
            assert (sTemplate.indexOf(sPref) >= 0);
            if (sTemplate.indexOf(sArg) != -1) {
                vd.remove(i);
            }
            while (sTemplate.indexOf(sArg) != -1) {
                sTemplate = Statics.strReplace(sTemplate, sArg, sPos);
            }
            if (sTemplate.indexOf(sPref) == -1) {
                addFeature(vn, sTemplate, 1.0d, bAdd);
            } else {
                vd.add(sTemplate);
            }
        }
    }

    protected static void addFeature(IntFeatVector v, String sFeature, double dValue, boolean bAdd, Vocab vocab) {
        if (bAdd) {
            v.put(vocab.getIndex(sFeature), dValue);
        } else {
            Integer idx = vocab.getBoxed(sFeature);
            if (idx != null) {
                v.put(idx, dValue);
            }
        }
    }

    protected static void addFeature(IntFeatVector v, String sFeature, String sLabel, double dValue, boolean bAdd, Vocab vocab) {
        addFeature(v, sFeature + SEP + sLabel, dValue, bAdd, vocab);
    }

    protected void addFeature(IntFeatVector v, String sFeature, double dValue, boolean bAdd) {
        addFeature(v, sFeature, dValue, bAdd, m_vocab);
    }

    protected void addFeature(IntFeatVector v, String sFeature, String sLabel, double dValue, boolean bAdd) {
        addFeature(v, sFeature, sLabel, dValue, bAdd, m_vocab);
    }

    protected static int[] getValencies(DepTree w) {
        int[] vals = new int[2];
        int iIdx = w.index;
        for (int i = 0; i < w.children.size(); ++i) {
            DepTree wc = w.children.get(i);
            if (wc.index < iIdx) {
                ++vals[0];
            } else {
                ++vals[1];
            }
        }
        return vals;
    }

    protected static int getDistance(DepWord w1, DepWord w2) {
        if (w1.index == -1 || w2.index == -1) {
            return -1;
        } else {
            return Math.abs(w2.index - w1.index);
        }
    }

    protected static DepTree getLeftChild(DepTree w) {
        int iCIdx = -1;
        int iIdx = w.index;

        for (int i = 0; i < w.children.size(); ++i) {
            DepTree wc = w.children.get(i);
            if (wc.index < iIdx && wc.index > iCIdx) {
                iCIdx = wc.index;
            }
        }

        return iCIdx == -1 ? null : w.sent.get(iCIdx);
    }

    protected static DepTree getRightChild(DepTree w) {
        int iCIdx = w.sent.size();
        int iIdx = w.index;

        for (int i = 0; i < w.children.size(); ++i) {
            DepTree wc = w.children.get(i);
            if (wc.index > iIdx && wc.index < iCIdx) {
                iCIdx = wc.index;
            }
        }

        return iCIdx == w.sent.size() ? null : w.sent.get(iCIdx);
    }

    protected static DepTree getLeftmostChild(DepTree w) {
        final int szSent = w.sent.size();
        int cidx = szSent;

        for (int i = 0; i < w.children.size(); ++i) {
            int idx = w.children.get(i).index;
            if (idx < cidx && idx < w.index) {
                cidx = idx;
            }
        }

        return cidx == szSent ? null : w.sent.get(cidx);
    }

    protected static DepTree getRightmostChild(DepTree w) {
        int cidx = -1;

        for (int i = 0; i < w.children.size(); ++i) {
            int idx = w.children.get(i).index;
            if (idx > cidx && idx > w.index) {
                cidx = idx;
            }
        }

        return cidx == -1 ? null : w.sent.get(cidx);
    }

    protected static DepTree[] getTwoLeftmostChild(DepTree w) {
        final int szSent = w.sent.size();
        int cidx1 = szSent;
        int cidx2 = szSent;
        for (int i = 0; i < w.children.size(); ++i) {
            int idx = w.children.get(i).index;
            if (idx < cidx1 && idx < w.index) {
                cidx2 = cidx1;
                cidx1 = idx;
            }
        }
        DepTree[] out = new DepTree[2];
        out[0] = cidx1 == szSent ? null : w.sent.get(cidx1);
        out[1] = cidx2 == szSent ? null : w.sent.get(cidx2);
        return out;
    }

    protected static DepTree[] getTwoRightmostChild(DepTree w) {
        int cidx1 = -1;
        int cidx2 = -1;
        for (int i = 0; i < w.children.size(); ++i) {
            int idx = w.children.get(i).index;
            if (idx > cidx1 && idx > w.index) {
                cidx2 = cidx1;
                cidx1 = idx;
            }
        }
        DepTree[] out = new DepTree[2];
        out[0] = cidx1 == -1 ? null : w.sent.get(cidx1);
        out[1] = cidx2 == -1 ? null : w.sent.get(cidx2);
        return out;
    }

    protected static String getPunctInBetween(List<DepTree> s, int iStart, int iEnd) {
        if (iStart > iEnd) {
            int i = iStart;
            iStart = iEnd;
            iEnd = i;
        }
        for (int i = iStart + 1; i < iEnd; ++i) {
            if ("PU".equals(s.get(i).pos)) {
                return s.get(i).form;
            }
        }
        return NA;
    }
}