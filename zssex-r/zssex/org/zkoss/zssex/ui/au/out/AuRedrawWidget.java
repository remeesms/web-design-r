package org.zkoss.zssex.ui.au.out;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zss.ui.Spreadsheet;

public class AuRedrawWidget extends AuResponse
{
  public AuRedrawWidget(Spreadsheet sheet, String sheetid, String widgetUuid)
  {
    super("setAttr", sheet, new Object[] { sheet.getUuid(), "redrawWidget", new Object[] { sheetid, widgetUuid } });
  }
}