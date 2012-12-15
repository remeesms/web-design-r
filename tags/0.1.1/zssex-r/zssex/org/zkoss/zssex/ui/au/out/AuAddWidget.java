package org.zkoss.zssex.ui.au.out;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.util.DeferredValue;
import org.zkoss.zss.ui.Spreadsheet;

public class AuAddWidget extends AuResponse
{
  public AuAddWidget(Spreadsheet sheet, String sheetid, String widgetUuid, DeferredValue deffhtml)
  {
    super("setAttr", sheet, new Object[] { sheet.getUuid(), "addWidget", new Object[] { sheetid, widgetUuid, deffhtml.getValue() } });
  }
}