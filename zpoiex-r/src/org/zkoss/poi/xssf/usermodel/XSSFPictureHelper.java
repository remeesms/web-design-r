package org.zkoss.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;

public class XSSFPictureHelper
{
  public static XSSFPicture newXSSFPicture(XSSFDrawing drawing, XSSFClientAnchor anchor, CTPicture picture)
  {
    XSSFPicture pic = new XSSFPicture(drawing, picture);
    pic.anchor = anchor;
    return pic;
  }
}