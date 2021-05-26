package org.unidal.web.admin.config.refresh;

import org.unidal.web.admin.config.ConfigPage;
import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<ConfigPage, Action, Context> {
	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}
}
