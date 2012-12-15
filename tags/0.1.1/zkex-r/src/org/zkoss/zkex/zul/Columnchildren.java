package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Panel;
import org.zkoss.zul.impl.XulElement;

public class Columnchildren extends XulElement
  implements org.zkoss.zkex.zul.api.Columnchildren
{
  public Columnchildren()
  {
//    Runtime.init(this);
  }

  public String getZclass() {
    return ((this._zclass == null) ? "z-columnchildren" : this._zclass);
  }

  public void beforeParentChanged(Component parent) {
    if ((parent != null) && (!(parent instanceof Columnlayout)))
      throw new UiException("Wrong parent: " + parent);
    super.beforeParentChanged(parent); }

  public void beforeChildAdded(Component child, Component refChild) {
    if (!(child instanceof Panel))
      throw new UiException("Unsupported child for Columnchildren: " + child);

    super.beforeChildAdded(child, refChild);
  }
}