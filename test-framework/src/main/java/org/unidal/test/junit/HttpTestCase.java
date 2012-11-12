package org.unidal.test.junit;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.jasper.servlet.JspServlet;
import org.junit.Assert;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.log.Log;
import org.mortbay.log.StdErrLog;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.browser.Browser;
import org.unidal.test.browser.BrowserManager;
import org.unidal.test.server.EmbeddedServer;
import org.unidal.test.server.EmbeddedServerManager;

public abstract class HttpTestCase extends ComponentTestCase {
	protected static void main(HttpTestCase test) throws Exception {
		test.setUp();
		test.service();
	}

	private EmbeddedServer m_server;

	protected EmbeddedServer getServer() {
		return m_server;
	}

	protected void checkRequest(String uri, String expected) throws Exception {
		Browser browser = lookup(Browser.class, "memory");

		browser.display(new URL(m_server.getBaseUrl() + uri));
		Assert.assertEquals(expected, browser.toString());
	}

	protected void display(String requestUri) throws Exception {
		StringBuilder sb = new StringBuilder(256);

		sb.append("http://localhost:");
		sb.append(m_server.getPort());
		sb.append(requestUri);

		BrowserManager manager = lookup(BrowserManager.class);

		try {
			manager.display(new URL(sb.toString()));
		} finally {
			release(manager);
		}
	}

	protected void configure(EmbeddedServer server) {
		// to be overridden
	}

	protected String getContextPath() {
		return "/";
	}

	protected int getPort() {
		return 2000;
	}

	protected String getWebappDirectory() {
		return "src/main/webapp";
	}

	protected boolean logEnabled() {
		return false;
	}

	// blocking mode
	protected void service() throws Exception {
		// to be overridden
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		if (!logEnabled()) {
			StdErrLog logger = new StdErrLog();

			logger.setDebugEnabled(false);
			Log.setLog(logger);
		}

		m_server = EmbeddedServerManager.create(getPort(), getContextPath(), getWebappDirectory());

		// JSP servlet for *.jsp
		m_server.addServlet(new JspServlet(), "jsp-servlet", "*.jsp");

		// default servlet for static files, but not directory
		Map<String, String> defaultInitParameters = new HashMap<String, String>();
		defaultInitParameters.put("dirAllowed", "false");
		m_server.addServlet(new DefaultServlet(), "default-servlet", "/", defaultInitParameters);

		configure(m_server);
		m_server.start();
	}

	@Override
	public void tearDown() throws Exception {
		if (m_server.isStarted()) {
			m_server.stop();
		}

		super.tearDown();
	}
}
