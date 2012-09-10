package org.zkoss.zssex.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zssex.model.JsChartModel;
import org.zkoss.zul.SimplePieModel;

public class JsSimplePieModel extends SimplePieModel implements JsChartModel {

	private static final long serialVersionUID = 6362412551929393133L;

	private static final String FAKE_SERIES = "a";

	public Map<String, Object> getClientModel() {

		Map<String, Object> clientModel = new HashMap<String, Object>();

		List<Map<String, Object>> categoryDatasource = new ArrayList<Map<String, Object>>();
		this.fillClientDatasource(categoryDatasource);

		List<String> series = new ArrayList<String>();
		series.add(FAKE_SERIES);

		clientModel.put("categories", this.getCategories());
		clientModel.put("series", series);
		clientModel.put("datasource", categoryDatasource);

		return clientModel;
	}

	private void fillClientDatasource(List<Map<String, Object>> categoryDatasource) {

		List<Comparable<?>> categories = (List<Comparable<?>>) this.getCategories();

		for (int j = 0; j < categories.size(); j++) {
			Map<String, Object> csDataMap = new HashMap<String, Object>();
			csDataMap.put("c", categories.get(j));
			Number number = this.getValue(categories.get(j));
			if (number != null) {
				csDataMap.put(FAKE_SERIES, number);
			}
			categoryDatasource.add(csDataMap);
		}
	}
}
