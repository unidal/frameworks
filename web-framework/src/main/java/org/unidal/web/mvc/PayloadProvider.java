package org.unidal.web.mvc;

import java.util.List;

import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.payload.ParameterProvider;

public interface PayloadProvider<S extends Page, T extends Action> {
	public void register(Class<?> payloadClass);

	public List<ErrorObject> process(UrlMapping mapping, ParameterProvider parameterProvider, ActionPayload<S, T> payload);
}
