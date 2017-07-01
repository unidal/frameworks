package org.unidal.web.lifecycle;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.web.http.HttpServletRequestMock;
import org.unidal.web.mvc.payload.UrlEncodedParameterProvider;

public class ActionResolverTest extends ComponentTestCase {
	private void assertResolve(ActionResolver resolver, String uri) {
		final int pos = uri.indexOf('?');
		final String pathInfo = pos > 0 ? uri.substring(0, pos) : uri;
		final String queryString = pos > 0 ? uri.substring(pos + 1) : null;
		final String contextPath = null;

		HttpServletRequestMock request = new HttpServletRequestMock() {
			@Override
			public String getContextPath() {
				return contextPath;
			}

			@Override
			public String getServletPath() {
				return null;
			}

			@Override
			public String getPathInfo() {
				return pathInfo;
			}

			@Override
			public String getRequestURI() {
				return pathInfo;
			}

			@Override
			public String getQueryString() {
				return queryString;
			}
		};

		UrlMapping mapping = resolver.parseUrl(new UrlEncodedParameterProvider().setRequest(request));
		String actualUri = resolver.buildUrl(new UrlEncodedParameterProvider().setRequest(request), mapping);
		String expectedUri = (contextPath == null ? "" : contextPath) + uri;

		Assert.assertEquals(expectedUri, actualUri);
	}

	@Test
	public void testDefault() throws Exception {
		ActionResolver resolver = lookup(ActionResolver.class);

		assertResolve(resolver, "/book");
		assertResolve(resolver, "/");
		assertResolve(resolver, "/book/");
		assertResolve(resolver, "/book/add?op=add");
		assertResolve(resolver, "/book/add/");
		assertResolve(resolver, "/book/add/1");
		assertResolve(resolver, "/book/add/name/");
		assertResolve(resolver, "/book/add/1/name?op=submit");
	}
}
