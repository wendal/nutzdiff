package org.nutz.a.diff;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.nutz.a.diff.impl.NutDiff;
import org.nutz.a.diff.util.FileIterator;
import org.nutz.a.diff.util.StringLineAppender;
import org.nutz.a.diff.util.FileLineTranslator;
import org.nutz.a.diff.util.WriterAppender;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public abstract class Diffs {

	private static final StringTranslator<String> TN_STR = new FileLineTranslator();

	private static final Diff<String> __DIFF = createDiff();

	public static Difference<String> valueOf(String str) {
		return valueOf(Lang.inr(str));
	}

	public static Difference<String> valueOf(Reader reader) {
		Difference<String> diff = new Difference<String>(TN_STR);
		diff.valueOf(reader);
		return diff;
	}

	public static Iterator<String> itFile(String path) {
		return new FileIterator(Streams.fileInr(path));
	}

	public static Iterator<String> itFile(File f) {
		return new FileIterator(Streams.fileInr(f));
	}

	public static Iterator<String> itFile(Reader reader) {
		return new FileIterator(reader);
	}

	public static <T> Diff<T> createDiff(StringTranslator<T> trans) {
		return new NutDiff<T>(trans);
	}

	public static Diff<String> createDiff() {
		return createDiff(TN_STR);
	}

	public static DiffAppender<String> appender(StringBuilder sb) {
		return new StringLineAppender(sb);
	}

	public static DiffAppender<String> appender(Writer writer) {
		return new WriterAppender(writer);
	}

	public static StringLineAppender appender() {
		return new StringLineAppender();
	}

	public static Difference<String> diffFile(String basePath, String newPath) {
		return __DIFF.diff(itFile(basePath), itFile(newPath));
	}

	public static Difference<String> diffFile(File baseFile, File newFile) {
		return __DIFF.diff(itFile(baseFile), itFile(newFile));
	}

	public static Difference<String> diff(Reader rBase, Reader rNew) {
		return __DIFF.diff(itFile(rBase), itFile(rNew));
	}

}
