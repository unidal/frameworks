package org.unidal.test.jetty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.plexus.PlexusContainer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import org.unidal.lookup.ContainerLoader;
import org.unidal.test.browser.BrowserManager;

public abstract class JettyServer {
	private Server m_server;

	protected void stopServer() throws Exception {
		m_server.stop();
	}

	protected void configure(WebAppContext context) {
		File warRoot = getWarRoot();

		context.setContextPath(getContextPath());
		context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		context.setResourceBase(warRoot.getPath());
	}

	protected void display(String requestUri) throws Exception {
		StringBuilder sb = new StringBuilder(256);
		PlexusContainer container = ContainerLoader.getDefaultContainer();
		BrowserManager manager = container.lookup(BrowserManager.class);

		sb.append("http://localhost:").append(getServerPort()).append(requestUri);

		try {
			manager.display(new URL(sb.toString()));
		} finally {
			container.release(manager);
		}
	}

	protected abstract String getContextPath();

	protected abstract int getServerPort();

	protected File getWarRoot() {
		return new File("src/main/webapp");
	}

	protected void postConfigure(WebAppContext context) {
		// to be override
	}

	protected void startServer() throws Exception {
		Server server = new Server(getServerPort());
		WebAppContext context = new WebAppContext();

		configure(context);
		server.setHandler(context);
		server.start();
		postConfigure(context);

		m_server = server;
	}

	protected void waitForAnyKey() throws IOException {
		String timestamp = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());

		System.out.println(String.format("[%s] [INFO] Press any key to stop server ... ", timestamp));
		System.in.read();
	}
}
