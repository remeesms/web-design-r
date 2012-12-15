package org.zkoss.zkex.zul.impl;

import org.zkoss.zk.ui.Desktop;

public abstract interface Operation
{
  public abstract void execute(Desktop paramDesktop);

  public abstract void failToExecute(Desktop paramDesktop);
}