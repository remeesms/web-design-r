package org.zkoss.poi.hssf.usermodel;

import java.lang.reflect.Field;
import org.zkoss.lang.Classes;
import org.zkoss.poi.xssf.usermodel.XSSFPivotCache;
import org.zkoss.poi.xssf.usermodel.XSSFPivotCacheRecords;
import org.zkoss.poi.xssf.usermodel.XSSFPivotTable;
import org.zkoss.poi.xssf.usermodel.XSSFRelation;

public class PivotTableHelper
{
  static
  {
    Field fd = null;
    try {
      fd = Classes.getAnyField(XSSFRelation.class, "_cls");
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    boolean old = fd.isAccessible();
    try {
      fd.setAccessible(true);
      fd.set(XSSFRelation.PIVOT_TABLE, XSSFPivotTable.class);
      fd.set(XSSFRelation.PIVOT_CACHE_DEFINITION, XSSFPivotCache.class);
      fd.set(XSSFRelation.PIVOT_CACHE_RECORDS, XSSFPivotCacheRecords.class);
    } catch (IllegalAccessException e) {
    }
    finally {
      fd.setAccessible(old);
    }
  }
}