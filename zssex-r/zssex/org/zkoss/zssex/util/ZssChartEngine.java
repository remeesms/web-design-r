package org.zkoss.zssex.util;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.zkoss.poi.hssf.usermodel.HSSFChart;
import org.zkoss.poi.ss.usermodel.ChartInfo;
import org.zkoss.poi.xssf.usermodel.XSSFChart;
import org.zkoss.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;

public class ZssChartEngine extends JFreeChartEngine
{
  protected final org.zkoss.poi.ss.usermodel.Chart _poiChart;
  protected final ChartInfo _chartInfo;
  private static final long serialVersionUID = 201011021111L;
  static final int POS_B = 1;
  static final int POS_TR = 2;
  static final int POS_L = 3;
  static final int POS_R = 4;
  static final int POS_T = 5;

  public ZssChartEngine(ChartInfo chartInfo)
  {
    this._chartInfo = chartInfo;
    this._poiChart = null;
  }

  public ZssChartEngine(org.zkoss.poi.ss.usermodel.Chart poiChart) {
    this._chartInfo = null;
    this._poiChart = poiChart; }

  public boolean prepareJFreeChart(JFreeChart jfchart, org.zkoss.zul.Chart chart) {
    chart.setPaneColor("#FFFFFF");
    chart.setSclass("chartborder");

    jfchart.getPlot().setOutlineVisible(false);
    jfchart.getLegend().setFrame(BlockBorder.NONE);
    RectangleEdge edge = null;
    switch (getLegendPos())
    {
    case 1:
      edge = RectangleEdge.BOTTOM;
      break;
    case 3:
      edge = RectangleEdge.LEFT;
      break;
    case 5:
      edge = RectangleEdge.TOP;
      break;
    case 2:
    case 4:
    default:
      edge = RectangleEdge.RIGHT;
    }

    jfchart.getLegend().setPosition(edge);
    return false;
  }

  private int getLegendPos() {
    if (this._poiChart instanceof XSSFChart) {
      XSSFChart xssfChart = (XSSFChart)this._poiChart;
      XSSFChartLegend legend = xssfChart.getOrCreateLegend();
      switch (legend.getPosition().ordinal()) {
      case 1:
        return 1;
      case 2:
        return 3;
      case 3:
      default:
        return 4;
      case 4:
        return 5;
      case 5: }
      return 2;
    }

    switch (((HSSFChart)this._chartInfo).getLegendPos()) {
    case 0:
      return 1;
    case 4:
      return 3;
    case 2:
      return 5;
    case 1:
      return 2;
    case 3:
    }
    return 4;
  }
}