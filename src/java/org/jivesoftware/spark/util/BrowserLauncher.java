/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.util;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public class BrowserLauncher {

	public static void openURL(String url) throws Exception {
		if (url.startsWith("http") || url.startsWith("ftp") || url.startsWith("file")) {

			if (url.startsWith("file") && url.contains(" ")) {
				url = url.replace(" ", "%20");
			}
			Desktop.getDesktop().browse(new URI(url));
		} else {
			File f = new File(url);
			if (f.exists() && Desktop.isDesktopSupported()){
				try {
					Desktop.getDesktop().open(f);
				} catch (Exception ex){
					if (!url.toLowerCase().startsWith("//")) url = "//" + url;
					Desktop.getDesktop().browse(new URI("http:" + url));
				}
			}
		}
	}
}
