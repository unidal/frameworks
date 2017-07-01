package org.unidal.web.http;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.unidal.helper.Joiners;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
	private Map<String, String[]> m_parameters;

	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request != null ? request : new HttpServletRequestMock());

		m_parameters = getParameters();
	}

	protected void addParameters(Map<String, String> parameters) {
		for (Map.Entry<String, String> e : parameters.entrySet()) {
			m_parameters.put(e.getKey(), new String[] { e.getValue() });
		}
	}

	protected void addParameters(String[] names, String[] values) {
		if (names.length != values.length) {
			throw new RuntimeException("Names and values are not paired.");
		}

		int index = 0;

		for (String name : names) {
			String[] oldValues = m_parameters.get(name);

			m_parameters.put(name, getNewValues(oldValues, values[index++]));
		}
	}

	protected void addParameters(String[] nameValues) {
		int len = nameValues.length;

		if (len % 2 != 0) {
			throw new RuntimeException("Names and values are not paired.");
		}

		for (int i = 0; i < len; i += 2) {
			String name = nameValues[i];
			String value = nameValues[i + 1];
			String[] oldValues = m_parameters.get(name);

			m_parameters.put(name, getNewValues(oldValues, value));
		}
	}

	private String[] getNewValues(String[] oldValues, String value) {
		if (oldValues == null) {
			return new String[] { value };
		} else {
			int oldLen = oldValues.length;
			String[] values = new String[oldLen + 1];

			System.arraycopy(oldValues, 0, values, 0, oldLen);
			values[oldLen] = value;
			return values;
		}
	}

	@Override
	public String getParameter(String name) {
		String[] values = m_parameters.get(name);

		if (values == null) {
			return super.getParameter(name);
		} else if (values.length == 1) {
			return values[0];
		} else {
			return Joiners.by(',').join(values);
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = super.getParameterMap();
		Map<String, String[]> map = parameterMap != null ? new HashMap<String, String[]>(parameterMap)
		      : new HashMap<String, String[]>();

		map.putAll(m_parameters);
		return map;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new Vector<String>(getParameterMap().keySet()).elements();
	}

	protected Map<String, String[]> getParameters() {
		return new HashMap<String, String[]>();
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = m_parameters.get(name);

		if (values == null) {
			return super.getParameterValues(name);
		} else {
			return values;
		}
	}
}
