package org.zkoss.zssex.util;

import org.zkoss.poi.hssf.usermodel.HSSFSheet;
import org.zkoss.poi.ss.formula.eval.NotImplementedException;
import org.zkoss.poi.ss.usermodel.ZssTableX;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.poi.xssf.usermodel.XSSFTable;
import org.zkoss.poi.xssf.usermodel.XSSFTableX;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zssex.model.TableModel;
import org.zkoss.zssex.model.impl.SimpleTableModel;
import org.zkoss.zssex.ui.widget.SimpleTable;
import org.zkoss.zssex.ui.widget.Table;

public class TableHelper {

	public static Table createTable(ZssTableX tableX) {
		return new SimpleTable();
	}

	public static void drawTable(Table table, Worksheet sheet, ZssTableX tableX) {
		if (sheet instanceof HSSFSheet) {
			drawHSSFTable(table, (HSSFSheet)sheet, tableX);
		} else {
			drawXSSFTable(table, (XSSFSheet)sheet, tableX);
		}
	}

	private static void drawHSSFTable(Table table, HSSFSheet sheet, ZssTableX tableX) {
		throw new NotImplementedException("not support HSSF table");
	}

	private static void drawXSSFTable(Table table, XSSFSheet sheet, ZssTableX tableX) {
		table.setModel(prepareSimpleTableModel(sheet, tableX));
	}
	
	private static TableModel prepareSimpleTableModel(XSSFSheet sheet, ZssTableX tableX) {
		XSSFTable table = ((XSSFTableX)tableX).getTable();
		TableModel model = new SimpleTableModel(); // FIXME
		// TODO 根据XSSFTable创造SimpleTableModelImpl
		return model;
	}
}
