package org.nutz.a.diff.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.nutz.a.diff.DiffAppender;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class WriterAppender implements DiffAppender<String> {

	private boolean neverWrite;

	private BufferedWriter writer;

	public WriterAppender(Writer writer) {
		this.writer = Streams.buffw(writer);
		neverWrite = true;
	}

	@Override
	public DiffAppender<String> append(String obj) {
		try {
			if (neverWrite) 
				neverWrite = false;
			else
				writer.newLine();
			writer.write(obj);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return this;
	}
}
