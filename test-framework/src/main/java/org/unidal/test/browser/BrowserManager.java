package org.unidal.test.browser;

import java.net.URL;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;

@Named
public class BrowserManager extends ContainerHolder {
	public void display(String html) {
		display(html, "utf-8", BrowserType.DEFAULT);
	}

	public void display(String html, BrowserType id) {
		display(html, "utf-8", id);
	}

	public void display(String html, String charset) {
		display(html, charset, BrowserType.DEFAULT);
	}

	public void display(String html, String charset, BrowserType id) {
		Browser browser = lookup(Browser.class, id.getId());

		try {
			browser.display(html, charset);
		} finally {
			release(browser);
		}
	}

	public void display(URL url) {
		display(url, BrowserType.DEFAULT);
	}

	public void display(URL url, BrowserType id) {
		Browser browser = lookup(Browser.class, id.getId());

		try {
			browser.display(url);
		} finally {
			release(browser);
		}
	}
}
