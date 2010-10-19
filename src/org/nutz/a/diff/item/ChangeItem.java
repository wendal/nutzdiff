package org.nutz.a.diff.item;

import java.util.Iterator;
import java.util.List;

import org.nutz.a.diff.DiffAppender;
import org.nutz.a.diff.DiffItem;
import org.nutz.a.diff.StringTranslator;

public class ChangeItem<T> implements DiffItem<T> {

	private AppendItem<T> append;

	private RemoveItem<T> remove;

	public ChangeItem(StringTranslator<T> trans, int l, int r, List<T> objs) {
		append = new AppendItem<T>(trans, objs);
		remove = new RemoveItem<T>(l, r);
	}

	@Override
	public String getTypeName() {
		return "C";
	}

	@Override
	public void render(Iterator<T> ibase, DiffAppender<T> appender) {
		remove.render(ibase, appender);
		append.render(ibase, appender);
	}

	@Override
	public String toString() {
		return remove.toString() + this.append.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangeItem<?> other = (ChangeItem<?>) obj;
		if (!remove.equals(other.remove))
			return false;
		if (!append.equals(other.append))
			return false;
		return true;
	}

}
