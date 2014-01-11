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
package corbit.commons.word;

import corbit.commons.util.Statics;
import java.util.Arrays;

public class UnsegmentedSentence {

    int hash = 0;
    final char[] chars;

    public UnsegmentedSentence(char[] _chars) {
        chars = _chars;
    }

    public int length() {
        return chars.length;
    }

    public String substring(int begin, int end) {
        return new String(chars, begin, end - begin);
    }

    public String substringWithPadding(int begin, int end) {
        String s = "";
        if (begin < 0) {
            s += Statics.charMultiplyBy('$', -begin);
        }
        begin = Math.max(0, begin);
        s += new String(chars, begin, Math.min(chars.length, end) - begin);
        if (end > chars.length) {
            s += Statics.charMultiplyBy('$', end - chars.length);
        }
        return s;
    }

    public char charAt(int index) {
        return chars[index];
    }

    public char what(int index) {
        return charAtIgnoreRange(index);
    }

    public char charAtIgnoreRange(int index) {
        if (index >= 0 && index < chars.length) {
            return chars[index];
        } else {
            return '$';
        }
    }

    public int charTypeAt(int index) {
        if (index >= 0 && index < chars.length) {
            return Statics.getCharType(chars[index]);
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return new String(chars);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UnsegmentedSentence)) {
            return false;
        } else {
            return Arrays.equals(chars, ((UnsegmentedSentence) obj).chars);
        }
    }

    @Override
    public int hashCode() {
        if (hash != 0) {
            return hash;
        } else {
            return (hash = Arrays.hashCode(chars));
        }
    }
}