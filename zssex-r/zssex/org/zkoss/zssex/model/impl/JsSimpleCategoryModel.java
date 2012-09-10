package org.zkoss.zssex.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zssex.model.JsChartModel;
import org.zkoss.zul.SimpleCategoryModel;

public class JsSimpleCategoryModel extends SimpleCategoryModel implements JsChartModel {

	private static final long serialVersionUID = 3520654386588737295L;

	public Map<String, Object> getClientModel() {

		Map<String, Object> clientModel = new HashMap<String, Object>();

		List<Map<String, Object>> categoryDatasource = new ArrayList<Map<String, Object>>();
		this.fillClientDatasource(categoryDatasource);
		
		clientModel.put("categories", this.getCategories());
		clientModel.put("series", this.getSeries());
		clientModel.put("datasource", categoryDatasource);
		
		return clientModel;
	}

	private void fillClientDatasource(List<Map<String, Object>> categoryDatasource) {
		
		List<Comparable<?>> categories = (List<Comparable<?>>) this.getCategories();
		List<Comparable<?>> series = (List<Comparable<?>>) this.getSeries();
		
		for (int j = 0; j < categories.size(); j++) {
			Map<String, Object> csDataMap = new HashMap<String, Object>();
			csDataMap.put("c", categories.get(j));
			for (int i = 0; i < series.size(); i++) {
				Number number = this.getValue(series.get(i), categories.get(j));
				if (number != null) {
					csDataMap.put(series.get(i).toString(), number);
				}
			}
			categoryDatasource.add(csDataMap);
		}
	}
}
