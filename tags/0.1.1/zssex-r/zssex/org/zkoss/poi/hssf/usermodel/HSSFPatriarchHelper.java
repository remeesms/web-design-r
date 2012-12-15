package org.zkoss.poi.hssf.usermodel;

import org.zkoss.poi.hssf.record.EscherAggregate;

public class HSSFPatriarchHelper
{
  private HSSFPatriarch _patriarch;

  public HSSFPatriarchHelper(HSSFPatriarch patriarch)
  {
    this._patriarch = patriarch;
  }

  public EscherAggregate getBoundAggregate() {
    return this._patriarch._getBoundAggregate();
  }

  public HSSFSheet getSheet() {
    return this._patriarch._sheet;
  }
}