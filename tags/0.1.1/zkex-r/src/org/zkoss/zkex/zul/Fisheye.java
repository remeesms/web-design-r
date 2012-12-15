package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

public class Fisheye extends LabelImageElement
  implements org.zkoss.zkex.zul.api.Fisheye
{
  public Fisheye()
  {
  }

  public Fisheye(String label, String image)
  {
    super(label, image);
  }

  public String getZclass()
  {
    return ((this._zclass == null) ? "z-fisheye" : this._zclass);
  }

  public void setWidth(String width)
  {
    throw new UnsupportedOperationException("readonly");
  }

  public void setHeight(String height)
  {
    throw new UnsupportedOperationException("readonly");
  }

  public void beforeParentChanged(Component parent) {
    if ((parent != null) && (!(parent instanceof Fisheyebar)))
      throw new UiException("Unsupported parent for fisheye: " + parent);
    super.beforeParentChanged(parent);
  }

  protected boolean isChildable() {
    return false;
  }
}