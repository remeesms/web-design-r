package org.zkoss.poi.ss.usermodel;

public abstract interface DataField
{
  public abstract PivotField getPivotField();

  public abstract String getName();

  public abstract Calculation getSubtotal();
}