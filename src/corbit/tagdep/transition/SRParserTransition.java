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

import corbit.commons.ml.IntFeatVector;
import corbit.commons.ml.WeightVector;
import corbit.commons.transition.PDAction;
import corbit.commons.util.Pair;
import corbit.tagdep.SRParserState;
import corbit.tagdep.SRParserStateGenerator;
import corbit.tagdep.dict.TagDictionary;
import corbit.tagdep.handler.SRParserHandler;
import corbit.tagdep.word.DepTreeSentence;
import java.util.List;

public abstract class SRParserTransition {

    protected boolean m_bParse;
    protected boolean m_bDP;
    protected SRParserStateGenerator m_generator;
    protected SRParserHandler m_fhandler;
    protected WeightVector m_weight;
    protected TagDictionary m_dict;

    protected SRParserTransition(SRParserStateGenerator sg, SRParserHandler fh, WeightVector w, TagDictionary d, boolean bParse) {
        m_generator = sg;
        m_fhandler = fh;
        m_weight = w;
        m_dict = d;
        m_bParse = bParse;
    }

    public void setParse(boolean b) {
        m_bParse = b;
    }

    public abstract List<Pair<PDAction, SRParserState>> moveNext(SRParserState s, DepTreeSentence gsent, boolean bAdd);

    public abstract Pair<PDAction, SRParserState> moveNextGold(SRParserState s, DepTreeSentence gsent, boolean bAdd);

    public abstract IntFeatVector getPrefixFeatures(SRParserState s);

    public abstract void clear();

    class Transition extends Pair<PDAction, SRParserState> {

        public Transition(PDAction act, SRParserState s) {
            super(act, s);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Pair)) {
                return false;
            }
            @SuppressWarnings("rawtypes")
            Pair p = (Pair) obj;
            if (!(p.first instanceof PDAction) || !(p.second instanceof SRParserState)) {
                return false;
            }
            PDAction act = (PDAction) p.first;
            SRParserState s = (SRParserState) p.second;
            return this.first.equals(act) && this.second == s;
        }

        @Override
        public int hashCode() {
            return this.first.hashCode() * 17 + this.second.hashCode() + 7;
        }
    }
}