package org.zkoss.zssex.ui.widget;

import org.zkoss.poi.ss.usermodel.ChartDrawer;
import org.zkoss.poi.ss.usermodel.ChartInfo;
import org.zkoss.poi.ss.usermodel.ClientAnchor;
import org.zkoss.poi.ss.usermodel.ZssChartX;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zssex.util.ChartHelper;
import org.zkoss.zssex.ui.widget.Chart;
import org.zkoss.zul.ChartModel;

public class ChartWidget extends BaseWidget implements ChartDrawer {

	private int _chartLibType = ChartHelper.CHART_LIB_TYPE_JS_CHART;
//	private int _chartLibType = ChartHelper.CHART_LIB_TYPE_PIC_CHART;

	private Worksheet _sheet;
	private int _zindex;
	private ZssChartX _chartX;
	private Chart _chart;
	private int _outcol1;
	private int _outcol2;
	private int _outrow1;
	private int _outrow2;

	private Chart inner() {
		return this._chart;
	}

	public ChartWidget(Worksheet sheet, ZssChartX chartX, int zindex) {
		this._sheet = sheet;
		this._chartX = chartX;
		this._zindex = zindex;
		Chart chart = this._chart;

		this.createChart();

		setId(this._chartX.getChartId());
		setSizable(true);
		setMovable(true);
		setCtrlKeys("#del");
	}

	protected Component getInnerComponent() {
		return (Component) inner();
	}

	public String getType() {
		return inner().getType();
	}

	public void setType(String type) {
		inner().setType(type);
	}

	public String getTitle() {
		return inner().getTitle();
	}

	public void setTitle(String title) {
		inner().setTitle(title);
	}

	public boolean isThreeD() {
		return inner().isThreeD();
	}

	public void setThreeD(boolean b) {
		inner().setThreeD(b);
	}

	public void setWidth(String w) {
		inner().setWidth(w);
	}

	public String getWidth() {
		return inner().getWidth();
	}

	public void setHeight(String h) {
		inner().setHeight(h);
	}

	public String getHeight() {
		return inner().getHeight();
	}

	private void initUpdateAreaReference() {
		this._outcol1 = 2147483647;
		this._outrow1 = 2147483647;
		this._outcol2 = -2147483648;
		this._outrow2 = -2147483648;
	}

	public void prepareUpdateAreaReference(int col1, int row1, int col2,
			int row2) {
		if (this._outcol1 > col1)
			this._outcol1 = col1;

		if (this._outcol2 < col2)
			this._outcol2 = col2;

		if (this._outrow1 > row1)
			this._outrow1 = row1;

		if (this._outrow2 < row2)
			this._outrow2 = row2;
	}

	private void createChart() {

		this._chart = newChart0();
		if (this._chart == null)
			this._chart = newChart1();
		if (this._chart != null) {
			initChart();
		}
	}

	private Chart newChart0() {
		ChartInfo chartInfo = this._chartX.getChartInfo();
		if (chartInfo == null)
			return null;

		Chart chart = ChartHelper.createChart(chartInfo, this._chartLibType);
		chart.setParent(getCtrl());
		return chart;
	}

	private Chart newChart1() {
		org.zkoss.poi.ss.usermodel.Chart poiChart = this._chartX.getChart();
		if (poiChart == null)
			return null;

		Chart chart = ChartHelper.createChart(poiChart, this._chartLibType);
		chart.setParent(getCtrl());
		return chart;
	}

	public void invalidate() {
		if (this._chart != null)
			initChart();
	}

	public AreaReference getUpdateAreaReference() {
		CellReference c1 = new CellReference(this._outrow1, this._outcol1,
				true, true);
		CellReference c2 = new CellReference(this._outrow2, this._outcol2,
				true, true);
		return new AreaReference(c1, c2);
	}

	private void prepareAnchorPosition() {
		ClientAnchor anchor = this._chartX.getPreferredSize();
		if (anchor != null) {
			int row = anchor.getRow1();
			int col = anchor.getCol1();
			int row2 = anchor.getRow2();
			int col2 = anchor.getCol2();
			int height = DefaultBookWidgetLoader.getHeightInPx(this._sheet,
					anchor);
			int width = DefaultBookWidgetLoader.getWidthInPx(this._sheet,
					anchor);
			int left = DefaultBookWidgetLoader.getLeftFraction(this._sheet,
					anchor);
			int top = DefaultBookWidgetLoader.getTopFraction(this._sheet,
					anchor);
			setRow(row);
			setColumn(col);
			setRow2(row2);
			setColumn2(col2);
			setZindex(this._zindex);
			setWidth((width - 1)+ "px");
			setHeight((height - 1)+ "px");
			setLeft(left - 1);
			setTop(top - 1);
		}
	}

	private void initChart() {
		initUpdateAreaReference();
		prepareAnchorPosition();
		ChartHelper.drawChart(this, this._chart, this._sheet, this._chartX);
	}

	public boolean prepareChart(Chart chart, ChartModel model,
			ChartInfo chartInfo) {
		return false;
	}

	public boolean prepareChart(Chart chart, ChartModel model,
			org.zkoss.poi.ss.usermodel.Chart poiChart) {
		return false;
	}

	public String getWidgetType() {
		return "chart";
	}
}