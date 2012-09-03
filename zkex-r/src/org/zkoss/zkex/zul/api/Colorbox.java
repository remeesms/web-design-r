package org.zkoss.zkex.zul.api;

import org.zkoss.zul.impl.api.XulElement;

public abstract interface Colorbox extends XulElement
{
  public abstract void setColor(String paramString);

  public abstract String getColor();

  public abstract void setValue(String paramString);

  public abstract String getValue();

  public abstract int getRGB();

  public abstract boolean isDisabled();

  public abstract void setDisabled(boolean paramBoolean);
}