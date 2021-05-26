package org.unidal.web.admin.config.home;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.unidal.cat.Cat;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.admin.config.ConfigPage;
import org.unidal.web.config.Config;
import org.unidal.web.config.ConfigService;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;
import org.unidal.web.mvc.lifecycle.RequestContext;

@Named
public class Handler implements PageHandler<Context> {
	@Inject
	private ConfigService m_configService;

	@Inject
	private JspViewer m_jspViewer;

	private void doUpdate(Context ctx, Payload payload) {
		String category = payload.getCategory();
		String name = payload.getName();
		String description = payload.getDescription();
		String content = payload.getContent();

		try {
			m_configService.updateConfig(category, name, description, content);

			RequestContext rc = ctx.getRequestContext();
			String pageUri = rc.getActionUri(String.format("%s/%s/%s", ConfigPage.HOME.getPath(), category, name));

			ctx.redirect(pageUri);
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		if (!ctx.hasErrors()) {
			Payload payload = ctx.getPayload();
			Action action = payload.getAction();

			if (action.isEdit() && payload.isUpdate()) {
				doUpdate(ctx, payload);
			} else if (action.isAdd() && payload.isUpdate()) {
				doUpdate(ctx, payload);
			}
		}
	}

	@Override
	@OutboundActionMeta(name = "home")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		model.setAction(action);
		model.setPage(ConfigPage.HOME);

		switch (action) {
		case LIST:
		case ADD:
		case EDIT:
			showConfigs(ctx, payload, model);
			break;
		default:
			break;
		}

		if (!ctx.isProcessStopped()) {
			m_jspViewer.view(ctx, model);
		}
	}

	private void showConfigs(Context ctx, Payload payload, Model model) {
		List<String> categories = m_configService.findCategories();
		String category = payload.getCategory();

		if (category == null && !categories.isEmpty()) {
			category = categories.get(0);
		}

		model.setCategories(categories);
		model.setCategory(category);

		try {
			String name = payload.getName();
			List<Config> configs = m_configService.findConfigs(category);
			Config config = name == null ? null : m_configService.findConfig(category, name);

			if (config == null && !configs.isEmpty()) {
				config = configs.get(0);
				category = config.getName();
			}

			model.setConfigs(configs);
			model.setConfig(config);
		} catch (Exception e) {
			Cat.logError(e);
		}
	}
}
