package org.nutz.a.diff;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.lang.Files;

public class DiffFileTest {

	private static String path(String name) {
		return "org/nutz/a/diff/txt/" + name + ".txt";
	}

	@Test
	public void test_blank_ending_file_a() {
		Difference<String> diff = Diffs.diffFile(path("a_base"), path("a_new"));
		String newText = Files.read(path("a_new"));
		StringBuilder sb = new StringBuilder();
		diff.render(Diffs.itFile(path("a_base")), Diffs.appender(sb));

		assertEquals(newText, sb.toString());
	}

}
