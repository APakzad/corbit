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

package corbit.segdep.handler;

import corbit.commons.io.Console;

public class DelayedFeature implements Comparable<DelayedFeature>
{
	enum DelayedFeatureType {
		LEX, POS
	}

	private static final String SEP = "-";

	private final int idxbgn;
	private final DelayedFeatureType type1;
	private final DelayedFeatureType type2;
	private final String template;
	private final int arg1idx;
	private final int arg2idx;
	private final String arg1;
	private final String arg2;
	final double value;

	private final int hash;

	DelayedFeature(String template, int idxbgn, int arg1idx, int arg2idx,
			DelayedFeatureType type1, DelayedFeatureType type2, double value)
	{
		this(template, idxbgn, arg1idx, arg2idx, type1, type2, null, null, value);
	}

	DelayedFeature(String template, int idxbgn, int arg1idx, int arg2idx,
			DelayedFeatureType type1, DelayedFeatureType type2, String arg1, String arg2, double value)
	{
		if (type1 == null)
			throw new IllegalArgumentException("type1 must be non-null.");

		this.template = template;
		this.idxbgn = idxbgn;
		this.arg1idx = arg1idx;
		this.arg2idx = arg2idx;
		this.type1 = type1;
		this.type2 = type2;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.value = value;

		hash = hash();
	}

	DelayedFeature fill(int woffset, DelayedFeatureType type, String str)
	{
		String _arg1 = arg1;
		String _arg2 = arg2;

		if (arg1idx == woffset && type1 == type)
			_arg1 = str;
		else if (arg2idx == woffset && type2 == type)
			_arg2 = str;
		else
			throw new IllegalArgumentException("Illegal arguments to fill.");

		return new DelayedFeature(template, idxbgn, arg1idx, arg2idx, type1, type2, _arg1, _arg2, value);
	}

	boolean filled()
	{
		return type2 == null && arg1 != null ||
				type2 != null && arg1 != null && arg2 != null;
	}

	boolean hasArgument(int idxbgn, int woffset, DelayedFeatureType type)
	{
		return this.idxbgn == idxbgn &&
				(woffset == arg1idx && type1 == type && arg1 == null ||
				woffset == arg2idx && type2 == type && arg2 == null);
	}

	String compile()
	{
		return type2 == null ?
				template + SEP + arg1 :
				template + SEP + arg1 + SEP + arg2;
	}

	public void print()
	{
		Console.writeLine(String.format("%d: (%d: %d, %s), (%d: %d, %s), %f, %s", idxbgn,
				arg1idx, getDelayedFeatureType(type1), arg1,
				arg2idx, getDelayedFeatureType(type2), arg2, value, filled() ? "filled" : ""));
	}

	public int getDelayedFeatureType(DelayedFeatureType t)
	{
		return t == null ? 0 : t == DelayedFeatureType.LEX ? 1 : 2;
	}

	public boolean equals(Object o)
	{
		if (o == null)
			throw new NullPointerException();
		if (!(o instanceof DelayedFeature))
			throw new ClassCastException();

		DelayedFeature df = (DelayedFeature)o;
		if (hash != df.hash) // it suffices for most cases
			return false;  
		return (idxbgn == df.idxbgn &&
				arg1idx == df.arg1idx &&
				arg2idx == df.arg2idx &&
				type1 == df.type1 &&
				type2 == df.type2 &&
				template.equals(df.template) &&
				value == df.value &&
				(arg1 == null ? df.arg1 == null : arg1.equals(df.arg1)) && (arg2 == null ? df.arg2 == null : arg2.equals(df.arg2)));
	}

	public int hashCode()
	{
		return hash;
	}

	private static int getHashCodeOfDouble(double d)
	{
		long bits = Double.doubleToLongBits(d);
		return (int)(bits ^ (bits >>> 32));
	}

	private int hash()
	{
		int hash = 13;
		hash = hash * 31 + idxbgn;
		hash = hash * 31 + arg1idx;
		hash = hash * 31 + arg2idx;
		hash = hash * 31 + (type1 == null ? 1 : type1 == DelayedFeatureType.LEX ? 2 : 3);
		hash = hash * 31 + (type2 == null ? 1 : type2 == DelayedFeatureType.LEX ? 2 : 3);
		hash = hash * 31 + template.hashCode();
		hash = hash * 31 + getHashCodeOfDouble(value);
		hash = hash * 31 + (arg1 == null ? 1 : arg1.hashCode());
		hash = hash * 31 + (arg2 == null ? 1 : arg2.hashCode());
		return hash;
	}

	@Override
	public int compareTo(DelayedFeature o)
	{
		int hash = o.hash;
		int hash0 = this.hash;
		return hash0 > hash ? 1 : hash0 < hash ? -1 : 0;
	}
}
