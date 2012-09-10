/* ChartModel.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 03 11:22:44     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zssex.model;

import java.util.Map;

import org.zkoss.zssex.ui.widget.Chart;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.XYModel;
import org.zkoss.zul.XYZModel;

/**
 * @see Chart
 * @see PieModel
 * @see CategoryModel
 * @see XYModel
 * @see XYZModel
 * @see org.zkoss.zul.event.ChartAreaListener
 */
public interface JsChartModel {
	
	public Map<String, Object> getClientModel();
}
