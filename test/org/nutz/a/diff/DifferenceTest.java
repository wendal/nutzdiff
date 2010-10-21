package org.nutz.a.diff;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.util.LinkedArray;

public class DifferenceTest {

	static List<String> list(String... ss) {
		ArrayList<String> list = new ArrayList<String>(ss.length);
		for (String s : ss)
			list.add(s);
		return list;
	}

	@Test
	public void test_simple_tostring() {
		ForTestStringTranslator trans = new ForTestStringTranslator();

		Difference<String> d = new Difference<String>(trans);
		d.addChange(3, 8, list("A", "B", "C", "D"));
		d.addRemove(20, 29);
		d.addAppend(list("X", "Y", "Z"));
		d.addSame(10, 20);

		String str = d.toString();

		Difference<String> d2 = new Difference<String>(trans);
		d2.valueOf(Lang.inr(str));

		assertTrue(d2.equals(d));
	}

	@Test
	public void test_simple_render() {
		ForTestStringTranslator trans = new ForTestStringTranslator();

		LinkedArray<String> base = new LinkedArray<String>(3);
		final StringBuilder sb = new StringBuilder();

		base.push("1").push("2").push("3").push("4");

		Difference<String> d = new Difference<String>(trans);
		d.addAppend(list("0"));
		d.addSame(0, 2);
		d.addRemove(2, 3);
		d.addChange(3, 4, list("A", "B"));
		d.addAppend(list("C"));

		d.render(base.iterator(), new DiffAppender<String>() {
			public DiffAppender<String> append(String obj) {
				sb.append(obj);
				return this;
			}
		});

		assertEquals("012ABC", sb.toString());
	}
}
