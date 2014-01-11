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

import corbit.commons.util.Statics;
import java.util.Set;
import java.util.TreeSet;

public abstract class AtomicFeatures {

    public final TreeSet<String> fvdelay;
    protected final int numFeatures;
    protected String[] features;
    protected int hash;

    protected AtomicFeatures(int n, TreeSet<String> _fvdelay) {
        numFeatures = n;
        features = new String[n];
        hash = 0;
        fvdelay = _fvdelay;
    }

    public String get(int n) {
        return features[n];
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // check the equality of the type
        if (obj == null || !(obj instanceof AtomicFeatures)) {
            return false;
        }
        AtomicFeatures atoms = (AtomicFeatures) obj;

        // return hash == atoms.hash;

        // check the equality of features
        for (int i = 0; i < features.length; ++i) {
            String s1 = features[i];
            String s2 = atoms.features[i];
            if ((s1 == null ^ s1 == null) || s1 != null && !s1.equals(s2)) {
                return false;
            }
        }
        // check the equality of delayed features
        Set<String> fvdelay2 = atoms.fvdelay;
        if (fvdelay != null) {
            if (fvdelay == null) {
                return false;
            }
            return (Statics.setEquals(fvdelay, fvdelay2));
        } else if (fvdelay2 != null) {
            return false;
        }
        return true;
    }

    protected void setHash() {
        hash = hash();
    }

    protected int hash() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < features.length; ++i) {
            sb.append(features[i]);
            sb.append(' ');
        }
        if (fvdelay != null) {
            for (String s : fvdelay) {
                sb.append(s);
                sb.append(' ');
            }
        }
        return sb.toString().hashCode();
    }
}