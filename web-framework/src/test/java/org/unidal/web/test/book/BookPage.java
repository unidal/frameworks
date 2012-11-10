package org.unidal.web.test.book;

import org.unidal.web.mvc.Page;

public enum BookPage implements Page {
	ADD("add"),

	LIST("list");

	private String m_name;

	private BookPage(String name) {
		m_name = name;
	}

	public static BookPage getByName(String name, BookPage defaultAction) {
		for (BookPage action : BookPage.values()) {
			if (action.getName().equals(name)) {
				return action;
			}
		}

		return defaultAction;
	}
	
	public String getName() {
	   return m_name;
	}

	public String getPath() {
		return m_name;
	}
}
