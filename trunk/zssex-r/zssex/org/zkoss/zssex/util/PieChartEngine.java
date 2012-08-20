package org.zkoss.zssex.util;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.zkoss.poi.ss.usermodel.ChartInfo;

public class PieChartEngine extends ZssChartEngine
{
  private static final long serialVersionUID = 201011011426L;

  public PieChartEngine(org.zkoss.poi.ss.usermodel.Chart poiChart)
  {
    super(poiChart); }

  public PieChartEngine(ChartInfo chartInfo) {
    super(chartInfo); }

  public boolean prepareJFreeChart(JFreeChart jfchart, org.zkoss.zul.Chart chart) {
    super.prepareJFreeChart(jfchart, chart);
    PiePlot plot = (PiePlot)jfchart.getPlot();
    plot.setLabelGenerator(null);
    return false;
  }
}