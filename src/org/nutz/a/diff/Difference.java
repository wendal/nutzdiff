package org.nutz.a.diff;

import java.util.Iterator;
import java.util.List;

import org.nutz.a.diff.item.*;
import org.nutz.lang.util.LinkedArray;

/**
 * 描述一个不同
 * 
 * 基于这个不同，能够从 base 构建出 new
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Difference<T> {

	/**
	 * 所有的不同都存放在这里，构建时为压栈，输出时，从栈顶依次弹出
	 */
	private LinkedArray<DiffItem<T>> stack;

	private StringTranslator<T> trans;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public Difference(StringTranslator<T> trans, int width) {
		this.stack = new LinkedArray(DiffItem.class, width);
		this.trans = trans;
	}

	public Difference(StringTranslator<T> trans) {
		this(trans, 5);
	}

	public Difference<T> addSame(int l, int r) {
		stack.push(new SameItem<T>(l, r));
		return this;
	}

	public Difference<T> addRemove(int l, int r) {
		stack.push(new RemoveItem<T>(l, r));
		return this;
	}

	public Difference<T> addChange(int l, int r, List<T> objs) {
		stack.push(new ChangeItem<T>(trans, l, r, objs));
		return this;
	}

	public Difference<T> addAppend(List<T> objs) {
		stack.push(new AppendItem<T>(trans, objs));
		return this;
	}

	public void render(Iterator<T> ibase, DiffAppender<T> appender) {
		while (!stack.isEmpty())
			stack.popFirst().render(ibase, appender);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<DiffItem<T>> it = stack.iterator();
		while (it.hasNext()) {
			DiffItem<T> item = it.next();
			String s = item.toString();
			sb.append(String.format("<%s:%d>%s\n", item.getTypeName(), s.length(), s));
		}
		return sb.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj instanceof Difference) {
			if (stack.size() != ((Difference<T>) obj).stack.size())
				return false;
			Iterator<DiffItem<T>> me = stack.iterator();
			Iterator<DiffItem<T>> ta = ((Difference<T>) obj).stack.iterator();
			while (me.hasNext()) {
				if (!me.next().equals(ta.next()))
					return false;
			}
			return true;
		}
		return false;
	}

}
