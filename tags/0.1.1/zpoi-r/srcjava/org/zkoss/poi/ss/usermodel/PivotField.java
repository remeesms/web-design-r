package org.zkoss.poi.ss.usermodel;

import java.util.List;
import java.util.Set;

public abstract interface PivotField
{
  public abstract void setItems(List<Object> paramList);

  public abstract List<Item> getItems();

  public abstract FieldGroup getFieldGroup();

  public abstract void setType(Type paramType);

  public abstract Type getType();

  public abstract void setName(String paramString);

  public abstract String getName();

  public abstract void setDefaultSubtotal(boolean paramBoolean);

  public abstract boolean getDefaultSubtotal();

  public abstract void setSubtotals(Set<Calculation> paramSet);

  public abstract Set<Calculation> getSubtotals();

  public abstract void setSortType(SortType paramSortType);

  public abstract SortType getSortType();

  public abstract boolean getDatabaseField();

  public abstract void setOutline(boolean paramBoolean);

  public abstract boolean getOutline();

  public static abstract interface FieldGroup
  {
    public abstract PivotField.Item getItem();

    public abstract List<Object> getItems();

    public abstract Set<Object> getGroup();

    public abstract PivotField getBase();

    public abstract FieldGroup getParent();
  }

  public static abstract interface Item
  {
    public abstract void setHide(boolean paramBoolean);

    public abstract boolean getHide();

    public abstract Object getValue();

    public abstract void setShowDetail(boolean paramBoolean);

    public abstract boolean getShowDetail();

    public abstract void setType(Type paramType);

    public abstract Type getType();

    public static enum Type
    {
      AVERAGE, 
      BLANK, 
      COUNT_NUMS, 
      COUNT, 
      DATA, 
      DEFAULT, 
      GRAND, 
      MAX, 
      MIN, 
      PRODUCT, 
      STD_DEV, 
      STD_DEV_P, 
      SUM, 
      VARIANCE, 
      VARIANCE_P;
    }
  }

  public static enum SortType
  {
    ASCENDING, 
    DESCENDING, 
    MANUAL;
  }

  public static enum Type
  {
    ROW, 
    COLUMN, 
    DATA;
  }
}