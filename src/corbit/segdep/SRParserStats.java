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

package corbit.segdep;

import corbit.commons.io.Console;

public class SRParserStats
{
	/**
	 * Even with volatile declaration, these variables are not thread-safe.
	 * However, it is practically ok for getting rough statistics.
	 */

	public volatile double totalScore = 0.0;

	public volatile int numSentencesProcessed = 0;
	public volatile int numSentencesStopped = 0;

	public volatile long numNonDPStatesEvaluated = 0;
	public volatile int numStatesEvaluated = 0;
	public volatile int numStatesMerged = 0;

	public volatile int numSegCandidates = 0;
	public volatile int numSegsPruned = 0;
	public volatile int numSegsEvaluated = 0;

	public volatile int numTagCandidates = 0;
	public volatile int numTagsConsidered = 0;
	public volatile int numTagsPruned = 0;
	public volatile int numTagsEvaluated = 0;

	public volatile int numGoldMoves = 0;
	public volatile int numGoldMovesSegCovered = 0;
	public volatile int numGoldMovesCovered = 0;

	public volatile int numSegsIncorrectlyPruned = 0;
	public volatile int numSegsPrematurelyPruned = 0;

	public volatile int numGoldOutOfBeamHigher = 0;
	public volatile int numGoldOutOfBeamLower = 0;
	public volatile int numGoldInBeamHigher = 0;
	public volatile int numGoldInBeamLower = 0;

	public void print()
	{
		/* evaluated sentences and states */

		if (numSentencesStopped > 0)
			Console.writeLine(String.format("%d/%d sentences (%f%%) have been early updated.", numSentencesStopped, numSentencesProcessed, (double)numSentencesStopped / numSentencesProcessed * 100));
		if (numNonDPStatesEvaluated > 0)
			Console.writeLine(numNonDPStatesEvaluated + " non-DP states evaluated.");
		if (numStatesMerged > 0)
			Console.writeLine(String.format("%d/%d states (%f%%) have been merged.", numStatesMerged, numStatesEvaluated, (double)numStatesMerged / (double)numStatesEvaluated * 100));
		Console.writeLine("Average output score: " + totalScore / numSentencesProcessed);

		/* evaluated actions */

		Console.writeLine(String.format("Segmentation: %,9d evaluated, %,9d pruned, %,9d in total",
				numSegsEvaluated, numSegsPruned, numSegCandidates));
		Console.writeLine(String.format("POS tagging:  %,9d evaluated, %,9d pruned, %,9d considered, %,9d in total",
				numTagsEvaluated, numTagsPruned, numTagsConsidered, numTagCandidates));
		Console.writeLine(String.format("Gold actions: %,9d / %,9d (%f%%) covered.", numGoldMovesCovered, numGoldMoves, (double)numGoldMovesCovered / numGoldMoves * 100));
		Console.writeLine(String.format("Gold actions: %,9d / %,9d (%f%%) segs covered.", numGoldMovesSegCovered, numGoldMoves, (double)numGoldMovesSegCovered / numGoldMoves * 100));
		Console.writeLine(String.format("Gold actions: %,9d / %,9d (%f%%) segs are incorrectly pruned.", numSegsIncorrectlyPruned, numSegsPruned, (double)numSegsIncorrectlyPruned / numSegsPruned * 100));
		Console.writeLine(String.format("Gold actions: %,9d / %,9d (%f%%) segs are prematurely pruned.", numSegsPrematurelyPruned, numSegsPruned, (double)numSegsPrematurelyPruned / numSegsPruned * 100));
	}
}
