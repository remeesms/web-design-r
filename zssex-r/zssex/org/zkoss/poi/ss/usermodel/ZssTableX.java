package org.zkoss.poi.ss.usermodel;

import org.zkoss.poi.ss.util.AreaReference;

public interface ZssTableX {

	AreaReference getPreferredSize();

	String getTableId();
	
	void setClientAnchor(AreaReference anchor);
	
}
