package org.zkoss.zkex.zul.impl;

import org.zkoss.zk.ui.Desktop;

public abstract interface OperationQueueListener
{
  public abstract void queueUnavailable(Desktop paramDesktop);
}