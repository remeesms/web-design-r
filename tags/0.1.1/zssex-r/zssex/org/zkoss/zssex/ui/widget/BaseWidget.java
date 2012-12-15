package org.zkoss.zssex.ui.widget;

import java.io.Serializable;

import org.zkoss.lang.Objects;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zss.ui.Widget;
import org.zkoss.zss.ui.sys.WidgetHandler;

public abstract class BaseWidget implements Widget, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String _id;
	private int _row;
	private int _column;
	private int _row2;
	private int _column2;
	private int _left;
	private int _top;
	private int _zindex;
	private boolean _visible;
	private boolean _movable;
	private boolean _sizable;
	private boolean _focusable;
	private String _ctrlKeys;
	private WidgetCtrl _ctrl;
	private WidgetHandler _handler;
	private boolean _inClient;

	public BaseWidget() {
		this._zindex = 0;

		this._visible = true;

		this._movable = false;

		this._sizable = false;

		this._inClient = false;
	}

	protected WidgetCtrl newCtrl() {
		WidgetCtrl ctrl = new WidgetCtrl(this);
		return ctrl;
	}

	boolean isInClient() {
		return this._inClient;
	}

	void setInClient(boolean inClient) {
		this._inClient = inClient;
	}

	public Component getComponent() {
		return getCtrl();
	}

	protected Component getInnerComponent() {
		return null;
	}

	protected synchronized WidgetCtrl getCtrl() {
		if (this._ctrl == null)
			this._ctrl = newCtrl();

		return this._ctrl;
	}

	public WidgetHandler getHandler() {
		return this._handler;
	}

	public void setHandler(WidgetHandler handler) {
		if (!(Objects.equals(this._handler, handler))) {
			if (this._handler != null)
				this._handler.removeWidget(this);
			this._handler = handler;
			if (this._handler != null)
				this._handler.addWidget(this);
		}
	}

	void setHandler0(WidgetHandler handler) {
		if (!(Objects.equals(this._handler, handler)))
			this._handler = handler;
	}

	public int getRow() {
		return this._row;
	}

	public void setRow(int row) {
		if (this._row != row) {
			this._row = row;
			getCtrl().smartUpdate("row", Integer.valueOf(this._row));
		}
	}

	public int getColumn() {
		return this._column;
	}

	public void setColumn(int column) {
		if (this._column != column) {
			this._column = column;
			getCtrl().smartUpdate("col", Integer.valueOf(this._column));
		}
	}

	public int getRow2() {
		return this._row2;
	}

	public void setRow2(int row) {
		if (this._row2 != row) {
			this._row2 = row;
			getCtrl().smartUpdate("row2", Integer.valueOf(this._row2));
		}
	}

	public int getColumn2() {
		return this._column2;
	}

	public void setColumn2(int column) {
		if (this._column2 != column) {
			this._column2 = column;
			getCtrl().smartUpdate("col2", Integer.valueOf(this._column2));
		}
	}

	public int getLeft() {
		return this._left;
	}

	public void setLeft(int left) {
		if (this._left != left) {
			this._left = left;
			getCtrl().smartUpdate("left", Integer.valueOf(this._left));
		}
	}

	public int getTop() {
		return this._top;
	}

	public void setTop(int top) {
		if (this._top != top) {
			this._top = top;
			getCtrl().smartUpdate("top", Integer.valueOf(this._top));
		}
	}

	public void setZindex(int zindex) {
		if (this._zindex != zindex) {
			this._zindex = zindex;
			getCtrl().smartUpdate("zindex", Integer.valueOf(this._zindex));
		}
	}

	public int getZindex() {
		return this._zindex;
	}

	public boolean isVisible() {
		return this._visible;
	}

	public void setVisible(boolean visible) {
		if (this._visible != visible) {
			this._visible = visible;
			getCtrl().smartUpdate("visible", Boolean.valueOf(this._visible));
		}
	}

	public boolean isMovable() {
		return this._movable;
	}

	public void setMovable(boolean movable) {
		if (this._movable != movable) {
			this._movable = movable;
			getCtrl().smartUpdate("movable", Boolean.valueOf(movable));
		}
	}

	public boolean isSizable() {
		return this._sizable;
	}

	public void setSizable(boolean sizable) {
		if (this._sizable != sizable) {
			this._sizable = sizable;
			getCtrl().smartUpdate("sizable", Boolean.valueOf(sizable));
		}
	}
	
	public boolean isFocusable() {
		return this._focusable;
	}
	
	public void setFocusable(boolean focusable) {
		if (this._focusable != focusable) {
			this._focusable = focusable;
			getCtrl().smartUpdate("focusable", Boolean.valueOf(focusable));
		}
	}

	public void setCtrlKeys(String ctrlKeys) {
		if ((ctrlKeys != null) && (ctrlKeys.length() == 0))
			ctrlKeys = null;
		if (this._ctrlKeys != ctrlKeys) {
			this._ctrlKeys = ctrlKeys;
			getCtrl().smartUpdate("ctrlKeys", ctrlKeys);
		}
	}

	public String getCtrlKeys() {
		return this._ctrlKeys;
	}

	public void setId(String id) {
		if (this._id != id) {
			this._id = id;
			getCtrl().smartUpdate("id", id);
		}
	}

	public String getId() {
		return this._id;
	}

	public void addEventListener(String evtnm, EventListener listener) {
		Component comp = getInnerComponent();
		if (comp != null)
			comp.addEventListener(evtnm, listener);
	}

	public boolean removeEventListener(String evtnm, EventListener listener) {
		Component comp = getInnerComponent();
		if (comp != null)
			return comp.removeEventListener(evtnm, listener);

		return false;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(super.getClass().getSimpleName());
		sb.append(",row:").append(this._row).append(",col:").append(this._column).append(",client:")
				.append(this._inClient);
		sb.append(",zindex:").append(this._zindex);
		return sb.toString();
	}
	
	public abstract void invalidate();

	public abstract AreaReference getUpdateAreaReference();

	public abstract String getWidgetType();
}