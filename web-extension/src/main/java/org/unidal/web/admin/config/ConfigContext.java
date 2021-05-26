package org.unidal.web.admin.config;

import org.unidal.web.mvc.Action;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.Page;

public class ConfigContext<T extends ActionPayload<? extends Page, ? extends Action>> extends ActionContext<T> {

}
