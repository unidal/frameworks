package org.unidal.web.config;

import org.unidal.web.admin.dal.config.ConfigDo;

public class Config {
	private String m_category;

	private String m_name;

	private String m_description;

	private int m_status;

	private byte[] m_details;

	public Config(String category, String name, String description, int status, byte[] details) {
		m_category = category;
		m_name = name;
		m_description = description;
		m_status = status;
		m_details = details;
	}

	public Config(String category, String name) {
		m_category = category;
		m_name = name;
	}

	public Config(ConfigDo config) {
		m_category = config.getCategory();
		m_name = config.getName();
		m_description = config.getDescription();
		m_status = config.getStatus();
		m_details = config.getDetails();
	}

	public String getCategory() {
		return m_category;
	}

	public String getDescription() {
		return m_description;
	}

	public byte[] getDetails() {
		return m_details;
	}

	public String getName() {
		return m_name;
	}

	public int getStatus() {
		return m_status;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1024);

		sb.append("Config[");
		sb.append("category: ").append(m_category);
		sb.append(", name: ").append(m_name);
		sb.append(", status: ").append(m_status);
		sb.append(", description: ").append(m_description);
		sb.append(", details: ").append(m_details == null ? null : new String(m_details));
		sb.append("]");
		return sb.toString();
	}
}
