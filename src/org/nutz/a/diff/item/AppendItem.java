package org.nutz.a.diff.item;

import java.util.Iterator;
import java.util.List;

import org.nutz.a.diff.DiffAppender;
import org.nutz.a.diff.DiffItem;
import org.nutz.a.diff.StringTranslator;

/**
 * 这段内容 BASE 没有，应该立即增加
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class AppendItem<T> implements DiffItem<T> {

	private List<T> objs;

	private StringTranslator<T> trans;

	public AppendItem(StringTranslator<T> trans, List<T> objs) {
		this.trans = trans;
		this.objs = objs;
	}

	@Override
	public String getTypeName() {
		return "A";
	}

	@Override
	public void render(Iterator<T> ibase, DiffAppender<T> appender) {
		if (null != objs && !objs.isEmpty())
			for (T obj : objs)
				appender.append(obj);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T obj : objs)
			trans.appendTo(sb, obj);
		return String.format("+++:%d:%d\n%s", objs.size(), sb.length(), sb);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		AppendItem<?> other = (AppendItem<?>) obj;
		if (objs.size() != other.objs.size())
			return false;
		Iterator<?> me = objs.iterator();
		Iterator<?> ta = other.objs.iterator();
		while (me.hasNext())
			if (!me.next().equals(ta.next()))
				return false;
		return true;
	}

}
