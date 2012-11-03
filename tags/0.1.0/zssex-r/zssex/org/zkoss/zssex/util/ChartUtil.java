package org.zkoss.zssex.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.zkoss.zssex.model.JsChartModel;

public class ChartUtil {

	public static interface ValueAnalyzer {
		public void analyze(Object value);

		public String getResult();
	}

	/**
	 * 根据数据集，获得其展现格式类型。（如："I,III.DD%"）
	 */
	public static class DataFormatAnalyzer implements ValueAnalyzer {
		private boolean isPercent = false;
		private boolean isFraction = false;
		private int maxFractionLength = 0;

		public void analyze(Object value) {
			if (value == null) {
				return;
			}
			String valueStr = value.toString().trim();
			if (valueStr.contains("%")) {
				isPercent = true;
			}
			valueStr.replace("%", "");
			int dotIndex = valueStr.lastIndexOf(".");
			if (dotIndex >= 0) {
				isFraction = true;
				int fractionLength = valueStr.length() - dotIndex - 1;
				maxFractionLength = fractionLength > maxFractionLength ? fractionLength : maxFractionLength;
			}
		}

		public String getResult() {
			StringBuilder dataFormat = new StringBuilder();
			dataFormat.append("I,III");
			if (isFraction) {
				dataFormat.append(".");
				for (int i = 0; i < maxFractionLength; i++) {
					dataFormat.append("D");
				}
			}
			if (isPercent) {
				dataFormat.append("%");
			}
			return dataFormat.toString();
		}
	}

	/**
	 * 根据数据集，获得其轴的tick类型
	 * 因为excel中格式众多，可自定义。
	 * 暂定：只有如下格式的日期可以被识别成日期类型，其余一律当作category处理。
	 */
	public static class TickTypeAnalyzer implements ValueAnalyzer {
		private Set<String> tickTypeSet = new HashSet<String>();
		private static Pattern datetimePattern = Pattern.compile("[0-9]{1,4}/[0-9]{1,2}/[0-9]{1,2}");
		private static Pattern monthPattern = Pattern.compile("[0-9]{1,4}/[0-9]{1,2}");
		private static Pattern quarterPattern = Pattern.compile("[0-9]{1,4}/Q[1-4]");
		private static Pattern yearPattern = Pattern.compile("[0-9]{1,4}");
		
		public void analyze(Object value) {
			if (value == null) {
				return;
			}
			String valueStr = value.toString().trim();
			if (datetimePattern.matcher(valueStr).matches()) {
				tickTypeSet.add(JsChartModel.TICK_TYPE_DATETIME);
			} else if (monthPattern.matcher(valueStr).matches()) {
				tickTypeSet.add(JsChartModel.TICK_TYPE_MONTH);
			} else if (quarterPattern.matcher(valueStr).matches()) {
				tickTypeSet.add(JsChartModel.TICK_TYPE_QUARTER);
			} else if (yearPattern.matcher(valueStr).matches()) {
				tickTypeSet.add(JsChartModel.TICK_TYPE_YEAR);
			} else {
				tickTypeSet.add(JsChartModel.TICK_TYPE_CATEGORY);
			}
		}

		public String getResult() {
			if (tickTypeSet.size() > 1 || tickTypeSet.size() == 0) {
				return JsChartModel.CATEGORIES; // defualt;
			} else {
				return tickTypeSet.iterator().next();
			}
		}
	}
	
	/**
	 * 根据数据集，获得其展现格式类型。（如："I,III.DD%"）
	 */
	@SuppressWarnings("rawtypes")
	public static String analyzeValues(Collection values, Class analyzerClass) {
		if (values == null || values.size() == 0) {
			return "";
		}
		try {
			ValueAnalyzer analyzer = (ValueAnalyzer) analyzerClass.newInstance();
			Iterator<?> it = values.iterator();
			while (it.hasNext()) {
				analyzer.analyze(it.next());
			}
			return analyzer.getResult();
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
