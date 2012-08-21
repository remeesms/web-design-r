package org.zkoss.zssex.ui.widget;

import java.io.IOException;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zss.ui.Spreadsheet;

public class WidgetCtrl extends AbstractComponent {
	private static final long serialVersionUID = 1L;
	private BaseWidget _widget;

	public WidgetCtrl(BaseWidget widget) {
		this._widget = widget;
	}

	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		renderer.render("id", this._widget.getId());
		renderer.render("type", this._widget.getWidgetType());
		renderer.render("row", this._widget.getRow());
		renderer.render("col", this._widget.getColumn());
		renderer.render("left", this._widget.getLeft());
		renderer.render("top", this._widget.getTop());
		renderer.render("zIndex", this._widget.getZindex());
		renderer.render("style", getStyle());
		renderer.render("sizable", this._widget.isSizable());
		renderer.render("movable", this._widget.isMovable());
		renderer.render("ctrlKeys", this._widget.getCtrlKeys());
	}

	protected String getStyle() {
		StringBuffer sb = new StringBuffer(64);
		HTMLs.appendStyle(sb, "z-index", Integer.toString(this._widget.getZindex()));
		// getSpreadsheet().get
		return sb.toString();
	}

	public Spreadsheet getSpreadsheet() {
		return ((Spreadsheet) getParent());
	}

	public void setSpreadsheet(Spreadsheet ss) {
		setParent(ss);
	}

	public void setParent(Component parent) {
		if ((parent != null) && (!(parent instanceof Ghost)))
			throw new UiException("parent must be a " + Ghost.class);

		super.setParent(parent);
	}

	public BaseWidget getWidget() {
		return this._widget;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append("[").append(this._widget.toString()).append("]");
		return sb.toString();
	}

	public void smartUpdate(String attr, Object value) {
		super.smartUpdate(attr, value);
	}
}