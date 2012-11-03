package org.zkoss.zssex.ui.widget;

import org.zkoss.poi.ss.usermodel.Picture;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zss.model.Worksheet;

public class ImageWidget extends BaseWidget {
	org.zkoss.zul.Image _image;

	private synchronized org.zkoss.zul.Image inner() {
		if (this._image == null)
			this._image = new org.zkoss.zul.Image();

		return this._image;
	}

	public ImageWidget(Worksheet sheet, Picture pic) {
		setId(pic.getPictureId());
		setSizable(true);
		setMovable(true);
		setCtrlKeys("#del");
	}

	protected Component getInnerComponent() {
		return inner();
	}

	public String getSrc() {
		return inner().getSrc();
	}

	public void setSrc(String src) {
		inner().setSrc(src);
	}

	public org.zkoss.image.Image getContent() {
		return inner().getContent();
	}

	public void setWidth(String w) {
		inner().setWidth(w);
	}

	public String getWidth() {
		return inner().getWidth();
	}

	public void setHeight(String h) {
		inner().setHeight(h);
	}

	public String getHeight() {
		return inner().getHeight();
	}

	public void setBorder(String border) {
		inner().setBorder(border);
	}

	public String getBorder() {
		return inner().getBorder();
	}

	public void setContent(org.zkoss.image.Image image) {
		inner().setContent(image);
	}

	protected WidgetCtrl newCtrl() {
		WidgetCtrl ctrl = super.newCtrl();
		inner().setParent(ctrl);
		return ctrl;
	}
	
	public void invalidate() {
		inner().invalidate();
	}

	public AreaReference getUpdateAreaReference() {
		return null;
	}

	public String getWidgetType() {
		return "image";
	}
}