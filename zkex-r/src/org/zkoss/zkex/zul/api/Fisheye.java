package org.zkoss.zkex.zul.api;

import org.zkoss.zul.impl.api.XulElement;

public abstract interface Fisheye extends XulElement
{
  public abstract String getLabel();

  public abstract void setLabel(String paramString);

  public abstract String getImage();

  public abstract void setImage(String paramString);
}