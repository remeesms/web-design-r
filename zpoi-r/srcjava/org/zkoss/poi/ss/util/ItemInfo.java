package org.zkoss.poi.ss.util;

import org.zkoss.poi.ss.usermodel.PivotField;

public class ItemInfo
{
  private Object _value;
  private int _depth;
  private PivotField.Item.Type _type;
  private int _index = -1;

  public ItemInfo(PivotField.Item.Type type, Object value, int depth) {
    this(type, value, depth, -1);
  }

  public ItemInfo(PivotField.Item.Type type, Object value, int depth, int index) {
    this._type = type;
    this._value = value;
    this._depth = depth;
    this._index = index;
  }

  public PivotField.Item.Type getType()
  {
    return this._type;
  }

  public Object getValue()
  {
    return this._value;
  }

  public int getDepth()
  {
    return this._depth;
  }

  public int getIndex() {
    return this._index;
  }
}