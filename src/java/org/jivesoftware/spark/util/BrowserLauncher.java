package org.jivesoftware.spark.util;

import java.awt.Desktop;
import java.net.URI;

public class BrowserLauncher {

	public static void openURL(String url) throws Exception {
		if (url.startsWith("http") || 
			url.startsWith("ftp")) {
			Desktop.getDesktop().browse(new URI(url));
		}
		else {
			Desktop.getDesktop().browse(new URI("http://" + url));
		}
	}
}
