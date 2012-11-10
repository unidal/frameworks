package org.unidal.web.mvc;

public interface Normalizer<T extends ActionContext<?>> {
	public void normalize(T context);
}
