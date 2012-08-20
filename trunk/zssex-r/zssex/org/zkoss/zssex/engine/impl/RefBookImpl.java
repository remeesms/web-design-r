package org.zkoss.zssex.engine.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zss.engine.RefBook;
//import org.zkoss.zssex.rt.Runtime;

public class RefBookImpl extends org.zkoss.zss.engine.impl.RefBookImpl
  implements RefBook
{
  private EventQueue _queue;
  private LinkedHashMap<EventListener, DelegateListener> _listeners = new LinkedHashMap();

  public RefBookImpl(String paramString, int paramInt1, int paramInt2)
  {
    super(paramString, paramInt1, paramInt2);
  }

  protected EventQueue getEventQueue()
  {
    if (this._queue == null)
      this._queue = EventQueues.lookup(getBookName(), (this._scope == null) ? "desktop" : this._scope, true);
    return this._queue;
  }

  private void renewEventQueue(String paramString)
  {
    DelegateListener localDelegateListener;
    Iterator localIterator = this._listeners.values().iterator();
    while (localIterator.hasNext())
    {
      localDelegateListener = (DelegateListener)localIterator.next();
      super.unsubscribe(localDelegateListener);
    }
    EventQueues.remove(getBookName(), (this._scope == null) ? "desktop" : this._scope);
    this._queue = null;
    this._scope = paramString;
    if (this._listeners != null)
    {
      localIterator = this._listeners.values().iterator();
      while (localIterator.hasNext())
      {
        localDelegateListener = (DelegateListener)localIterator.next();
        super.subscribe(localDelegateListener);
      }
    }
  }

  private void disableEventQueue()
  {
    Iterator localIterator = this._listeners.values().iterator();
    while (localIterator.hasNext())
    {
      DelegateListener localDelegateListener = (DelegateListener)localIterator.next();
      super.unsubscribe(localDelegateListener);
    }
  }

  public void setShareScope(String paramString)
  {
    if ("disable".equals(paramString))
      disableEventQueue();
    else
      renewEventQueue(paramString);
  }

  public void subscribe(EventListener paramEventListener)
  {
    DelegateListener localDelegateListener = new DelegateListener(paramEventListener);
    super.subscribe(localDelegateListener);
    this._listeners.put(paramEventListener, localDelegateListener);
  }

  public void unsubscribe(EventListener paramEventListener)
  {
    DelegateListener localDelegateListener = (DelegateListener)this._listeners.get(paramEventListener);
    if (localDelegateListener != null)
    {
      super.unsubscribe(localDelegateListener);
      this._listeners.remove(paramEventListener);
    }
    else
    {
      throw new RuntimeException("Oops! Try to unsubscribe an EventListener not subcribed! " + paramEventListener);
    }
  }

  private static class DelegateListener
    implements EventListener
  {
    private EventListener _listener;

    public DelegateListener(EventListener paramEventListener)
    {
      this._listener = paramEventListener;
    }

    public void onEvent(Event paramEvent)
      throws Exception
    {
//      if (!(Runtime.token(Executions.getCurrent())))
//      {
//        init(this);
//        return;
//      }
      init(Integer.valueOf(12));
      this._listener.onEvent(paramEvent);
    }

    private final void init(Object paramObject)
    {
//      Object localObject;
//      if (equals(paramObject))
//      {
//        localObject = Executions.getCurrent();
//        if (localObject != null)
//        {
//          Desktop localDesktop = ((Execution)localObject).getDesktop();
//          if (localDesktop != null)
//          {
//            WebApp localWebApp = localDesktop.getWebApp();
//            if (localWebApp != null)
//              Runtime.init(localWebApp);
//          }
//        }
//      }
//      else
//      {
//        localObject = Sessions.getCurrent();
//        if (localObject != null)
//          Runtime.init(localObject);
//      }
    }

    public int hashCode()
    {
      return this._listener.hashCode();
    }

    public boolean equals(Object paramObject)
    {
      if (paramObject instanceof DelegateListener)
        return super.equals(paramObject);
      return this._listener.equals(paramObject);
    }
  }
}