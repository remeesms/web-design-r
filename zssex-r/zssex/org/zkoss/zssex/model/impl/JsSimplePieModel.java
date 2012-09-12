package org.zkoss.zssex.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zssex.model.JsChartModel;
import org.zkoss.zssex.util.ChartUtil.DataFormatAnalyzer;
import org.zkoss.zssex.util.ChartUtil.TickTypeAnalyzer;
import org.zkoss.zssex.util.ChartUtil;
import org.zkoss.zssex.util.CommonUtil;
import org.zkoss.zul.SimplePieModel;

public class JsSimplePieModel extends SimplePieModel implements JsChartModel {

	private static final long serialVersionUID = 6362412551929393133L;

	public Map<String, Object> getClientModel() {

		Map<String, Object> clientModel = new HashMap<String, Object>();

		this.fillClientDatasource(clientModel);

		List<String> series = new ArrayList<String>();
		series.add(FAKE_SERIES_VALUE);

		clientModel.put(CATEGORIES, CommonUtil.wrapCollection(this.getCategories(), LABEL));
		/** 目前暂所有都是category */
		clientModel.put(TICK_TYPE, TICK_TYPE_CATEGORY);
//		clientModel.put(TICK_TYPE, ChartUtil.analyzeValues(this.getCategories(), TickTypeAnalyzer.class));
		clientModel.put(SERIES, CommonUtil.wrapCollection(series, LABEL));

		return clientModel;
	}

	private void fillClientDatasource(Map<String, Object> clientModel) {

		List<Comparable<?>> categories = (List<Comparable<?>>) this.getCategories();
		List<Map<String, Object>> categoryDatasource = new ArrayList<Map<String, Object>>();
		DataFormatAnalyzer analyzer = new DataFormatAnalyzer();
		
		for (int j = 0; j < categories.size(); j++) {
			Map<String, Object> csDataMap = new HashMap<String, Object>();
			csDataMap.put(CATEGORIES_ITEM, categories.get(j));
			Number number = this.getValue(categories.get(j));
			if (number != null) {
				csDataMap.put(FAKE_SERIES_VALUE, number);
				analyzer.analyze(number);
			}
			categoryDatasource.add(csDataMap);
		}
		clientModel.put(DATASOURCE, categoryDatasource);
		clientModel.put(SERIES_FORMAT, analyzer.getResult());
	}
}
