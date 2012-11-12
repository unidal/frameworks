package org.unidal.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.test.book.BookModule;

import org.unidal.test.junit.HttpTestCase;
import org.unidal.test.server.EmbeddedServer;

@RunWith(JUnit4.class)
public class MvcTest extends HttpTestCase {
	@Override
	protected void configure(EmbeddedServer server) {
		server.addServlet(new MVC().setContainer(getContainer()), "mvc-servlet", "/*");
	}

	@Override
	protected int getPort() {
		return super.getPort() + 1;
	}

	@Test
	public void testElse() throws Exception {
		checkRequest("/book/else", "==>doElse==>no payload==>transition==>showList[]");

		checkRequest("/book/else?id=1", "==>doElse==>no payload==>transition==>error:No method annotated by @"
		      + OutboundActionMeta.class.getSimpleName() + "(unknown) found in " + BookModule.class);
	}

	@Test
	public void testList() throws Exception {
		checkRequest("/book/list", "==>doList==>transition==>showList[]");
		checkRequest("/book/list/1", "==>doList==>transition==>showList[]");

		checkRequest("/book/list?id=1", "==>doList==>error:Error occured during handling transition(default)");
	}

	@Test
	public void testAdd() throws Exception {
		checkRequest("/book/add?id=1&name=first", "==>signin==>permission==>doAdd==>transition==>showList[1(first)]");
		checkRequest("/book/add?id=2&name=second",
		      "==>signin==>permission==>doAdd==>transition==>showList[1(first), 2(second)]");

		checkRequest("/book/add?id=0&name=zero", "==>signin==>permission==>doAdd==>transition==>showAdd");
		checkRequest("/book/add?id=3&name=",
		      "==>signin==>permission==>error:Error occured during handling inbound action(add)!");
	}
}
