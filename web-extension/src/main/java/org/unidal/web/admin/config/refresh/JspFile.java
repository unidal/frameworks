package org.unidal.web.admin.config.refresh;

public enum JspFile {
	VIEW("/jsp/config/refresh.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}
