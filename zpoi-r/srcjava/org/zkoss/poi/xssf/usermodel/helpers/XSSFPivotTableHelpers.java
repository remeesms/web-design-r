package org.zkoss.poi.xssf.usermodel.helpers;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.poi.ss.usermodel.PivotTableHelper;

public class XSSFPivotTableHelpers
{
  public static final XSSFPivotTableHelpers instance = new XSSFPivotTableHelpers();
  private PivotTableHelper _helper;

  public PivotTableHelper getHelper()
  {
    if (this._helper != null)
      return this._helper;
    String clsStr = Library.getProperty("org.zkoss.poi.ss.usermodel.PivotTableHelper.class");
    if (clsStr != null)
      try {
        Class cls = Classes.forNameByThread(clsStr);
        this._helper = ((PivotTableHelper)cls.newInstance());
        return this._helper;
      }
      catch (ClassNotFoundException e)
      {
      }
      catch (IllegalAccessException e) {
      }
      catch (InstantiationException e) {
      }
    this._helper = new XSSFPivotTableHelper();
    return this._helper;
  }
}