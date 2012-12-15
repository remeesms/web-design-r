package org.zkoss.poi.ss.usermodel;

import java.util.List;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;

public abstract interface PivotTableHelper
{
  public static final String CLASS = "org.zkoss.poi.ss.usermodel.PivotTableHelper.class";

  public abstract List<PivotCache> initPivotCaches(Workbook paramWorkbook);

  public abstract PivotCache createPivotCache(AreaReference paramAreaReference, Workbook paramWorkbook);

  public abstract List<PivotTable> initPivotTables(Sheet paramSheet);

  public abstract PivotTable createPivotTable(CellReference paramCellReference, String paramString, PivotCache paramPivotCache, Sheet paramSheet);
}