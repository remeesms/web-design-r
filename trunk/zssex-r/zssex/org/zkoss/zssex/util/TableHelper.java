package org.zkoss.zssex.util;

import org.zkoss.poi.hssf.usermodel.HSSFSheet;
import org.zkoss.poi.ss.usermodel.PivotTable;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zssex.ui.widget.SimpleTable;
import org.zkoss.zssex.ui.widget.Table;

public class TableHelper {

	public static Table createTable(PivotTable poiTable) {
		// TODO
		return new SimpleTable();
	}

	public static void drawTable(Table table, Worksheet sheet, PivotTable poiTable) {
		if (sheet instanceof HSSFSheet) {
			drawHSSFTable(table, sheet, poiTable);
		} else {
			drawXSSFTable(table, sheet, poiTable);
		}
	}

	private static void drawHSSFTable(Table table, Worksheet sheet, PivotTable poiTable) {
		// TODO
	}

	private static void drawXSSFTable(Table table, Worksheet sheet, PivotTable poiTable) {
		// TODO
		/*
		 * XSSFStockChartData data16 = new XSSFStockChartData(xssfChart); model
		 * = prepareStockModel(drawer, (XSSFSheet) sheet, data16 .getSeries());
		 */
//		if ((model != null) && (!(drawer.prepareChart(chart, model, xssfChart))))
//			drawChart1(drawer, (XSSFSheet) sheet, chart, model, xssfChart, grouping, barDir);
	}
}
