package org.unidal.test.browser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.unidal.helper.Files;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

public abstract class AbstractBrowser implements Browser, LogEnabled {
	private Logger m_logger;

	public void display(String html) {
		display(html, "utf-8");
	}

	public void display(String html, String charset) {
		URL url = saveToTemporaryFile(html, false, charset);

		display(url);
	}

	public abstract String[] getCommandLine(String url);

	public void display(URL url) {
		if (!isAvailable()) {
			throw new RuntimeException(getId() + " is unavailable.");
		}

		try {
			String[] commandLine = getCommandLine(url.toExternalForm());
			Process process = new ProcessBuilder(commandLine).start();
			InputStream in = process.getInputStream();
			String output = Files.forIO().readUtf8String(in);

			if (output != null && output.length() > 0) {
				m_logger.info(output);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error when display page(" + url.toExternalForm() + ")", e);
		}
	}

	private URL saveToTemporaryFile(String html, boolean deleteOnExit, String charset) {
		try {
			File tempFile = File.createTempFile("test", ".html");

			if (deleteOnExit) {
				tempFile.deleteOnExit();
			}

			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(tempFile), charset);

			out.write(html);
			out.close();

			return tempFile.getCanonicalFile().toURI().toURL();
		} catch (Exception e) {
			throw new RuntimeException("Error when writing to temporary file: " + e.getMessage(), e);
		}
	}

	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	protected Logger getLogger() {
		return m_logger;
	}
}
