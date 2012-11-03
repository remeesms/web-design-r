package org.zkoss.zkex.zul.api;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.api.XulElement;

public abstract interface Fisheyebar extends XulElement
{
  public abstract int getItemWidth();

  public abstract void setItemWidth(int paramInt)
    throws WrongValueException;

  public abstract int getItemHeight();

  public abstract void setItemHeight(int paramInt)
    throws WrongValueException;

  public abstract int getItemMaxWidth();

  public abstract void setItemMaxWidth(int paramInt)
    throws WrongValueException;

  public abstract int getItemMaxHeight();

  public abstract void setItemMaxHeight(int paramInt)
    throws WrongValueException;

  public abstract int getItemPadding();

  public abstract void setItemPadding(int paramInt)
    throws WrongValueException;

  public abstract String getOrient();

  public abstract void setOrient(String paramString)
    throws WrongValueException;

  public abstract String getAttachEdge();

  public abstract void setAttachEdge(String paramString)
    throws WrongValueException;

  public abstract String getLabelEdge();

  public abstract void setLabelEdge(String paramString)
    throws WrongValueException;
}