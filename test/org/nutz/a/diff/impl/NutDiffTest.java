package org.nutz.a.diff.impl;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;
import org.nutz.a.diff.CharIterator;
import org.nutz.a.diff.DiffItem;
import org.nutz.a.diff.Difference;
import org.nutz.a.diff.ForTestStringTranslator;

public class NutDiffTest {

	private Difference<String> diff(String base, String snew) {
		NutDiff<String> nd = new NutDiff<String>(new ForTestStringTranslator());
		return nd.diff(new CharIterator(base), new CharIterator(snew));
	}

	@Test
	public void test_2line_same() {
		Difference<String> df = diff("AB", "AB");
		assertEquals(1, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		DiffItem<String> ap = it.next();
		assertEquals("S", ap.getTypeName());
		assertEquals("=2:0:2", ap.toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_oneline_change() {
		Difference<String> df = diff("A", "B");
		assertEquals(1, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		DiffItem<String> ap = it.next();
		assertEquals("C", ap.getTypeName());
		assertEquals("-1:0:1;+1:1\nB", ap.toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_oneline_append() {
		Difference<String> df = diff("", "B");
		assertEquals(1, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		DiffItem<String> ap = it.next();
		assertEquals("A", ap.getTypeName());
		assertEquals("+1:1\nB", ap.toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_oneline_remove() {
		Difference<String> df = diff("A", "");
		assertEquals(1, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		DiffItem<String> ap = it.next();
		assertEquals("D", ap.getTypeName());
		assertEquals("-1:0:1", ap.toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_first_same() {
		Difference<String> df = diff("ABCDE", "A123E");
		assertEquals(3, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("=1:0:1", it.next().toString());
		assertEquals("-3:1:4;+3:3\n123", it.next().toString());
		assertEquals("=1:4:5", it.next().toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_first_remove() {
		Difference<String> df = diff("XBCDE", "B123E");
		assertEquals(4, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("-1:0:1", it.next().toString());
		assertEquals("=1:1:2", it.next().toString());
		assertEquals("-2:2:4;+3:3\n123", it.next().toString());
		assertEquals("=1:4:5", it.next().toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_first_change() {
		Difference<String> df = diff("XYBCDE", "FFFB123E");
		assertEquals(4, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("-2:0:2;+3:3\nFFF", it.next().toString());
		assertEquals("=1:2:3", it.next().toString());
		assertEquals("-2:3:5;+3:3\n123", it.next().toString());
		assertEquals("=1:5:6", it.next().toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_base_is_short() {
		Difference<String> df = diff("ABC", "ABC12");
		assertEquals(2, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("=3:0:3", it.next().toString());
		assertEquals("+2:2\n12", it.next().toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_new_is_short() {
		Difference<String> df = diff("ABC12", "ABC");
		assertEquals(2, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("=3:0:3", it.next().toString());
		assertEquals("-2:3:5", it.next().toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_append_in_middle() {
		Difference<String> df = diff("AB", "A12B");
		assertEquals(3, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("=1:0:1", it.next().toString());
		assertEquals("+2:2\n12", it.next().toString());
		assertEquals("=1:1:2", it.next().toString());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_unify_case() {
		Difference<String> df = diff("123456789", "1AF23B7C9DE");
		assertEquals(8, df.size());
		Iterator<DiffItem<String>> it = df.iterator();
		assertEquals("=1:0:1", it.next().toString());
		assertEquals("+2:2\nAF", it.next().toString());
		assertEquals("=2:1:3", it.next().toString());
		assertEquals("-3:3:6;+1:1\nB", it.next().toString());
		assertEquals("=1:6:7", it.next().toString());
		assertEquals("-1:7:8;+1:1\nC", it.next().toString());
		assertEquals("=1:8:9", it.next().toString());
		assertEquals("+2:2\nDE", it.next().toString());
		assertFalse(it.hasNext());
	}
}
