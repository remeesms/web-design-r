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
	
	public static final String SERIES = "series";
	public static final String CATEGORIES = "categories";
	public static final String DATASOURCE = "datasource";
	public static final String CATEGORIES_ITEM = "c";
	public static final String SERIES_ITEM = "s";
	public static final String LABEL = "label";
	public static final String SERIES_FORMAT = "seriesFormat";
	public static final String FAKE_SERIES_VALUE = "a";
	public static final String X = "x";
	public static final String Y = "y";
	
	public static final String TICK_TYPE = "tickType";
	public static final String TICK_TYPE_DATETIME = "datetime";
	public static final String TICK_TYPE_MONTH = "month";
	public static final String TICK_TYPE_QUARTER = "quarter";
	public static final String TICK_TYPE_YEAR = "year";
	public static final String TICK_TYPE_CATEGORY = "category";
	
	public Map<String, Object> getClientModel();
}
