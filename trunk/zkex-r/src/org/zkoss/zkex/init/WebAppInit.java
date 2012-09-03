package org.zkoss.zkex.init;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zkex.util.ObfuscatedString;

public class WebAppInit implements org.zkoss.zk.ui.util.WebAppInit {
	public void init(WebApp paramWebApp) throws Exception {
		if (!(Init.doInit(paramWebApp, true))) {
			String str = (WebApps.getFeature("ee")) ? "EE" : "PE";
			str = "init: " + str;
			Log localLog = Log.lookup("global");
			if (localLog.errorable())
				localLog.error(str);
			else
				System.err.println(str);
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println();
	}
}