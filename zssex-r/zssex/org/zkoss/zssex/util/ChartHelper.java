package org.zkoss.zssex.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.poi.hssf.record.LabelSSTRecord;
import org.zkoss.poi.hssf.record.chart.LinkedDataRecord;
import org.zkoss.poi.hssf.record.chart.PieRecord;
import org.zkoss.poi.hssf.usermodel.HSSFChart;
import org.zkoss.poi.hssf.usermodel.HSSFSheet;
import org.zkoss.poi.hssf.usermodel.HSSFWorkbook;
import org.zkoss.poi.hssf.usermodel.HSSFWorkbookHelper;
import org.zkoss.poi.ss.formula.ptg.Area3DPtg;
import org.zkoss.poi.ss.formula.ptg.AreaPtgBase;
import org.zkoss.poi.ss.formula.ptg.Ptg;
import org.zkoss.poi.ss.formula.ptg.Ref3DPtg;
import org.zkoss.poi.ss.formula.ptg.RefPtgBase;
import org.zkoss.poi.ss.formula.ptg.UnionPtg;
import org.zkoss.poi.ss.usermodel.ChartDrawer;
import org.zkoss.poi.ss.usermodel.ChartInfo;
import org.zkoss.poi.ss.usermodel.ZssChartX;
import org.zkoss.poi.ss.usermodel.charts.CategoryData;
import org.zkoss.poi.ss.usermodel.charts.CategoryDataSerie;
import org.zkoss.poi.ss.usermodel.charts.ChartDataSource;
import org.zkoss.poi.ss.usermodel.charts.ChartDirection;
import org.zkoss.poi.ss.usermodel.charts.ChartGrouping;
import org.zkoss.poi.ss.usermodel.charts.ChartTextSource;
import org.zkoss.poi.ss.usermodel.charts.ChartType;
import org.zkoss.poi.ss.usermodel.charts.XYDataSerie;
import org.zkoss.poi.ss.usermodel.charts.XYZDataSerie;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.poi.xssf.usermodel.XSSFChart;
import org.zkoss.poi.xssf.usermodel.XSSFSheet;
import org.zkoss.poi.xssf.usermodel.charts.XSSFArea3DChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFAreaChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFBar3DChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFBarChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFBubbleChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFColumn3DChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFColumnChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFDoughnutChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFLine3DChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFLineChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFPie3DChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFPieChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFScatChartData;
import org.zkoss.poi.xssf.usermodel.charts.XSSFStockChartData;
import org.zkoss.zss.model.Range;
import org.zkoss.zss.model.Ranges;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zssex.model.impl.JsSimpleCategoryModel;
import org.zkoss.zssex.model.impl.JsSimplePieModel;
import org.zkoss.zssex.model.impl.JsSimpleXYModel;
import org.zkoss.zssex.ui.widget.Chart;
import org.zkoss.zssex.ui.widget.JsChart;
import org.zkoss.zssex.ui.widget.PicChart;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.HiLoModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleHiLoModel;
import org.zkoss.zul.SimpleXYZModel;
import org.zkoss.zul.XYModel;
import org.zkoss.zul.XYZModel;

public class ChartHelper {

	public static final int CHART_LIB_TYPE_JS_CHART = 2;
	public static final int CHART_LIB_TYPE_PIC_CHART = 1;

	public static int emuToPx(int emu) {
		return (int) Math.round(emu * 96.0D / 72.0D / 20.0D / 635.0D);
	}

	public static int pxToEmu(int px) {
		return (int) Math.round(px * 72.0D * 20.0D * 635.0D / 96.0D);
	}

	public static String getChartType(ChartInfo chartInfo) {
		return getHSSFChartType(((HSSFChart) chartInfo).getType(), (HSSFChart) chartInfo);
	}

	private static String getHSSFChartType(HSSFChart.HSSFChartType type, HSSFChart chartInfo) {
		switch (type.ordinal() + 1) {
		case 1:
			return "area";
		case 2:
			return "bar";
		case 3:
			return "line";
		case 4:
			return ((((PieRecord) chartInfo.getShapeRecord()).getPcDonut() == 0) ? "pie" : "ring");
		case 5:
			return "scatter";
		}
		return null;
	}

	public static AreaReference getAreaReference(HSSFSheet sheet, LinkedDataRecord linkedDataRecord) {
		if (linkedDataRecord == null)
			return null;

		return prepareSingleReference(sheet, linkedDataRecord.getFormulaOfLink());
	}

	public static AreaReference prepareSingleReference(HSSFSheet sheet, Ptg[] ptgs) {
		String sheetName = null;
		int firstRow = -1;
		int lastRow = 0;
		int firstCol = 0;
		int lastCol = 0;

		Ptg[] arr$ = ptgs;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; ++i$) {
			int externIndex;
			Ptg ptg = arr$[i$];
			if (ptg instanceof Ref3DPtg) {
				Ref3DPtg areaPtg = (Ref3DPtg) ptg;
				externIndex = areaPtg.getExternSheetIndex();
				sheetName = new HSSFWorkbookHelper(sheet.getWorkbook()).getInternalWorkbook()
						.findSheetNameFromExternSheet(externIndex);
				firstRow = areaPtg.getRow();
				lastRow = firstRow;
				firstCol = areaPtg.getColumn();
				lastCol = firstCol;
			} else if (ptg instanceof Area3DPtg) {
				Area3DPtg areaPtg = (Area3DPtg) ptg;
				externIndex = areaPtg.getExternSheetIndex();
				sheetName = new HSSFWorkbookHelper(sheet.getWorkbook()).getInternalWorkbook()
						.findSheetNameFromExternSheet(externIndex);
				firstRow = areaPtg.getFirstRow();
				lastRow = areaPtg.getLastRow();
				firstCol = areaPtg.getFirstColumn();
				lastCol = areaPtg.getLastColumn();
			} else if (ptg instanceof AreaPtgBase) {
				AreaPtgBase areaPtg = (AreaPtgBase) ptg;
				sheetName = sheet.getSheetName();
				firstRow = areaPtg.getFirstRow();
				lastRow = areaPtg.getLastRow();
				firstCol = areaPtg.getFirstColumn();
				lastCol = areaPtg.getLastColumn();
			} else if (ptg instanceof RefPtgBase) {
				RefPtgBase areaPtg = (RefPtgBase) ptg;
				sheetName = sheet.getSheetName();
				firstRow = areaPtg.getRow();
				lastRow = firstRow;
				firstCol = areaPtg.getColumn();
				lastCol = firstCol;
			}
		}
		if (firstRow >= 0) {
			CellReference c1 = new CellReference(sheetName, firstRow, firstCol, true, true);
			CellReference c2 = new CellReference(lastRow, lastCol, true, true);
			return new AreaReference(c1, c2);
		}
		return null;
	}

	public static Chart createChart(ChartInfo chartInfo, int chartLibType) {

		if (chartLibType == CHART_LIB_TYPE_JS_CHART) {
			// TODO
			return new JsChart();

		} else {
			PicChart chart = new PicChart();
			String type = getChartType(chartInfo);
//			if ("line".equals(type))
//				chart.setEngine(new LineChartEngine(chartInfo));
//			else if (("pie".equals(type)) || ("ring".equals(type)))
//				chart.setEngine(new PieChartEngine(chartInfo));
//			else
//				chart.setEngine(new ZssChartEngine(chartInfo));

			return (Chart) chart;
		}

	}

	public static String[] getLiterals(HSSFWorkbook book, LabelSSTRecord[] labelRecords) {
		String[] lits = new String[labelRecords.length];
		for (int j = 0; j < lits.length; ++j) {
			lits[j] = new HSSFWorkbookHelper(book).getInternalWorkbook().getSSTString(labelRecords[j].getSSTIndex())
					.getString();
		}

		return lits;
	}

	public static boolean isContiguous(Ptg[] ptgs) {
		for (int j = 0; j < ptgs.length; ++j)
			if (ptgs[j] instanceof UnionPtg)
				return false;

		return true;
	}

	public static String getChartType(org.zkoss.poi.ss.usermodel.Chart poiChart, ChartGrouping grouping) {
		XSSFChart xssfChart = (XSSFChart) poiChart;
		switch (xssfChart.getChartType().ordinal() + 1) {
		case 1:
		case 2:
			return ((grouping == ChartGrouping.STACKED) ? "stacked_area" : "area");
		case 3:
		case 4:
			return ((grouping == ChartGrouping.STACKED) ? "stacked_bar" : "bar");
		case 5:
			return "bubble";
		case 6:
		case 7:
			return "column";
		case 8:
			return "ring";
		case 9:
		case 10:
			return "line";
		case 11:
			return "ofpie";
		case 12:
		case 13:
			return "pie";
		case 14:
			return "radar";
		case 15:
			return "scatter";
		case 16:
			return "candlestick";
		case 17:
		case 18:
			return "surface";
		}
		return "";
	}

	public static String getChartType(org.zkoss.poi.ss.usermodel.Chart poiChart) {
		XSSFChart xssfChart = (XSSFChart) poiChart;
		switch (xssfChart.getChartType().ordinal() + 1) {
		case 1:
		case 2:
			return "area";
		case 3:
		case 4:
		case 5:
			return "bar";
		case 6:
		case 7:
			return "column";
		case 8:
			return "ring";
		case 9:
		case 10:
			return "line";
		case 11:
			return "ofpie";
		case 12:
		case 13:
			return "pie";
		case 14:
			return "radar";
		case 15:
			return "scatter";
		case 16:
			return "candlestick";
		case 17:
		case 18:
			return "surface";
		}
		return "";
	}

	public static Chart createChart(org.zkoss.poi.ss.usermodel.Chart poiChart, int chartLibType) {

		if (chartLibType == CHART_LIB_TYPE_JS_CHART) {
			// TODO
			return new JsChart();

		} else {
			PicChart chart = new PicChart();
			String type = getChartType(poiChart);
//			if ("line".equals(type))
//				chart.setEngine(new LineChartEngine(poiChart));
//			else if (("pie".equals(type)) || ("ring".equals(type)))
//				chart.setEngine(new PieChartEngine(poiChart));
//			else
//				chart.setEngine(new ZssChartEngine(poiChart));
			return (Chart) chart;
		}
	}

	public static void drawChart(ChartDrawer drawer, Chart zchart, Worksheet sheet, ZssChartX poiChart) {
		if (sheet instanceof HSSFSheet)
			drawHSSFChart(drawer, zchart, sheet, poiChart);
		else
			drawXSSFChart(drawer, zchart, sheet, poiChart);
	}
	
	private static void drawHSSFChart(ChartDrawer drawer, Chart chart, Worksheet sheet, ZssChartX chartX) {
		HSSFChart chartInfo = (HSSFChart) chartX.getChartInfo();
		HSSFChart.HSSFChartType type = chartInfo.getType();
		HSSFChart.HSSFSeries[] series = chartInfo.getSeries();
		ChartModel model = null;
		switch (type.ordinal() + 1) {
		case 1:
			model = prepareAreaModel(drawer, (HSSFSheet) sheet, series);
			break;
		case 2:
			model = prepareBarModel(drawer, (HSSFSheet) sheet, series);
			break;
		case 3:
			model = prepareLineModel(drawer, (HSSFSheet) sheet, series);
			break;
		case 4:
			model = preparePieModel(drawer, (HSSFSheet) sheet, series);
			break;
		case 5:
			model = prepareScatterModel(drawer, (HSSFSheet) sheet, series);
		}

		if (!(drawer.prepareChart(chart, model, chartInfo)))
			drawChart0(drawer, (HSSFSheet) sheet, chart, model, chartInfo);
	}

	private static ChartModel prepareScatterModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		return prepareXYModel(drawer, sheet, series);
	}

	private static ChartModel prepareAreaModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		return prepareCategoryModel(drawer, sheet, series);
	}

	private static ChartModel prepareBarModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		return prepareCategoryModel(drawer, sheet, series);
	}

	private static ChartModel prepareLineModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		return prepareCategoryModel(drawer, sheet, series);
	}

	private static ChartModel prepareCategoryModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		CategoryModel model = new JsSimpleCategoryModel();

		for (HSSFChart.HSSFSeries ser : series) {
			String title = prepareTitle(drawer, sheet, ser, -1);
			String[] labels = prepareLabels(drawer, sheet, ser);
			Number[] values = prepareValues(drawer, sheet, ser);
			for (int j = 0; j < values.length; ++j)
				model.setValue(title, labels[j], values[j]);
		}

		return model;
	}

	private static XYModel prepareXYModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		XYModel model = new JsSimpleXYModel();
		int sj = 1;
		for (HSSFChart.HSSFSeries ser : series) {
			String title = prepareTitle(drawer, sheet, ser, sj);
			Number[] xs = prepareXValues(drawer, sheet, ser);
			Number[] ys = prepareValues(drawer, sheet, ser);
			for (int j = 0; j < ys.length; ++j)
				model.addValue(title, xs[j], ys[j]);
		}

		return model;
	}

	private static PieModel preparePieModel(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries[] series) {
		PieModel model = new JsSimplePieModel();

		for (HSSFChart.HSSFSeries ser : series) {
			String[] labels = prepareLabels(drawer, sheet, ser);
			Number[] values = prepareValues(drawer, sheet, ser);
			for (int j = 0; j < values.length; ++j)
				model.setValue(labels[j], values[j]);
		}

		return model;
	}

	private static void drawChart0(ChartDrawer drawer, HSSFSheet sheet, Chart chart, ChartModel model,
			ChartInfo chartInfo) {
		chart.setType(getChartType(chartInfo));
		chart.setTitle(getChartTitle(drawer, sheet, chartInfo));
		chart.setThreeD(isThreeD(chartInfo));
		chart.setFgAlpha(128);
		chart.setModel(model);
	}

	private static String getChartTitle(ChartDrawer drawer, HSSFSheet sheet, ChartInfo chartInfo) {
		boolean autoTitleDeleted = ((HSSFChart) chartInfo).isAutoTitleDeleted();
		if (!(autoTitleDeleted)) {
			String title = ((HSSFChart) chartInfo).getChartTitle();
			String type = getChartType(chartInfo);
			return ((("pie".equals(type)) || ("ring".equals(type))) ? getFirstSeriesTitle(drawer, sheet, chartInfo)
					: (title != null) ? title : null);
		}
		return null;
	}

	private static String getFirstSeriesTitle(ChartDrawer drawer, HSSFSheet sheet, ChartInfo chartInfo) {
		HSSFChart.HSSFSeries[] series = ((HSSFChart) chartInfo).getSeries();
		if (series.length == 0)
			return null;

		return prepareTitle(drawer, sheet, series[0], -1);
	}

	private static String prepareTitle(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries ser, int sj) {
		AreaReference dataname = getAreaReference(sheet, ser.getDataName());
		if (dataname == null) {
			String title = ser.getSeriesTitle();
			return ((title == null) ? "" : (sj >= 0) ? "Series" + sj : title);
		}
		CellReference dc1 = dataname.getFirstCell();
		CellReference dc2 = dataname.getLastCell();
		int dcol1 = dc1.getCol();
		int drow1 = dc1.getRow();
		int dcol2 = dc2.getCol();
		int drow2 = dc2.getRow();
		drawer.prepareUpdateAreaReference(dcol1, drow1, dcol2, drow2);
		String sheetName = dc1.getSheetName();
		Worksheet csheet = (Worksheet) ((sheetName != null) ? sheet.getWorkbook().getSheet(sheetName) : sheet);
		Range rng = Ranges.range(csheet, drow1, dcol1);
		return rng.getText().getString();
	}

	private static String[] prepareLabels(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries ser) {
		short num = ser.getNumValues();
		AreaReference categories = getAreaReference(sheet, ser.getDataCategoryLabels());
		if (categories == null)
			return getLiterals(sheet.getWorkbook(), ser.getDataCategoryLabelLiterals());

		CellReference c1 = categories.getFirstCell();
		CellReference c2 = categories.getLastCell();
		int col1 = c1.getCol();
		int row1 = c1.getRow();
		int col2 = c2.getCol();
		int row2 = c2.getRow();
		drawer.prepareUpdateAreaReference(col1, row1, col2, row2);
		String sheetName = c1.getSheetName();
		Worksheet csheet = (Worksheet) ((sheetName != null) ? sheet.getWorkbook().getSheet(sheetName) : sheet);
		String[] labels = new String[num];
		int r = row1;
		for (int j = 0; r <= row2; ++r)
			for (int c = col1; c <= col2; ++c) {
				Range rng = Ranges.range(csheet, r, c);
				labels[(j++)] = rng.getText().getString();
			}

		return labels;
	}

	private static Number[] prepareXValues(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries ser) {
		int len = ser.getSeries().getNumValues();
		Number[] values = new Number[len];
		for (int j = 0; j < len; ++j)
			values[j] = Integer.valueOf(j + 1);

		return values;
	}

	private static Number[] prepareValues(ChartDrawer drawer, HSSFSheet sheet, HSSFChart.HSSFSeries ser) {
		LinkedDataRecord valueRecord = ser.getDataValues();
		if (valueRecord == null)
			return new Double[0];

		Ptg[] ptgs = valueRecord.getFormulaOfLink();

		if (isContiguous(ptgs))
			return prepareSingleReferenceValues(drawer, sheet, ptgs);

		Number[][] nums = new Number[ptgs.length][];
		int count = 0;
		int total = 0;
		for (int j = 0; j < ptgs.length; ++j) {
			Ptg ptg = ptgs[j];
			Number[] value = prepareSingleReferenceValues(drawer, sheet, new Ptg[] { ptg });
			if (value != null) {
				nums[(count++)] = value;
				total += value.length;
			}
		}
		Number[] vals = new Number[total];
		int j = 0;
		for (int k = 0; j < count; ++j) {
			int len = nums[j].length;
			System.arraycopy(nums[j], 0, vals, k, len);
			k += len;
		}
		return vals;
	}

	private static Number[] prepareSingleReferenceValues(ChartDrawer drawer, HSSFSheet sheet, Ptg[] ptgs) {
		AreaReference values = prepareSingleReference(sheet, ptgs);
		if (values == null)
			return null;

		return prepareSingleReferenceValues(drawer, (Worksheet) sheet, values);
	}

	private static boolean isThreeD(ChartInfo chartInfo) {
		return (((HSSFChart) chartInfo).getChart3D() != null);
	}

	private static void drawXSSFChart(ChartDrawer drawer, Chart chart, Worksheet sheet, ZssChartX _chartX) {
		XSSFChart xssfChart = (XSSFChart) _chartX.getChart();
		ChartModel model = null;
		ChartGrouping grouping = ChartGrouping.STANDARD;
		ChartDirection barDir = ChartDirection.VERTICAL;
		switch (xssfChart.getChartType().ordinal() + 1) {
		case 12:
			XSSFPie3DChartData data13 = new XSSFPie3DChartData(xssfChart);
			model = preparePieModel(drawer, (XSSFSheet) sheet, data13.getSeries());
			break;
		case 13:
			XSSFPieChartData data12 = new XSSFPieChartData(xssfChart);
			model = preparePieModel(drawer, (XSSFSheet) sheet, data12.getSeries());
			break;
		case 8:
			XSSFDoughnutChartData data8 = new XSSFDoughnutChartData(xssfChart);
			model = preparePieModel(drawer, (XSSFSheet) sheet, data8.getSeries());
			break;
		case 3:
			XSSFBar3DChartData data4 = new XSSFBar3DChartData(xssfChart);
			model = prepareBarModel(drawer, (XSSFSheet) sheet, data4.getSeries());
			grouping = data4.getGrouping();
			barDir = data4.getBarDirection();
			break;
		case 7:
			XSSFColumn3DChartData data6 = new XSSFColumn3DChartData(xssfChart);
			model = prepareBarModel(drawer, (XSSFSheet) sheet, data6.getSeries());
			grouping = data6.getGrouping();
			barDir = data6.getBarDirection();
			break;
		case 4:
			XSSFBarChartData data3 = new XSSFBarChartData(xssfChart);
			model = prepareBarModel(drawer, (XSSFSheet) sheet, data3.getSeries());
			grouping = data3.getGrouping();
			barDir = data3.getBarDirection();
			break;
		case 6:
			XSSFColumnChartData data5 = new XSSFColumnChartData(xssfChart);
			model = prepareBarModel(drawer, (XSSFSheet) sheet, data5.getSeries());
			grouping = data5.getGrouping();
			barDir = data5.getBarDirection();
			break;
		case 9:
			XSSFLine3DChartData data10 = new XSSFLine3DChartData(xssfChart);
			model = prepareLineModel(drawer, (XSSFSheet) sheet, data10.getSeries());
			break;
		case 10:
			XSSFLineChartData data9 = new XSSFLineChartData(xssfChart);
			model = prepareLineModel(drawer, (XSSFSheet) sheet, data9.getSeries());
			break;
		case 2:
			XSSFAreaChartData data1 = new XSSFAreaChartData(xssfChart);
			model = prepareAreaModel(drawer, (XSSFSheet) sheet, data1.getSeries());
			break;
		case 1:
			XSSFArea3DChartData data2 = new XSSFArea3DChartData(xssfChart);
			model = prepareAreaModel(drawer, (XSSFSheet) sheet, data2.getSeries());
			break;
		case 15:
			XSSFScatChartData data15 = new XSSFScatChartData(xssfChart);
			model = prepareScatterModel(drawer, (XSSFSheet) sheet, data15.getSeries());
			break;
		case 5:
			XSSFBubbleChartData data7 = new XSSFBubbleChartData(xssfChart);
			model = prepareBubbleModel(drawer, (XSSFSheet) sheet, data7.getSeries());
			break;
		case 16:
			XSSFStockChartData data16 = new XSSFStockChartData(xssfChart);
			model = prepareStockModel(drawer, (XSSFSheet) sheet, data16.getSeries());
		case 11:
		case 14:
		case 17:
		case 18:
		}

		if ((model != null) && (!(drawer.prepareChart(chart, model, xssfChart))))
			drawChart1(drawer, (XSSFSheet) sheet, chart, model, xssfChart, grouping, barDir);
	}

	private static ChartModel prepareBarModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends CategoryDataSerie> series) {
		return prepareCategoryModel(drawer, sheet, series);
	}

	private static ChartModel prepareLineModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends CategoryDataSerie> series) {
		return prepareCategoryModel(drawer, sheet, series);
	}

	private static ChartModel prepareAreaModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends CategoryDataSerie> series) {
		return prepareCategoryModel(drawer, sheet, series);
	}

	private static ChartModel prepareScatterModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends XYDataSerie> series) {
		return prepareXYModel(drawer, sheet, series);
	}

	private static ChartModel prepareBubbleModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends XYZDataSerie> series) {
		return prepareXYZModel(drawer, sheet, series);
	}

	private static ChartModel prepareStockModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends CategoryDataSerie> series) {
		HiLoModel model = new SimpleHiLoModel();
		CategoryDataSerie vols = (CategoryDataSerie) series.get(0);
		CategoryDataSerie opens = (CategoryDataSerie) series.get(1);
		CategoryDataSerie highs = (CategoryDataSerie) series.get(2);
		CategoryDataSerie lows = (CategoryDataSerie) series.get(3);
		CategoryDataSerie closes = (CategoryDataSerie) series.get(4);

		Number[] volvals = prepareValues(drawer, sheet, vols.getValues());
		Number[] openvals = prepareValues(drawer, sheet, opens.getValues());
		Number[] highvals = prepareValues(drawer, sheet, highs.getValues());
		Number[] lowvals = prepareValues(drawer, sheet, lows.getValues());
		Number[] closevals = prepareValues(drawer, sheet, closes.getValues());
		String[] labels = prepareLabels(drawer, sheet, opens.getCategories(), highvals.length);

		for (int j = 0; j < openvals.length; ++j)
			model.addValue(null, openvals[j], highvals[j], lowvals[j], closevals[j], volvals[j]);

		return model;
	}

	private static ChartModel prepareCategoryModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends CategoryDataSerie> series) {
		String title;
		Number[] vals;
		String[] labels;
		int j;
		CategoryModel model = new JsSimpleCategoryModel();
		int sj = 1;
		for (CategoryDataSerie ser : series) {
			title = prepareTitle(drawer, sheet, ser.getTitle(), sj++);
			vals = prepareValues(drawer, sheet, ser.getValues());
			labels = prepareLabels(drawer, sheet, ser.getCategories(), vals.length);
			for (j = 0; j < vals.length; ++j)
				model.setValue(title, labels[j], vals[j]);
		}

		return model;
	}

	private static XYModel prepareXYModel(ChartDrawer drawer, XSSFSheet sheet, List<? extends XYDataSerie> series) {
		String title;
		Number[] xs;
		Number[] ys;
		int j;
		XYModel model = new JsSimpleXYModel();
		int sj = 1;
		for (XYDataSerie ser : series) {
			title = prepareTitle(drawer, sheet, ser.getTitle(), sj++);
			xs = prepareValues(drawer, sheet, ser.getXs());
			ys = prepareValues(drawer, sheet, ser.getYs());
			for (j = 0; j < ys.length && j < xs.length; ++j)
				model.addValue(title, xs[j], ys[j]);
		}

		return model;
	}

	private static XYModel prepareXYZModel(ChartDrawer drawer, XSSFSheet sheet, List<? extends XYZDataSerie> series) {
		String title;
		Number[] xs;
		Number[] ys;
		Number[] zs;
		int j;
		XYZModel model = new SimpleXYZModel();
		int sj = 1;
		for (XYZDataSerie ser : series) {
			title = prepareTitle(drawer, sheet, ser.getTitle(), sj++);
			xs = prepareValues(drawer, sheet, ser.getXs());
			ys = prepareValues(drawer, sheet, ser.getYs());
			zs = prepareValues(drawer, sheet, ser.getZs());
			for (j = 0; j < ys.length; ++j)
				model.addValue(title, xs[j], ys[j], zs[j]);
		}

		return model;
	}

	private static ChartModel preparePieModel(ChartDrawer drawer, XSSFSheet sheet,
			List<? extends CategoryDataSerie> series) {
		Number[] vals;
		String[] labels;
		int j;
		PieModel model = new JsSimplePieModel();

		for (CategoryDataSerie ser : series) {
			vals = prepareValues(drawer, sheet, ser.getValues());
			labels = prepareLabels(drawer, sheet, ser.getCategories(), vals.length);
			for (j = 0; j < vals.length; ++j)
				model.setValue(labels[j], vals[j]);
		}

		return model;
	}

	private static void drawChart1(ChartDrawer drawer, XSSFSheet sheet, Chart chart, ChartModel model,
			org.zkoss.poi.ss.usermodel.Chart poiChart, ChartGrouping grouping, ChartDirection dir) {
		chart.setType(getChartType(poiChart, grouping));
		chart.setTitle(getChartTitle(drawer, sheet, poiChart));
		chart.setThreeD(isThreeD(poiChart));
		chart.setFgAlpha(128);
		if (dir != null)
			chart.setOrient((dir == ChartDirection.HORIZONTAL) ? "horizontal" : "vertical");

		chart.setModel(model);
	}

	private static boolean isThreeD(org.zkoss.poi.ss.usermodel.Chart poiChart) {
		return ((XSSFChart) poiChart).isSetView3D();
	}

	private static String getChartTitle(ChartDrawer drawer, XSSFSheet sheet, org.zkoss.poi.ss.usermodel.Chart poiChart) {
		XSSFChart xssfChart = (XSSFChart) poiChart;
		boolean autoTitleDeleted = xssfChart.isAutoTitleDeleted();
		if (!(autoTitleDeleted)) {
			String title = xssfChart.getChartTitle();
			String type = getChartType(poiChart);
			return ((("pie".equals(type)) || ("ring".equals(type))) ? getFirstSeriesTitle(drawer, sheet, poiChart)
					: (!(Strings.isEmpty(title))) ? title : null);
		}
		return null;
	}

	private static String getFirstSeriesTitle(ChartDrawer drawer, XSSFSheet sheet,
			org.zkoss.poi.ss.usermodel.Chart poiChart) {
		CategoryData data;
		XSSFChart xssfChart = (XSSFChart) poiChart;
		ChartType type = xssfChart.getChartType();

		switch (type.ordinal() + 1) {
		case 12:
			data = new XSSFPie3DChartData(xssfChart);
			if (data.getSeries().size() == 0) {
				return null;
			}
			return getFristSeriesTitle(drawer, sheet, (CategoryDataSerie) data.getSeries().get(0));
		case 13:
			data = new XSSFPieChartData(xssfChart);
			if (data.getSeries().size() == 0) {
				return null;
			}
			return getFristSeriesTitle(drawer, sheet, (CategoryDataSerie) data.getSeries().get(0));
		case 8:
			data = new XSSFDoughnutChartData(xssfChart);
			if (data.getSeries().size() == 0) {
				return null;
			}
			return getFristSeriesTitle(drawer, sheet, (CategoryDataSerie) data.getSeries().get(0));
		}

		return null;
	}

	private static String getFristSeriesTitle(ChartDrawer drawer, XSSFSheet sheet, CategoryDataSerie serie) {
		ChartTextSource text = serie.getTitle();
		return ((text == null) ? "" : prepareTitle(drawer, sheet, text, 1));
	}

	private static String prepareTitle(ChartDrawer drawer, XSSFSheet sheet, ChartTextSource text, int sj) {
		if (text == null)
			return "Series" + sj;
		if (text.isReference()) {
			String titleRef = text.getFormulaString();
			if ((titleRef.startsWith("(")) && (titleRef.endsWith(")")))
				titleRef = titleRef.substring(1, titleRef.length() - 1);

			if (AreaReference.isContiguous(titleRef))
				return prepareSingleReferenceTitle(drawer, (Worksheet) sheet, new AreaReference(titleRef));

			AreaReference[] refs = AreaReference.generateContiguous(titleRef);
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < refs.length; ++j)
				sb.append(prepareSingleReferenceTitle(drawer, (Worksheet) sheet, refs[j]));

			return sb.toString();
		}

		return text.getTextString();
	}

	private static String prepareSingleReferenceTitle(ChartDrawer drawer, Worksheet sheet, AreaReference refs) {
		CellReference c1 = refs.getFirstCell();
		CellReference c2 = refs.getLastCell();
		String sheetName = c1.getSheetName();
		int col1 = c1.getCol();
		int row1 = c1.getRow();
		int col2 = c2.getCol();
		int row2 = c2.getRow();
		Worksheet csheet = (Worksheet) ((sheetName != null) ? sheet.getWorkbook().getSheet(sheetName) : sheet);
		drawer.prepareUpdateAreaReference(col1, row1, col2, row2);
		Range rng = Ranges.range(csheet, row1, col1);
		return rng.getText().getString();
	}

	private static Number[] prepareValues(ChartDrawer drawer, XSSFSheet sheet, ChartDataSource<? extends Number> vals) {
		if (vals.isReference()) {
			String valRef = vals.getFormulaString();
			if ((valRef.startsWith("(")) && (valRef.endsWith(")")))
				valRef = valRef.substring(1, valRef.length() - 1);

			if (AreaReference.isContiguous(valRef))
				return prepareSingleReferenceValues(drawer, (Worksheet) sheet, new AreaReference(valRef));

			AreaReference[] refs = AreaReference.generateContiguous(valRef);
			Number[][] numbers = new Number[refs.length][];
			int total = 0;
			for (int j = 0; j < refs.length; ++j) {
				numbers[j] = prepareSingleReferenceValues(drawer, (Worksheet) sheet, refs[j]);
				total += numbers[j].length;
			}
			Number[] values = new Number[total];
			int j = 0;
			for (int k = 0; j < refs.length; ++j) {
				int len = numbers[j].length;
				System.arraycopy(numbers[j], 0, values, k, len);
				k += len;
			}
			return values;
		}

		int len = vals.getPointCount();
		Number[] values = new Number[len];
		for (int j = 0; j < len; ++j)
			values[j] = ((Number) vals.getPointAt(j));

		return values;
	}

	private static String[] prepareLabels(ChartDrawer drawer, XSSFSheet sheet, ChartDataSource<?> cats, int size) {
		if (cats.isReference()) {
			String catRef = cats.getFormulaString();
			if ((catRef.startsWith("(")) && (catRef.endsWith(")")))
				catRef = catRef.substring(1, catRef.length() - 1);

			if (AreaReference.isContiguous(catRef))
				return prepareSingleReferenceLabels(drawer, (Worksheet) sheet, new AreaReference(catRef));

			AreaReference[] refs = AreaReference.generateContiguous(catRef);
			String[][] labelA = new String[refs.length][];
			int total = 0;
			for (int j = 0; j < refs.length; ++j) {
				labelA[j] = prepareSingleReferenceLabels(drawer, (Worksheet) sheet, refs[j]);
				total += labelA[j].length;
			}
			String[] labels = new String[total];
			int j = 0;
			for (int k = 0; j < refs.length; ++j) {
				int len = labelA[j].length;
				System.arraycopy(labelA[j], 0, labels, k, len);
				k += len;
			}
			return labels;
		}

		int num = cats.getPointCount();
		if (num > size)
			size = num;

		String[] labels = new String[size];
		int j = 0;
		for (; j < num; ++j)
			labels[j] = ((String) cats.getPointAt(j));

		for (; j < size; ++j)
			labels[j] = "" + (j + 1);

		return labels;
	}

	private static String[] prepareSingleReferenceLabels(ChartDrawer drawer, Worksheet sheet, AreaReference refs) {
		CellReference c1 = refs.getFirstCell();
		CellReference c2 = refs.getLastCell();
		String sheetName = c1.getSheetName();
		int col1 = c1.getCol();
		int row1 = c1.getRow();
		int col2 = c2.getCol();
		int row2 = c2.getRow();
		Worksheet csheet = (Worksheet) ((sheetName != null) ? sheet.getWorkbook().getSheet(sheetName) : sheet);
		drawer.prepareUpdateAreaReference(col1, row1, col2, row2);
		int len = (col2 - col1 + 1) * (row2 - row1 + 1);
		String[] labels = new String[len];
		int r = row1;
		for (int j = 0; r <= row2; ++r)
			for (int c = col1; c <= col2; ++c) {
				Range rng = Ranges.range(csheet, r, c);
				labels[(j++)] = rng.getText().getString();
			}

		return labels;
	}

	private static Number[] prepareSingleReferenceValues(ChartDrawer drawer, Worksheet sheet, AreaReference values) {
		CellReference vc1 = values.getFirstCell();
		CellReference vc2 = values.getLastCell();
		int vcol1 = vc1.getCol();
		int vrow1 = vc1.getRow();
		int vcol2 = vc2.getCol();
		int vrow2 = vc2.getRow();
		String sheetName = vc1.getSheetName();
		Worksheet csheet = (Worksheet) ((sheetName != null) ? sheet.getWorkbook().getSheet(sheetName) : sheet);
		drawer.prepareUpdateAreaReference(vcol1, vrow1, vcol2, vrow2);
		int num = (vcol2 - vcol1 + 1) * (vrow2 - vrow1 + 1);
		Number[] vals = new Number[num];
		int r = vrow1;
		for (int j = 0; r <= vrow2; ++r)
			for (int c = vcol1; c <= vcol2; ++c) {
				Range rng = Ranges.range(csheet, r, c);
				Object obj = rng.getValue();
				Number db = new Double(0.0D);
				if (obj instanceof String) {
					String txtstr = (String) obj;
					try {
						db = new Double(txtstr);
					} catch (NumberFormatException ex) {
					}
				} else {
					if (obj instanceof Byte)
						break;

					if (obj instanceof Boolean)
						db = new Double(0.0D);
					else if (obj instanceof Number)
						db = (Number) obj;
				}
				vals[(j++)] = db;
			}

		return vals;
	}
}