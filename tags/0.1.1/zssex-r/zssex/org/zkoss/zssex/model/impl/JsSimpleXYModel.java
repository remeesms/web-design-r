package org.zkoss.zssex.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zssex.model.JsChartModel;
import org.zkoss.zssex.util.ChartUtil.DataFormatAnalyzer;
import org.zkoss.zssex.util.CommonUtil;
import org.zkoss.zul.SimpleXYModel;

public class JsSimpleXYModel extends SimpleXYModel implements JsChartModel {

	private static final long serialVersionUID = 896341510208058684L;

	public Map<String, Object> getClientModel() {

		Map<String, Object> clientModel = new HashMap<String, Object>();
		this.fillClientDatasource(clientModel);
		
		clientModel.put(SERIES, CommonUtil.wrapCollection(this.getSeries(), LABEL));
		
		return clientModel;
	}

	private void fillClientDatasource(Map<String, Object> clientModel) {
		List<Comparable> series = (List<Comparable>) this.getSeries();
		Map<String, Object> xyDatasource = new HashMap<String, Object>();
		DataFormatAnalyzer analyzer = new DataFormatAnalyzer();
		
		for (int i = 0; i < series.size(); i++) {
			int dataCount = this.getDataCount(series.get(i));
			List<Map<String, Object>> sList = new ArrayList<Map<String, Object>>(series.size());
			for (int j = 0; j < dataCount; j++) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				Number x = this.getX(series.get(i), j);
				Number y = this.getY(series.get(i), j);
				dataMap.put(X, x);
				dataMap.put(Y, y);
				analyzer.analyze(x);
				analyzer.analyze(y);
				sList.add(dataMap);
			}
			xyDatasource.put(series.get(i).toString(), sList);
		}	
		clientModel.put(DATASOURCE, xyDatasource);
		clientModel.put(SERIES_FORMAT, analyzer.getResult());
	}
}
