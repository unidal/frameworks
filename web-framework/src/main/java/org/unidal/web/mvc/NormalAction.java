package org.unidal.web.mvc;

public enum NormalAction implements Action {
	NONE(""),

	LIST("list"),

	VIEW("view"),

	ADD("add"),

	EDIT("edit"),

	REMOVE("remove");

	private String m_name;

	private NormalAction(String name) {
		m_name = name;
	}

	public static NormalAction getByName(String name, NormalAction defaultAction) {
		for (NormalAction action : NormalAction.values()) {
			if (action.getName().equals(name)) {
				return action;
			}
		}

		return defaultAction;
	}

	public String getName() {
		return m_name;
	}
}
