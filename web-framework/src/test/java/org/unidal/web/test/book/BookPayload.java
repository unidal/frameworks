package org.unidal.web.test.book;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.NormalAction;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class BookPayload implements ActionPayload<BookPage, NormalAction> {
	private NormalAction m_action;

	private BookPage m_page;

	@FieldMeta("id")
	private int m_id;

	@FieldMeta("name")
	private String m_name;

	public BookPage getPage() {
		return m_page;
	}

	public int getId() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public void setPage(String action) {
		m_page = BookPage.getByName(action, BookPage.LIST);
	}

	public void setId(int id) {
		m_id = id;
	}

	public void setName(String name) {
		m_name = name;
	}

	public NormalAction getAction() {
		return m_action;
	}

	public void setAction(String name) {
		m_action = NormalAction.getByName(name, NormalAction.NONE);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
	}
}
