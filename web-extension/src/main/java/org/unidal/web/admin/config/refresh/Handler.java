package org.unidal.web.admin.config.refresh;

import java.io.IOException;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;
import org.unidal.web.admin.config.ConfigPage;
import org.unidal.web.config.ConfigService;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

@Named
public class Handler implements PageHandler<Context>, LogEnabled {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ConfigService m_configService;

	private Logger m_logger;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "refresh")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		int count = m_configService.refreshCache();
		String message = String.format("%s cache entries refreshed.", count);

		ctx.sendContent("text/plain", message);
		m_logger.info(message);
		ctx.stopProcess();
	}

	@Override
	@OutboundActionMeta(name = "refresh")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);

		model.setAction(Action.VIEW);
		model.setPage(ConfigPage.REFRESH);

		if (!ctx.isProcessStopped()) {
			m_jspViewer.view(ctx, model);
		}
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}
}
