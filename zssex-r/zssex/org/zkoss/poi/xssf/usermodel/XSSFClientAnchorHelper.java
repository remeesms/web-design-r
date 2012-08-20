package org.zkoss.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

public class XSSFClientAnchorHelper
{
  public static XSSFClientAnchor newXSSFClientAnchor(CTMarker from, CTMarker to)
  {
    return new XSSFClientAnchor(from, to);
  }
}