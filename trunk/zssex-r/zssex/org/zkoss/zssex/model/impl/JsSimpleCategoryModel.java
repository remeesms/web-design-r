package org.zkoss.zssex.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zssex.model.JsChartModel;
import org.zkoss.zssex.util.ChartUtil;
import org.zkoss.zssex.util.ChartUtil.DataFormatAnalyzer;
import org.zkoss.zssex.util.ChartUtil.TickTypeAnalyzer;
import org.zkoss.zssex.util.CommonUtil;
import org.zkoss.zul.SimpleCategoryModel;

public class JsSimpleCategoryModel extends SimpleCategoryModel implements JsChartModel {

	private static final long serialVersionUID = 3520654386588737295L;

	public Map<String, Object> getClientModel() {

		Map<String, Object> clientModel = new HashMap<String, Object>();

		this.fillClientDatasource(clientModel);
		
		clientModel.put(CATEGORIES, CommonUtil.wrapCollection(this.getCategories(), LABEL));
		/** 目前暂所有都是category */
		clientModel.put(TICK_TYPE, TICK_TYPE_CATEGORY);
//		clientModel.put(TICK_TYPE, ChartUtil.analyzeValues(this.getCategories(), TickTypeAnalyzer.class));
		clientModel.put(SERIES, CommonUtil.wrapCollection(this.getSeries(), LABEL));
		
		return clientModel;
	}

	private void fillClientDatasource(Map<String, Object> clientModel) {
		
		List<Map<String, Object>> categoryDatasource = new ArrayList<Map<String, Object>>();
		List<Comparable<?>> categories = (List<Comparable<?>>) this.getCategories();
		List<Comparable<?>> series = (List<Comparable<?>>) this.getSeries();
		DataFormatAnalyzer analyzer = new DataFormatAnalyzer();
		
		for (int j = 0; j < categories.size(); j++) {
			Map<String, Object> csDataMap = new HashMap<String, Object>();
			csDataMap.put(CATEGORIES_ITEM, categories.get(j));
			for (int i = 0; i < series.size(); i++) {
				Number number = this.getValue(series.get(i), categories.get(j));
				if (number != null) {
					csDataMap.put(series.get(i).toString(), number);
					analyzer.analyze(number);
				}
			}
			categoryDatasource.add(csDataMap);
		}
		
		clientModel.put(DATASOURCE, categoryDatasource);
		clientModel.put(SERIES_FORMAT, analyzer.getResult());
	}

}
