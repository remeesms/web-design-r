package org.zkoss.zssex.util;

import java.awt.BasicStroke;
import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.zkoss.poi.ss.usermodel.ChartInfo;
import org.zkoss.zul.CategoryModel;

public class LineChartEngine extends ZssChartEngine
{
  private static final long serialVersionUID = 201011011508L;
  private static final BasicStroke STROKE = new BasicStroke(new Integer(2).intValue());

  public LineChartEngine(org.zkoss.poi.ss.usermodel.Chart poiChart) { super(poiChart); }

  public LineChartEngine(ChartInfo chartInfo) {
    super(chartInfo);
  }
  public boolean prepareJFreeChart(JFreeChart jfchart, org.zkoss.zul.Chart chart) {
    super.prepareJFreeChart(jfchart, chart);
    LineAndShapeRenderer renderer = (LineAndShapeRenderer)((CategoryPlot)jfchart.getPlot()).getRenderer();
    int count = ((CategoryModel)chart.getModel()).getSeries().size();
    for (int j = 0; j < count; j++) {
      renderer.setSeriesStroke(j, STROKE);
      renderer.setSeriesShapesVisible(j, true);
    }
    
    // Process chart text font. FIXME: MENGRAN. Need retrieve from POI-chart.
    chart.setLegendFont(new Font("黑体", Font.PLAIN, 10));
    chart.setXAxisTickFont(new Font("宋体", Font.PLAIN, 10));
    return false;
  }
}