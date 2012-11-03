package org.zkoss.poi.ss.usermodel;

import org.zkoss.zul.ChartModel;

public abstract interface ChartDrawer
{
  public abstract void prepareUpdateAreaReference(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract boolean prepareChart(org.zkoss.zssex.ui.widget.Chart paramChart, ChartModel paramChartModel, ChartInfo paramChartInfo);

  public abstract boolean prepareChart(org.zkoss.zssex.ui.widget.Chart paramChart, ChartModel paramChartModel, Chart paramChart1);
}