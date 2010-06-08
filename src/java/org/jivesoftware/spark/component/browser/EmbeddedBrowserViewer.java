/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
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
package org.jivesoftware.spark.component.browser;

import java.awt.BorderLayout;
import java.net.MalformedURLException;

import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;

public class EmbeddedBrowserViewer extends BrowserViewer {

	private static final long serialVersionUID = 465853124210602603L;
	private SimpleHtmlRendererContext context;
	private HtmlPanel panel;
	
	public EmbeddedBrowserViewer() {
		panel = new HtmlPanel();
		context = new SimpleHtmlRendererContext(panel, new SimpleUserAgentContext());
	}
	
	@Override
	public void goBack() {
		context.back();
	}

	@Override
	public void initializeBrowser() {
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
	}

	@Override
	public void loadURL(String url) {
		try {
			context.navigate(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
