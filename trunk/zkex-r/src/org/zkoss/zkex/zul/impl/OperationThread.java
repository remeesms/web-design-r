package org.zkoss.zkex.zul.impl;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;

public class OperationThread extends Thread
{
  private static final String DESKTOP_KEY = "zkex:opthread";
  private boolean _running = true;
  private OperationQueue _queue;
  private Desktop _desktop;
  private long _activateTimeout = 10000L;
  private long _waitTimeout = 10000L;
  private int _maxFailCount = 4;
  private static final Log log = Log.lookup(OperationThread.class);

  OperationThread(Desktop desktop)
  {
    this._desktop = desktop;
    this._queue = new OperationQueue();
    setName("OPThread-" + desktop.getId());
  }

  OperationQueue getQueue() {
    return this._queue;
  }

  public static OperationQueue getQueue(Desktop desktop)
  {
    if (desktop == null)
      throw new NullPointerException("desktop is null");
    synchronized (desktop) {
      if (!(desktop.isAlive()))
        throw new IllegalStateException("desktop not alive:" + desktop);

      OperationThread t = (OperationThread)desktop.getAttribute("zkex:opthread");
      if (t == null) {
        t = new OperationThread(desktop);
        if (log.debugable())
          log.debug("staring a Operation Thread for desktop:" + desktop + ",name=" + t.getName());

        desktop.setAttribute("zkex:opthread", t);
        t.start();
      }

      return t.getQueue();
    }
  }

  public static void destroyWith(Desktop desktop)
  {
    if (desktop == null)
      throw new NullPointerException("desktop is null");

    if (log.debugable())
      log.debug("destory a Operation Thread for desktop:" + desktop);

    synchronized (desktop) {
      if (desktop.isAlive()) {
        OperationThread t = (OperationThread)desktop.getAttribute("zkex:opthread");

        desktop.removeAttribute("zkex:opthread");
        if ((t != null) && (t.isRunning()))
          t.terminate();
      }
    }
  }

  public boolean isRunning()
  {
    return this._running;
  }

  public void terminate()
  {
    this._running = false;
    synchronized (this._queue) {
      this._queue.notifyAll();
    }
  }

  // ERROR //
  public void run()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 7	org/zkoss/zkex/zul/impl/OperationThread:_running	Z
    //   6: ifeq +262 -> 268
    //   9: aload_0
    //   10: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   13: invokeinterface 27 1 0
    //   18: ifne +33 -> 51
    //   21: new 50	org/zkoss/zk/ui/DesktopUnavailableException
    //   24: dup
    //   25: new 17	java/lang/StringBuffer
    //   28: dup
    //   29: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   32: ldc 51
    //   34: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   37: aload_0
    //   38: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   41: invokevirtual 30	java/lang/StringBuffer:append	(Ljava/lang/Object;)Ljava/lang/StringBuffer;
    //   44: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   47: invokespecial 52	org/zkoss/zk/ui/DesktopUnavailableException:<init>	(Ljava/lang/String;)V
    //   50: athrow
    //   51: aload_0
    //   52: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   55: invokevirtual 53	org/zkoss/zkex/zul/impl/OperationQueue:hasElement	()Z
    //   58: ifeq +156 -> 214
    //   61: iconst_0
    //   62: istore_2
    //   63: aload_0
    //   64: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   67: aload_0
    //   68: getfield 10	org/zkoss/zkex/zul/impl/OperationThread:_activateTimeout	J
    //   71: invokestatic 54	org/zkoss/zk/ui/Executions:activate	(Lorg/zkoss/zk/ui/Desktop;J)Z
    //   74: istore_2
    //   75: goto +4 -> 79
    //   78: astore_3
    //   79: iload_2
    //   80: ifne +58 -> 138
    //   83: iinc 1 1
    //   86: iload_1
    //   87: aload_0
    //   88: getfield 12	org/zkoss/zkex/zul/impl/OperationThread:_maxFailCount	I
    //   91: if_icmplt -89 -> 2
    //   94: new 50	org/zkoss/zk/ui/DesktopUnavailableException
    //   97: dup
    //   98: new 17	java/lang/StringBuffer
    //   101: dup
    //   102: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   105: ldc 56
    //   107: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   110: aload_0
    //   111: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   114: invokevirtual 30	java/lang/StringBuffer:append	(Ljava/lang/Object;)Ljava/lang/StringBuffer;
    //   117: ldc 57
    //   119: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   122: iload_1
    //   123: invokevirtual 58	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
    //   126: ldc 59
    //   128: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   131: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   134: invokespecial 52	org/zkoss/zk/ui/DesktopUnavailableException:<init>	(Ljava/lang/String;)V
    //   137: athrow
    //   138: iconst_0
    //   139: istore_1
    //   140: aconst_null
    //   141: astore_3
    //   142: aload_0
    //   143: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   146: invokevirtual 53	org/zkoss/zkex/zul/impl/OperationQueue:hasElement	()Z
    //   149: ifeq +24 -> 173
    //   152: aload_0
    //   153: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   156: invokevirtual 60	org/zkoss/zkex/zul/impl/OperationQueue:next	()Lorg/zkoss/zkex/zul/impl/Operation;
    //   159: astore_3
    //   160: aload_3
    //   161: aload_0
    //   162: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   165: invokeinterface 61 2 0
    //   170: goto -28 -> 142
    //   173: aload_0
    //   174: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   177: invokestatic 62	org/zkoss/zk/ui/Executions:deactivate	(Lorg/zkoss/zk/ui/Desktop;)V
    //   180: goto +34 -> 214
    //   183: astore 4
    //   185: aload_3
    //   186: ifnull +13 -> 199
    //   189: aload_3
    //   190: aload_0
    //   191: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   194: invokeinterface 64 2 0
    //   199: aload 4
    //   201: athrow
    //   202: astore 5
    //   204: aload_0
    //   205: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   208: invokestatic 62	org/zkoss/zk/ui/Executions:deactivate	(Lorg/zkoss/zk/ui/Desktop;)V
    //   211: aload 5
    //   213: athrow
    //   214: aload_0
    //   215: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   218: dup
    //   219: astore_2
    //   220: monitorenter
    //   221: aload_0
    //   222: getfield 7	org/zkoss/zkex/zul/impl/OperationThread:_running	Z
    //   225: ifeq +24 -> 249
    //   228: aload_0
    //   229: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   232: invokevirtual 53	org/zkoss/zkex/zul/impl/OperationQueue:hasElement	()Z
    //   235: ifne +14 -> 249
    //   238: aload_0
    //   239: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   242: aload_0
    //   243: getfield 11	org/zkoss/zkex/zul/impl/OperationThread:_waitTimeout	J
    //   246: invokevirtual 65	java/lang/Object:wait	(J)V
    //   249: aload_2
    //   250: monitorexit
    //   251: goto +10 -> 261
    //   254: astore 6
    //   256: aload_2
    //   257: monitorexit
    //   258: aload 6
    //   260: athrow
    //   261: goto -259 -> 2
    //   264: astore_2
    //   265: goto -263 -> 2
    //   268: aload_0
    //   269: iconst_0
    //   270: putfield 7	org/zkoss/zkex/zul/impl/OperationThread:_running	Z
    //   273: aload_0
    //   274: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   277: invokestatic 66	org/zkoss/zkex/zul/impl/OperationThread:destroyWith	(Lorg/zkoss/zk/ui/Desktop;)V
    //   280: aload_0
    //   281: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   284: aload_0
    //   285: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   288: invokevirtual 67	org/zkoss/zkex/zul/impl/OperationQueue:fireQueueUnavailable	(Lorg/zkoss/zk/ui/Desktop;)V
    //   291: aload_0
    //   292: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   295: invokevirtual 68	org/zkoss/zkex/zul/impl/OperationQueue:clearListener	()V
    //   298: aload_0
    //   299: aconst_null
    //   300: putfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   303: aload_0
    //   304: aconst_null
    //   305: putfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   308: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   311: invokevirtual 37	org/zkoss/util/logging/Log:debugable	()Z
    //   314: ifeq +388 -> 702
    //   317: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   320: new 17	java/lang/StringBuffer
    //   323: dup
    //   324: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   327: ldc 69
    //   329: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   332: aload_0
    //   333: invokevirtual 40	org/zkoss/zkex/zul/impl/OperationThread:getName	()Ljava/lang/String;
    //   336: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   339: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   342: invokevirtual 41	org/zkoss/util/logging/Log:debug	(Ljava/lang/String;)V
    //   345: goto +357 -> 702
    //   348: astore_1
    //   349: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   352: invokevirtual 37	org/zkoss/util/logging/Log:debugable	()Z
    //   355: ifeq +31 -> 386
    //   358: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   361: new 17	java/lang/StringBuffer
    //   364: dup
    //   365: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   368: ldc 70
    //   370: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   373: aload_1
    //   374: invokevirtual 71	org/zkoss/zk/ui/DesktopUnavailableException:getMessage	()Ljava/lang/String;
    //   377: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   380: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   383: invokevirtual 41	org/zkoss/util/logging/Log:debug	(Ljava/lang/String;)V
    //   386: aload_0
    //   387: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   390: invokevirtual 53	org/zkoss/zkex/zul/impl/OperationQueue:hasElement	()Z
    //   393: ifeq +24 -> 417
    //   396: aload_0
    //   397: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   400: invokevirtual 60	org/zkoss/zkex/zul/impl/OperationQueue:next	()Lorg/zkoss/zkex/zul/impl/Operation;
    //   403: astore_2
    //   404: aload_2
    //   405: aload_0
    //   406: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   409: invokeinterface 64 2 0
    //   414: goto -28 -> 386
    //   417: aload_0
    //   418: iconst_0
    //   419: putfield 7	org/zkoss/zkex/zul/impl/OperationThread:_running	Z
    //   422: aload_0
    //   423: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   426: invokestatic 66	org/zkoss/zkex/zul/impl/OperationThread:destroyWith	(Lorg/zkoss/zk/ui/Desktop;)V
    //   429: aload_0
    //   430: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   433: aload_0
    //   434: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   437: invokevirtual 67	org/zkoss/zkex/zul/impl/OperationQueue:fireQueueUnavailable	(Lorg/zkoss/zk/ui/Desktop;)V
    //   440: aload_0
    //   441: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   444: invokevirtual 68	org/zkoss/zkex/zul/impl/OperationQueue:clearListener	()V
    //   447: aload_0
    //   448: aconst_null
    //   449: putfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   452: aload_0
    //   453: aconst_null
    //   454: putfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   457: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   460: invokevirtual 37	org/zkoss/util/logging/Log:debugable	()Z
    //   463: ifeq +239 -> 702
    //   466: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   469: new 17	java/lang/StringBuffer
    //   472: dup
    //   473: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   476: ldc 69
    //   478: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   481: aload_0
    //   482: invokevirtual 40	org/zkoss/zkex/zul/impl/OperationThread:getName	()Ljava/lang/String;
    //   485: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   488: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   491: invokevirtual 41	org/zkoss/util/logging/Log:debug	(Ljava/lang/String;)V
    //   494: goto +208 -> 702
    //   497: astore_1
    //   498: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   501: aload_1
    //   502: invokevirtual 72	org/zkoss/util/logging/Log:warning	(Ljava/lang/Throwable;)V
    //   505: aload_0
    //   506: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   509: invokevirtual 53	org/zkoss/zkex/zul/impl/OperationQueue:hasElement	()Z
    //   512: ifeq +28 -> 540
    //   515: aload_0
    //   516: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   519: invokevirtual 60	org/zkoss/zkex/zul/impl/OperationQueue:next	()Lorg/zkoss/zkex/zul/impl/Operation;
    //   522: astore_2
    //   523: aload_2
    //   524: ifnull +13 -> 537
    //   527: aload_2
    //   528: aload_0
    //   529: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   532: invokeinterface 64 2 0
    //   537: goto -32 -> 505
    //   540: aload_0
    //   541: iconst_0
    //   542: putfield 7	org/zkoss/zkex/zul/impl/OperationThread:_running	Z
    //   545: aload_0
    //   546: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   549: invokestatic 66	org/zkoss/zkex/zul/impl/OperationThread:destroyWith	(Lorg/zkoss/zk/ui/Desktop;)V
    //   552: aload_0
    //   553: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   556: aload_0
    //   557: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   560: invokevirtual 67	org/zkoss/zkex/zul/impl/OperationQueue:fireQueueUnavailable	(Lorg/zkoss/zk/ui/Desktop;)V
    //   563: aload_0
    //   564: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   567: invokevirtual 68	org/zkoss/zkex/zul/impl/OperationQueue:clearListener	()V
    //   570: aload_0
    //   571: aconst_null
    //   572: putfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   575: aload_0
    //   576: aconst_null
    //   577: putfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   580: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   583: invokevirtual 37	org/zkoss/util/logging/Log:debugable	()Z
    //   586: ifeq +116 -> 702
    //   589: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   592: new 17	java/lang/StringBuffer
    //   595: dup
    //   596: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   599: ldc 69
    //   601: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   604: aload_0
    //   605: invokevirtual 40	org/zkoss/zkex/zul/impl/OperationThread:getName	()Ljava/lang/String;
    //   608: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   611: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   614: invokevirtual 41	org/zkoss/util/logging/Log:debug	(Ljava/lang/String;)V
    //   617: goto +85 -> 702
    //   620: astore 7
    //   622: aload_0
    //   623: iconst_0
    //   624: putfield 7	org/zkoss/zkex/zul/impl/OperationThread:_running	Z
    //   627: aload_0
    //   628: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   631: invokestatic 66	org/zkoss/zkex/zul/impl/OperationThread:destroyWith	(Lorg/zkoss/zk/ui/Desktop;)V
    //   634: aload_0
    //   635: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   638: aload_0
    //   639: getfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   642: invokevirtual 67	org/zkoss/zkex/zul/impl/OperationQueue:fireQueueUnavailable	(Lorg/zkoss/zk/ui/Desktop;)V
    //   645: aload_0
    //   646: getfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   649: invokevirtual 68	org/zkoss/zkex/zul/impl/OperationQueue:clearListener	()V
    //   652: aload_0
    //   653: aconst_null
    //   654: putfield 13	org/zkoss/zkex/zul/impl/OperationThread:_desktop	Lorg/zkoss/zk/ui/Desktop;
    //   657: aload_0
    //   658: aconst_null
    //   659: putfield 16	org/zkoss/zkex/zul/impl/OperationThread:_queue	Lorg/zkoss/zkex/zul/impl/OperationQueue;
    //   662: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   665: invokevirtual 37	org/zkoss/util/logging/Log:debugable	()Z
    //   668: ifeq +31 -> 699
    //   671: getstatic 36	org/zkoss/zkex/zul/impl/OperationThread:log	Lorg/zkoss/util/logging/Log;
    //   674: new 17	java/lang/StringBuffer
    //   677: dup
    //   678: invokespecial 18	java/lang/StringBuffer:<init>	()V
    //   681: ldc 69
    //   683: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   686: aload_0
    //   687: invokevirtual 40	org/zkoss/zkex/zul/impl/OperationThread:getName	()Ljava/lang/String;
    //   690: invokevirtual 20	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   693: invokevirtual 22	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   696: invokevirtual 41	org/zkoss/util/logging/Log:debug	(Ljava/lang/String;)V
    //   699: aload 7
    //   701: athrow
    //   702: return
    //
    // Exception table:
    //   from	to	target	type
    //   63	75	78	java/lang/InterruptedException
    //   142	173	183	java/lang/Exception
    //   142	173	202	finally
    //   183	204	202	finally
    //   221	251	254	finally
    //   254	258	254	finally
    //   214	261	264	java/lang/InterruptedException
    //   0	268	348	org/zkoss/zk/ui/DesktopUnavailableException
    //   0	268	497	java/lang/Exception
    //   0	268	620	finally
    //   348	417	620	finally
    //   497	540	620	finally
    //   620	622	620	finally
  }
}