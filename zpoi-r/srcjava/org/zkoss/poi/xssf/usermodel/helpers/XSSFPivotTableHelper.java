package org.zkoss.poi.xssf.usermodel.helpers;

import java.util.Collections;
import java.util.List;
import org.zkoss.poi.ss.usermodel.PivotCache;
import org.zkoss.poi.ss.usermodel.PivotTable;
import org.zkoss.poi.ss.usermodel.PivotTableHelper;
import org.zkoss.poi.ss.usermodel.Sheet;
import org.zkoss.poi.ss.usermodel.Workbook;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;

public class XSSFPivotTableHelper
  implements PivotTableHelper
{
  public List<PivotCache> initPivotCaches(Workbook book)
  {
    return Collections.emptyList();
  }
  public PivotCache createPivotCache(AreaReference sourceRef, Workbook book) {
    return null;
  }
  public List<PivotTable> initPivotTables(Sheet sheet) {
    return Collections.emptyList();
  }
  public PivotTable createPivotTable(CellReference destination, String name, PivotCache pivotCache, Sheet sheet) {
    return null;
  }
}