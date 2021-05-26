package org.unidal.web.authorization;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.unidal.cat.CatFilter;
import org.unidal.web.admin.user.login.Context;
import org.unidal.web.admin.user.login.Payload;
import org.unidal.web.jsp.function.CodecFunction;
import org.unidal.web.mvc.ActionContext;

public class DefaultAccessContext implements AccessContext {
	private ActionContext<?> m_ctx;

	private String m_appId;

	private String m_clientIp;

	public DefaultAccessContext(ActionContext<?> ctx) {
		HttpServletRequest req = ctx.getHttpServletRequest();
		String appId = req.getParameter("__APP_ID");
		String clientIp = ctx.getClientIP();

		if (appId == null) {
			appId = req.getHeader("x-app-id");
		}

		if (appId == null) {
			appId = "Unknown";
		}

		m_ctx = ctx;
		m_appId = appId;
		m_clientIp = clientIp;
	}

	@Override
	public void appendToCatPageURI(String scenario) {
		HttpServletRequest req = m_ctx.getHttpServletRequest();
		String catPageUri = (String) req.getAttribute(CatFilter.CAT_PAGE_URI);

		req.setAttribute(CatFilter.CAT_PAGE_URI, catPageUri + '/' + scenario);
	}

	@Override
	public void error(int status, String format, Object... args) throws IOException {
		m_ctx.sendError(status, String.format(format, args));
	}

	@Override
	public String getAppId() {
		return m_appId;
	}

	@Override
	public String getClientIp() {
		return m_clientIp;
	}

	public Payload getPayload() {
		if (m_ctx instanceof Context) {
			Payload payload = (Payload) m_ctx.getPayload();

			return payload;
		} else {
			return null;
		}
	}

	@Override
	public String getUserToken() {
		Cookie cookie = m_ctx.getCookie(COOKIE_TOKEN);

		if (cookie != null) {
			return cookie.getValue();
		}

		return null;
	}

	@Override
	public void gotoLogin() {
		String url = m_ctx.getRequestContext().getModuleUri("user", "login");
		String rtnUrl = m_ctx.getHttpServletRequest().getRequestURI();

		m_ctx.redirect(url + "?rtnUrl=" + CodecFunction.urlEncode(rtnUrl));
	}
}
