package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.ListModel;

public abstract interface ListModelSharer
{
  public abstract ListModel getProxy(Desktop paramDesktop);

  public abstract int getProxyCount();
}