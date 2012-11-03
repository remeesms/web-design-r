package org.zkoss.poi.ss.usermodel;

import java.util.List;

public abstract interface PivotCache
{
  public abstract Workbook getWorkbook();

  public abstract long getCacheId();

  public abstract SheetSource getSheetSource();

  public abstract List<CacheField> getFields();

  public abstract List<CacheRecord> getRecords();

  public abstract short getRefreshedVersion();

  public abstract short getMinRefreshableVersion();

  public abstract short getCreatedVersion();

  public static abstract interface CacheRecord
  {
    public abstract List<Object> getData();
  }

  public static abstract interface CacheField
  {
    public abstract long getNumberFormatId();

    public abstract void setName(String paramString);

    public abstract String getName();

    public abstract List<Object> getSharedItems();

    public abstract void setDatabaseField(boolean paramBoolean);

    public abstract boolean getDatabaseField();

    public abstract int getFieldGroup();

    public abstract int getGroupBase();

    public abstract List<Integer> getGroupDiscrete();
  }

  public static abstract interface SheetSource
  {
    public abstract String getName();

    public abstract String getRef();
  }
}