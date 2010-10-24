package org.nutz.a.diff.impl;

import java.util.Iterator;

import org.nutz.a.diff.Diff;
import org.nutz.a.diff.Difference;
import org.nutz.a.diff.StringTranslator;

public class NutDiff<T> implements Diff<T> {

	private StringTranslator<T> trans;

	public NutDiff(StringTranslator<T> trans) {
		this.trans = trans;
	}

	@Override
	public Difference<T> diff(Iterator<T> ibase, Iterator<T> inew) {
		Difference<T> diff = new Difference<T>(trans);
		Diffing<T> ing = new Diffing<T>(ibase, inew);
		while (ing.hasNext()) {
			ing.next(diff);
		}
		return diff;
	}

}
