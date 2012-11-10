package org.unidal.web.mvc;

public interface ActionPayload<S extends Page, T extends Action> {
	public T getAction();

	public S getPage();

	public void setPage(String page);

	public void validate(ActionContext<?> ctx);
}
