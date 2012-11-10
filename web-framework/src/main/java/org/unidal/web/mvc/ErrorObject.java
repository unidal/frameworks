package org.unidal.web.mvc;

import java.util.Arrays;

public class ErrorObject {
	private String m_id;

	private Object[] m_arguments;

	private Exception m_exception;

	public ErrorObject(String id) {
		m_id = id;
	}

	public ErrorObject(String id, Exception exception) {
		m_id = id;
		m_exception = exception;
	}

	public Object[] getArguments() {
		return m_arguments;
	}

	public Exception getException() {
		return m_exception;
	}

	public String getId() {
		return m_id;
	}

	public ErrorObject setArguments(Object... arguments) {
		m_arguments = arguments;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);

		sb.append("ErrorObject[id=").append(m_id);

		if (m_arguments != null) {
			sb.append(",arguments=").append(Arrays.asList(m_arguments));
		}

		if (m_exception != null) {
			sb.append(",exception=").append(m_exception.toString());
		}

		sb.append("]");

		return sb.toString();
	}
}
