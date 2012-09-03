package org.zkoss.zssex.ui.widget;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ChartModel;

public interface Table {

	void setType(String type);

	String getType();

	void setTitle(String title);

	String getTitle();

	void setModel(TableModel model);

	TableModel getModel();

	void setWidth(String width);
	
	String getWidth();
	
	void setHeight(String height);
	
	String getHeight();
	
	void setParent(Component component);
	
	Component getParent();

}