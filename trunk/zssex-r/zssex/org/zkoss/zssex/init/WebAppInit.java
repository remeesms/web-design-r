package org.zkoss.zssex.init;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.WebApp;


public class WebAppInit implements org.zkoss.zk.ui.util.WebAppInit {
	public void init(WebApp paramWebApp) throws Exception {
		if (!(Init.doInit(paramWebApp, true))) {
			String str = "init";
			Log localLog = Log.lookup("global");
			if (localLog.errorable())
				localLog.error(str);
			else
				System.err.println(str);
		}
	}
}