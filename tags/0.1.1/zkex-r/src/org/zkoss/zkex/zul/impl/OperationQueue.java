package org.zkoss.zkex.zul.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.zkoss.zk.ui.Desktop;

public class OperationQueue {
	private LinkedList _inner;
	private List _listeners;

	public OperationQueue() {
		this._inner = new LinkedList();
		this._listeners = Collections.synchronizedList(new LinkedList());
	}

	public void addListener(OperationQueueListener listener) {
		synchronized (this._listeners) {
			this._listeners.add(listener);
		}
	}

	public void removeListener(OperationQueueListener listener) {
		synchronized (this._listeners) {
			this._listeners.remove(listener);
		}
	}

	void fireQueueUnavailable(Desktop desktop) {
		OperationQueueListener[] ls;
		int i;
		synchronized (this._listeners) {
			ls = (OperationQueueListener[]) (OperationQueueListener[]) this._listeners
					.toArray(new OperationQueueListener[0]);
			for (i = 0; i < ls.length; ++i)
				ls[i].queueUnavailable(desktop);
		}
	}

	void clearListener() {
		synchronized (this._listeners) {
			this._listeners.clear();
		}
	}

	public void put(Operation op) {
		synchronized (this._inner) {
			this._inner.add(op);
		}
		synchronized (this) {
			super.notifyAll();
		}
	}

	public Operation element() {
		try {
			synchronized (this._inner) {
				if (this._inner.size() <= 0) {
					return null;
				}
				Operation op = (Operation) this._inner.getFirst();
				return op;
			}
		} catch (NoSuchElementException e) {
		}
		return null;
	}

	public boolean hasElement() {
		return (this._inner.size() > 0);
	}

	public Operation next() {
		try {
			synchronized (this._inner) {
				if (this._inner.size() <= 0) {
					return null;
				}
				Operation op = (Operation) this._inner.getFirst();
				this._inner.removeFirst();
				return op;
			}
		} catch (NoSuchElementException e) {
		}
		return null;
	}

	public void remove() {
		next();
	}
}