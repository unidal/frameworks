package org.unidal.web.mvc;

public interface Module {
	public Class<? extends PageHandler<?>>[] getPageHandlers();
}
