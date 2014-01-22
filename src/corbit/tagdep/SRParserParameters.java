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

import corbit.tagdep.io.InputFormat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SRParserParameters {

    final int m_numOpts = 24;
    // model parameters to save
    int m_iFeatureType = 0; // 0: HS10, 1: ZC11
    int m_iBeam = 8;
    int m_iDepth = 0;
    int m_iIteration = 0;
    int m_iBestIteration = 0;
    double m_dMargin = 0.0d;
    double m_dBestScore = Double.NEGATIVE_INFINITY;
    boolean m_bAveraged = true;
    boolean m_bParse = true;
    boolean m_bEarlyUpdate = true;
    boolean m_bUseGoldPos = false;
    boolean m_bAssignGoldPos = false;
    boolean m_bDP = true;
    boolean m_bAssignPosFollowsShift = true;
    boolean m_bEvalDelay = true;
    boolean m_bShiftWithPos = true;
    boolean m_bStrictStop = true;
    boolean m_bUseTrie = false;
    boolean m_bShuffle = true;
    boolean m_bAggressiveParallel = false;
    boolean m_bUseClosedTags = true;
    public boolean m_bUseSyntax = true;
    public boolean m_bUseTagFeature = true;
    public boolean m_bUseLookAhead = true;
    // program options
    int m_iParallel = 1;
    InputFormat m_iInputFormat = InputFormat.Malt; // 0: Malt, 1: CTB, 2: CoNLL
    boolean m_bDebug = false;
    boolean m_bLoadOnMemory = true;
    boolean m_bSaveEach = false;
    boolean m_bCheckOpts = false;
    boolean m_bCompressedModel = true;
    boolean m_bSaveOnlyPos = false;
    boolean m_bShowStats = false;
    boolean m_bRebuildVocab = true;

    void saveProperties(PrintWriter pw) {
        pw.println("iFeatureType = " + m_iFeatureType);
        pw.println("iBeam = " + m_iBeam);
        pw.println("iDepth = " + m_iDepth);
        pw.println("iIteration = " + m_iIteration);
        pw.println("iBestIteration = " + m_iBestIteration);
        pw.println("dMargin = " + m_dMargin);
        pw.println("dBestScore = " + m_dBestScore);
        pw.println("bAveraged = " + m_bAveraged);
        pw.println("bParse = " + m_bParse);
        pw.println("bEarlyUpdate = " + m_bEarlyUpdate);
        pw.println("bUseGoldPos = " + m_bUseGoldPos);
        pw.println("bAssignGoldPos = " + m_bAssignGoldPos);
        pw.println("bDP = " + m_bDP);
        pw.println("bAssignPosFollowsShift = " + m_bAssignPosFollowsShift);
        pw.println("bEvalDelay = " + m_bEvalDelay);
        pw.println("bUseSyntax = " + m_bUseSyntax);
        pw.println("bUsePosFeature = " + m_bUseTagFeature);
        pw.println("bShiftWithPos = " + m_bShiftWithPos);
        pw.println("bStrictStop = " + m_bStrictStop);
        pw.println("bUseLookAhead = " + m_bUseLookAhead);
        pw.println("bUseTrie = " + m_bUseTrie);
        pw.println("bShuffle = " + m_bShuffle);
        pw.println("bAggressiveParallel = " + m_bAggressiveParallel);
        pw.println("bUseClosedTags = " + m_bUseClosedTags);
        pw.println("EOP");
    }

    void loadProperties(BufferedReader br) throws NumberFormatException, IOException {
        int iNumOpts = 0;
        String sLine;
        while (!(sLine = br.readLine()).equals("EOP")) {
            ++iNumOpts;
            String[] ss = sLine.split("=");
            String sKey = ss[0].trim();
            String sVal = ss[1].trim();

            if (sKey.equals("iFeatureType")) {
                m_iFeatureType = Integer.parseInt(sVal);
            } else if (sKey.equals("iBeam")) {
                m_iBeam = Integer.parseInt(sVal);
            } else if (sKey.equals("iDepth")) {
                m_iDepth = Integer.parseInt(sVal);
            } else if (sKey.equals("iIteration")) {
                m_iIteration = Integer.parseInt(sVal);
            } else if (sKey.equals("iBestIteration")) {
                m_iBestIteration = Integer.parseInt(sVal);
            } else if (sKey.equals("dMargin")) {
                m_dMargin = Double.parseDouble(sVal);
            } else if (sKey.equals("dBestScore")) {
                m_dBestScore = Double.parseDouble(sVal);
            } else if (sKey.equals("bAveraged")) {
                m_bAveraged = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bParse")) {
                m_bParse = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bEarlyUpdate")) {
                m_bEarlyUpdate = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bUseGoldPos")) {
                m_bUseGoldPos = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bAssignGoldPos")) {
                m_bAssignGoldPos = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bDP")) {
                m_bDP = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bAssignPosFollowsShift")) {
                m_bAssignPosFollowsShift = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bEvalDelay")) {
                m_bEvalDelay = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bUseSyntax")) {
                m_bUseSyntax = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bUsePosFeature")) {
                m_bUseTagFeature = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bShiftWithPos")) {
                m_bShiftWithPos = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bStrictStop")) {
                m_bStrictStop = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bUseLookAhead")) {
                m_bUseLookAhead = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bUseTrie")) {
                m_bUseTrie = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bShuffle")) {
                m_bShuffle = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bAggressiveParallel")) {
                m_bAggressiveParallel = Boolean.parseBoolean(sVal);
            } else if (sKey.equals("bUseClosedTags")) {
                m_bUseClosedTags = Boolean.parseBoolean(sVal);
            } else if (m_bCheckOpts) {
                throw new RuntimeException("Cannot recognize model parameter: " + sKey);
            } else {
                System.err.println("Cannot recognize model parameter: " + sKey);
            }
        }
        if (iNumOpts != m_numOpts) {
            if (m_bCheckOpts) {
                throw new RuntimeException("There are some missing model parameters. Probably, the model is not compatible.");
            } else {
                System.err.println("There are some missing model parameters. Probably, the model is not compatible.");
            }
        }

    }

    void printProperties() {
        System.err.println("[options]");
        System.err.println("iThread = " + m_iParallel);
        System.err.println("bDebug = " + m_bDebug);
        System.err.println("bLoadOnMemory = " + m_bLoadOnMemory);
        System.err.println("bSaveEachTime = " + m_bSaveEach);
        System.err.println("bCheckOpts = " + m_bCheckOpts);
        System.err.println("bCompressedModel = " + m_bCompressedModel);
        System.err.println("bSaveOnlyPos = " + m_bSaveOnlyPos);
        System.err.println("bShowStats = " + m_bShowStats);
        System.err.println("bRebuildVocab = " + m_bRebuildVocab);
        System.err.println();
        System.err.println("[model parameters]");
        System.err.println("iFeatureType = " + m_iFeatureType);
        System.err.println("iBeam = " + m_iBeam);
        System.err.println("iDepth = " + m_iDepth);
        System.err.println("iIteration = " + m_iIteration);
        System.err.println("iBestIteration = " + m_iBestIteration);
        System.err.println("dMargin = " + m_dMargin);
        System.err.println("dBestScore = " + m_dBestScore);
        System.err.println("bAveraged = " + m_bAveraged);
        System.err.println("bParse = " + m_bParse);
        System.err.println("bEarlyUpdate = " + m_bEarlyUpdate);
        System.err.println("bUseGoldPos = " + m_bUseGoldPos);
        System.err.println("bAssignGoldPos = " + m_bAssignGoldPos);
        System.err.println("bDP = " + m_bDP);
        System.err.println("bAssignPosFollowsShift = " + m_bAssignPosFollowsShift);
        System.err.println("bEvalDelay = " + m_bEvalDelay);
        System.err.println("bUseSyntax = " + m_bUseSyntax);
        System.err.println("bUsePosFeature = " + m_bUseTagFeature);
        System.err.println("bShiftWithPos = " + m_bShiftWithPos);
        System.err.println("bUseTrie = " + m_bUseTrie);
        System.err.println("bShuffle = " + m_bShuffle);
        System.err.println("bAggressiveParallel = " + m_bAggressiveParallel);
        System.err.println("bUseClosedTags = " + m_bUseClosedTags);
        System.err.println();
    }
}
