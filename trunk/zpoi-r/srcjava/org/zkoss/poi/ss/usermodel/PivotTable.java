package org.zkoss.poi.ss.usermodel;

import java.util.List;
import org.zkoss.poi.ss.util.AreaReference;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.poi.ss.util.ItemInfo;

public abstract interface PivotTable
{
  public abstract void setName(String paramString);

  public abstract String getName();

  public abstract long getCacheId();

  public abstract void setGrandTotalCaption(String paramString);

  public abstract String getGrandTotalCaption();

  public abstract void setDataCaption(String paramString);

  public abstract String getDataCaption();

  public abstract void setRowHeaderCaption(String paramString);

  public abstract String getRowHeaderCaption();

  public abstract void setFirstHeaderRow(int paramInt);

  public abstract void setFirstData(int paramInt1, int paramInt2);

  public abstract CellReference getFirstDataRef();

  public abstract void setLocationRef(AreaReference paramAreaReference);

  public abstract AreaReference getLocationRef();

  public abstract PivotCache getPivotCache();

  public abstract PivotField getPivotField(String paramString);

  public abstract List<PivotField> getPivotFields();

  public abstract void setRowField(PivotField paramPivotField);

  public abstract List<PivotField> getRowFields();

  public abstract void setRowItems(List<List<ItemInfo>> paramList);

  public abstract void setColumnField(PivotField paramPivotField);

  public abstract List<PivotField> getColumnFields();

  public abstract void setColumnItems(List<List<ItemInfo>> paramList);

  public abstract void setDataField(PivotField paramPivotField, String paramString, Calculation paramCalculation);

  public abstract List<DataField> getDataFields();

  public abstract void setDataOnRows(boolean paramBoolean);

  public abstract boolean getDataOnRows();

  public abstract void setOutline(boolean paramBoolean);

  public abstract boolean getOutline();

  public abstract void setOutlineData(boolean paramBoolean);

  public abstract boolean getOutlineData();
}