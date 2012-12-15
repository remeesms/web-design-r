package org.zkoss.zssex.model.impl;

import java.util.List;
import org.zkoss.poi.ss.usermodel.ZssChartX;
import org.zkoss.poi.xssf.usermodel.XSSFChartX;
import org.zkoss.zss.model.Book;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.model.impl.DrawingManager;

public class SheetCtrlImpl extends org.zkoss.zss.model.impl.SheetCtrlImpl
{
  private DrawingManager _drawingManager;

  public SheetCtrlImpl(Book book, Worksheet sheet)
  {
    super(book, sheet);
  }

  public void whenRenameSheet(String oldname, String newname) {
    DrawingManager dm = getDrawingManager();
    List<ZssChartX> charts = dm.getChartXs();
    for (ZssChartX chart : charts)
      if (chart instanceof XSSFChartX)
        ((XSSFChartX)chart).renameSheet(oldname, newname);
      else
        throw new RuntimeException("HSSFChartX not implmeented yet!");
  }

  public DrawingManager getDrawingManager()
  {
    if (this._drawingManager == null)
      this._drawingManager = new DrawingManagerImpl(this._sheet);

    return this._drawingManager;
  }
}