package org.unidal.web.mvc.view.model;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ModelHandler {
   public void handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException;
}
