package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

public class Columnlayout extends XulElement
  implements org.zkoss.zkex.zul.api.Columnlayout
{
  public Columnlayout()
  {
//    Runtime.init(this);
  }

  public void beforeChildAdded(Component child, Component refChild) {
    if (!(child instanceof Columnchildren))
      throw new UiException("Unsupported child for Columnlayout: " + child);

    super.beforeChildAdded(child, refChild);
  }

  public String getZclass() {
    return ((this._zclass == null) ? "z-columnlayout" : this._zclass);
  }
}