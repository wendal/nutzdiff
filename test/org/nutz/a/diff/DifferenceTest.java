package org.nutz.a.diff;

import java.util.List;

import org.junit.Test;
import org.nutz.json.Json;

public class DifferenceTest {

	@Test
	@SuppressWarnings("unchecked")
	public void test_simple_tostring() {
		Difference<String> d = new Difference<String>(new ForTestStringTranslator());
		d.addChange(3, 8, Json.fromJson(List.class, "['A','B','C','D']"));
		d.addRemove(3, 5);
		d.addSame(10, 20);
		System.out.print(d);
	}
}
