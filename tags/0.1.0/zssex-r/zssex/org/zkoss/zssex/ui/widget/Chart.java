package org.zkoss.zssex.ui.widget;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ChartModel;

public interface Chart {

	void setType(String type);

	String getType();

	void setTitle(String title);

	String getTitle();

	void setThreeD(boolean isThreeD);
	
	boolean isThreeD();

	void setFgAlpha(int alpha);

	int getFgAlpha();

	void setModel(ChartModel model);

	ChartModel getModel();

	void setOrient(String orient);

	String getOrient();
	
	void setWidth(String width);
	
	String getWidth();
	
	void setHeight(String height);
	
	String getHeight();
	
	void setParent(Component component);
	
	Component getParent();

}