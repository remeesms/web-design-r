package org.zkoss.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDateTime;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDiscretePr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFieldGroup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGroupItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIndex;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumber;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSharedItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTString;
import org.zkoss.poi.ss.usermodel.PivotCache.CacheField;

public class XSSFCacheField implements CacheField
{
  private final CTCacheField _cacheField;

  XSSFCacheField(CTCacheField cacheField)
  {
    this._cacheField = cacheField;
    this._cacheField.getDatabaseField();
  }

  public void setName(String name) {
    this._cacheField.setName(name);
  }

  public String getName() {
    return this._cacheField.getName();
  }

  private static void addDates(List<CTDateTime> from, List<Object> to) {
    if (from != null)
      for (CTDateTime e : from)
        to.add(e.getV());
  }

  private static void addNumbers(List<CTNumber> from, List<Object> to)
  {
    if (from != null)
      for (CTNumber e : from)
        to.add(Double.valueOf(e.getV()));
  }

  private static void addStrings(List<CTString> from, List<Object> to)
  {
    if (from != null)
      for (CTString e : from)
        to.add(e.getV());
  }

  public List<Object> getSharedItems()
  {
    ArrayList list = new ArrayList();

    if (this._cacheField.getDatabaseField()) {
      CTSharedItems sharedItems = this._cacheField.getSharedItems();
      if (sharedItems != null)
      {
        if (sharedItems.getContainsBlank())
          list.add(null);

        addDates(sharedItems.getDList(), list);
        addNumbers(sharedItems.getNList(), list);
        addStrings(sharedItems.getSList(), list);
        return list;
      }
    } else {
      CTFieldGroup fieldGroup = this._cacheField.getFieldGroup();
      if (fieldGroup != null) {
        CTGroupItems groupItems = fieldGroup.getGroupItems();
        addDates(groupItems.getDList(), list);
        addNumbers(groupItems.getNList(), list);
        addStrings(groupItems.getSList(), list);
        return list;
      }
    }
    return list;
  }

  public void setDatabaseField(boolean databaseField) {
    this._cacheField.setDatabaseField(databaseField);
  }

  public boolean getDatabaseField() {
    return this._cacheField.getDatabaseField();
  }

  public List<Integer> getGroupDiscrete() {
    CTFieldGroup fieldGroup = this._cacheField.getFieldGroup();
    if (fieldGroup == null) {
      return null;
    }

    List list = new ArrayList();
    CTDiscretePr discretePr = fieldGroup.getDiscretePr();
    for (CTIndex i : discretePr.getXList())
      list.add(Integer.valueOf((int)i.getV()));

    return list;
  }

  public int getFieldGroup() {
    CTFieldGroup fieldGroup = this._cacheField.getFieldGroup();
    if (fieldGroup == null) {
      return -1;
    }

    return (int)fieldGroup.getPar();
  }

  public int getGroupBase() {
    CTFieldGroup fieldGroup = this._cacheField.getFieldGroup();
    if (fieldGroup == null) {
      return -1;
    }

    return (int)fieldGroup.getBase();
  }

  public long getNumberFormatId() {
    return this._cacheField.getNumFmtId();
  }
}