package org.zkoss.zkex.zul;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zkex.zul.impl.Operation;
import org.zkoss.zkex.zul.impl.OperationQueue;
import org.zkoss.zkex.zul.impl.OperationQueueListener;
import org.zkoss.zkex.zul.impl.OperationThread;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

public class SimpleListModelSharer implements ListModelSharer {
	private static final Log log = Log.lookup(SimpleListModelSharer.class);
	private static final int OP_ADD = 1;
	private static final int OP_REMOVE = 2;
	private static final int OP_SET = 3;
	private List _proxys = Collections.synchronizedList(new LinkedList());
	private List _innerData;
	private ListModel _srcModel;
	private ListDataListener _srcListener;

	public SimpleListModelSharer(ListModel model) {
		this._srcModel = model;
		init();
	}

	private void init() {
		this._innerData = Collections.synchronizedList(new LinkedList());
		int size = this._srcModel.getSize();
		for (int i = 0; i < size; ++i) {
			this._innerData.add(this._srcModel.getElementAt(i));
		}

		this._srcListener = new ListDataListener() {
			public void onChange(ListDataEvent event) {
				SimpleListModelSharer.access$000(SimpleListModelSharer.this, event);
			}

		};
		this._srcModel.addListDataListener(this._srcListener);
	}

	private void onListDataChange(ListDataEvent event) {
		int start;
		int end;
		int i;
		Object obj;
		int type = event.getType();
		ListModel model = event.getModel();
		if (this._srcModel != model)
			return;
		int index0 = event.getIndex0();
		int index1 = event.getIndex1();

		int min = (index0 > index1) ? index1 : index0;
		int max = (index0 > index1) ? index0 : index1;

		switch (type) {
		case 0:
			start = (min < 0) ? 0 : min;
			end = (max < 0) ? this._srcModel.getSize() : max;

			for (i = start; i <= end; ++i) {
				obj = this._srcModel.getElementAt(i);
				this._innerData.set(i, obj);
				putToQueue(3, new Object[] { new Integer(i), obj });
			}
			break;
		case 1:
			start = (min < 0) ? 0 : min;
			end = (max < 0) ? this._srcModel.getSize() : max;

			for (i = start; i <= end; ++i) {
				obj = this._srcModel.getElementAt(i);
				this._innerData.add(i, obj);
				putToQueue(1, new Object[] { new Integer(i), obj });
			}
			break;
		case 2:
			start = (min < 0) ? 0 : min;
			end = (max < 0) ? this._srcModel.getSize() : max;

			for (i = end; i >= start; --i) {
				this._innerData.remove(i);
				putToQueue(2, new Object[] { new Integer(i) });
			}
			break;
		default:
			throw new IllegalStateException("Unknow Event Type:" + type);
		}
	}

	public ListModel getProxy(Desktop desktop) {
		ProxyModel proxy;
		if (log.debugable()) {
			log.debug("create proxy model for:" + desktop);
		}

		synchronized (this._proxys) {
			proxy = new ProxyModel(this._innerData);

			QueueListener oql = new QueueListener(desktop, proxy);
			proxy.setOperationQueueListener(oql);

			OperationQueue queue = OperationThread.getQueue(desktop);
			queue.addListener(oql);
			proxy.setQueue(queue);

			this._proxys.add(proxy);
		}
		return proxy;
	}

	public int getProxyCount() {
		synchronized (this._proxys) {
			return this._proxys.size();
		}
	}

	private void destroyProxy(Desktop desktop, ListModel model, boolean rmQueueListener) {
		if (!(model instanceof ProxyModel))
			throw new IllegalArgumentException("Not a created proxy model:" + model.getClass());

		synchronized (this._proxys) {
			if (this._proxys.remove(model)) {
				if (log.debugable())
					log.debug("destory proxy model for:" + desktop);

				((ProxyModel) model).clear();
			}
		}
	}

	private void putToQueue(int op, Object[] parms) {
		synchronized (this._proxys) {
			Iterator iter = this._proxys.iterator();
			while (iter.hasNext()) {
				ProxyModel model = (ProxyModel) iter.next();
				ListModelOperation lmop = new ListModelOperation(op, parms, model);
				OperationQueue queue = model.getQueue();
				if (queue != null)
					queue.put(lmop);
			}
		}
	}

	static void access$000(SimpleListModelSharer x0, ListDataEvent x1) {
		x0.onListDataChange(x1);
	}

	static void access$100(SimpleListModelSharer x0, Desktop x1, ListModel x2, boolean x3) {
		x0.destroyProxy(x1, x2, x3);
	}

	private static class ProxyModel extends AbstractListModel {
		private OperationQueue _queue;
		private OperationQueueListener _oqListener;
		List _proxyedData;

		OperationQueue getQueue() {
			return this._queue;
		}

		void setQueue(OperationQueue queue) {
			this._queue = queue;
		}

		ProxyModel(Collection c) {
			this._proxyedData = Collections.synchronizedList(new LinkedList(c));
		}

		void clear() {
			if ((this._queue != null) && (this._oqListener != null))
				this._queue.removeListener(this._oqListener);

			this._queue = null;
			this._oqListener = null;
			this._proxyedData.clear();
			clearSelection();
		}

		void add(int index, Object element) {
			this._proxyedData.add(index, element);
			fireEvent(1, index, index);
		}

		Object remove(int index) {
			Object obj = this._proxyedData.remove(index);
			removeSelection(obj);
			fireEvent(2, index, index);
			return obj;
		}

		Object set(int index, Object element) {
			Object obj = this._proxyedData.set(index, element);
			fireEvent(0, index, index);
			return obj;
		}

		void setOperationQueueListener(OperationQueueListener oql) {
			this._oqListener = oql;
		}

		public Object getElementAt(int index) {
			return this._proxyedData.get(index);
		}

		public int getSize() {
			return this._proxyedData.size();
		}
	}

	private class ListModelOperation implements Operation {
		int _op;
		Object[] _parms;
		SimpleListModelSharer.ProxyModel _model;

		ListModelOperation(int paramInt, Object[] paramArrayOfObject, SimpleListModelSharer.ProxyModel paramProxyModel) {
			this._op = paramInt;
			this._parms = paramArrayOfObject;
			this._model = paramProxyModel;
		}

		public void execute(Desktop _desktop) {
			switch (this._op) {
			case 1:
				this._model.add(((Integer) this._parms[0]).intValue(), this._parms[1]);
				break;
			case 2:
				this._model.remove(((Integer) this._parms[0]).intValue());
				break;
			case 3:
				this._model.set(((Integer) this._parms[0]).intValue(), this._parms[1]);
				break;
			default:
				throw new UnsupportedOperationException("Unknow operation:" + this._op);
			}
		}

		public void failToExecute(Desktop _desktop) {
			SimpleListModelSharer.access$100(SimpleListModelSharer.this, _desktop, this._model, true);
		}
	}

	private class QueueListener implements OperationQueueListener {
		Desktop _desktop;
		SimpleListModelSharer.ProxyModel _proxy;

		QueueListener(Desktop paramDesktop, SimpleListModelSharer.ProxyModel paramProxyModel) {
			this._desktop = paramDesktop;
			this._proxy = paramProxyModel;
		}

		public void queueUnavailable(Desktop desktop) {
			if (this._desktop == desktop) {
				SimpleListModelSharer.access$100(SimpleListModelSharer.this, desktop, this._proxy, false);
			}
		}
	}
}