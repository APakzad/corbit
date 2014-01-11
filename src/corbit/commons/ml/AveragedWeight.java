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
package corbit.commons.ml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AveragedWeight extends WeightVector {

    WeightVector wa = new WeightVector();
    int iStep = 0;

    public AveragedWeight(AveragedWeight v) {
        super(v);
        wa = new WeightVector(v.wa);
        iStep = v.iStep;
    }

    public void nextStep() {
        ++iStep;
    }

    public int getStep() {
        return iStep;
    }

    public WeightVector getAverageWeight() {
        return wa;
    }

    @Override
    public void append(IntFeatVector v) {
        super.append(v);
        wa.append(IntFeatVector.multiply(v, (double) iStep));
    }

    @Override
    public void subtract(IntFeatVector v) {
        super.subtract(v);
        wa.subtract(IntFeatVector.multiply(v, (double) iStep));
    }

    public AveragedWeight() {
        wa = new WeightVector();
    }

    public WeightVector getAveragedWeight() {
//    	Stopwatch sw = new Stopwatch("Averaging weight...");
        double dStep = (double) iStep;
        WeightVector v = new WeightVector(this);
        for (int i = 0; i < wa.capacity; ++i) {
            v.put(i, v.get(i) - wa.get(i) / dStep);
        }
//      sw.lap();
        return v;
    }

    @Override
    public void save(PrintWriter sw) {
        sw.println(iStep);
        super.save(sw);
        wa.save(sw);
    }

    @Override
    public void load(BufferedReader sr) throws IOException {
        iStep = Integer.parseInt(sr.readLine());
        super.load(sr);
        wa.load(sr);
    }
}