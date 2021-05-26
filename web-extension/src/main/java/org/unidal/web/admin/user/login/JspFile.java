package org.unidal.web.admin.user.login;

public enum JspFile {
	VIEW("/jsp/user/login.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}
