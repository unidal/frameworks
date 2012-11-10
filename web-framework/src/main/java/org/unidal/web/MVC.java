package org.unidal.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dianping.cat.Cat;
import org.unidal.web.lifecycle.RequestLifecycle;

public class MVC extends AbstractContainerServlet {
	private static final long serialVersionUID = 1L;

	private RequestLifecycle m_handler;

	@Override
	protected void initComponents(ServletConfig config) throws Exception {
		String catClientXml = config.getInitParameter("cat-client-xml");

		getLogger().info("MVC is starting at " + config.getServletContext().getContextPath());

		Cat.initialize(getContainer(), catClientXml == null ? null : new File(catClientXml));

		m_handler = lookup(RequestLifecycle.class, "mvc");
		m_handler.setServletContext(config.getServletContext());

		getLogger().info("MVC started at " + config.getServletContext().getContextPath());
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	      IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		try {
			m_handler.handle(request, response);
		} catch (Throwable t) {
			String message = "Error occured when handling uri: " + request.getRequestURI();

			getLogger().error(message, t);

			if (!response.isCommitted()) {
				response.sendError(500, message);
			}
		}
	}
}
