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
