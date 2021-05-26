package org.unidal.web.admin.config.refresh;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.view.BaseJspViewer;

import org.unidal.web.admin.config.ConfigPage;

@Named
public class JspViewer extends BaseJspViewer<ConfigPage, Action, Context, Model> {
	@Override
	protected String getJspFilePath(Context ctx, Model model) {
		Action action = model.getAction();

		switch (action) {
		case VIEW:
			return JspFile.VIEW.getPath();
		}

		throw new RuntimeException("Unknown action: " + action);
	}
}
