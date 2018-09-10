package org.unidal.web.mvc;

import org.unidal.web.mvc.lifecycle.RequestContext;

public abstract class ViewModel<P extends Page, A extends Action, M extends ActionContext<?>> {
	private transient M m_actionContext;

	private transient P m_page;

	private transient A m_action;

	public ViewModel(M actionContext) {
		m_actionContext = actionContext;
	}

	protected String buildPageUri(String action, String pathInfo) {
		RequestContext requestContext = m_actionContext.getRequestContext();

		return requestContext.getActionUri(action, pathInfo);
	}

	public A getAction() {
		return m_action;
	}

	public M getActionContext() {
		return m_actionContext;
	}

	public abstract A getDefaultAction();

	public String getModule() {
	   RequestContext requestContext = m_actionContext.getRequestContext();
	   
		return requestContext.getModule().getModuleName();
	}
	
	public String getModuleUri() {
	   return buildPageUri(null, null);
	}

	public P getPage() {
		return m_page;
	}

	public String getPageUri() {
		return buildPageUri(m_page.getPath(), null);
	}

	public String getWebapp() {
		return m_actionContext.getHttpServletRequest().getContextPath();
	}

	public void setAction(A action) {
		m_action = action;
	}

	public void setPage(P action) {
		m_page = action;
	}
}
