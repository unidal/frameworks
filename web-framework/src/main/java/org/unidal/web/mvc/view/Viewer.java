package org.unidal.web.mvc.view;

import java.io.IOException;

import javax.servlet.ServletException;

import org.unidal.web.mvc.Action;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.Page;
import org.unidal.web.mvc.ViewModel;

public interface Viewer<P extends Page, A extends Action, S extends ActionContext<?>, T extends ViewModel<P, A, S>> {
	public void view(S ctx, T model) throws ServletException, IOException;
}
