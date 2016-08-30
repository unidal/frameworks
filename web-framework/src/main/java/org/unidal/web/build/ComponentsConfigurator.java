package org.unidal.web.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.lifecycle.DefaultActionResolver;
import org.unidal.web.mvc.lifecycle.DefaultActionHandlerManager;
import org.unidal.web.mvc.lifecycle.DefaultErrorHandler;
import org.unidal.web.mvc.lifecycle.DefaultInboundActionHandler;
import org.unidal.web.mvc.lifecycle.DefaultOutboundActionHandler;
import org.unidal.web.mvc.lifecycle.DefaultRequestContextBuilder;
import org.unidal.web.mvc.lifecycle.DefaultRequestLifecycle;
import org.unidal.web.mvc.lifecycle.DefaultTransitionHandler;
import org.unidal.web.mvc.model.AnnotationMatrix;
import org.unidal.web.mvc.model.ModelManager;
import org.unidal.web.mvc.payload.DefaultParameterProvider;
import org.unidal.web.mvc.payload.DefaultPayloadProvider;
import org.unidal.web.mvc.payload.MultipartParameterProvider;
import org.unidal.web.mvc.payload.UrlEncodedParameterProvider;
import org.unidal.web.mvc.view.model.DefaultModelHandler;
import org.unidal.web.mvc.view.model.JsonModelBuilder;
import org.unidal.web.mvc.view.model.XmlModelBuilder;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(AnnotationMatrix.class));
      all.add(A(ModelManager.class));
      all.add(A(DefaultActionResolver.class));
      all.add(A(DefaultInboundActionHandler.class));
      all.add(A(DefaultOutboundActionHandler.class));
      all.add(A(DefaultTransitionHandler.class));
      all.add(A(DefaultErrorHandler.class));
      all.add(A(DefaultPayloadProvider.class));
      all.add(A(DefaultActionHandlerManager.class));
      all.add(A(DefaultRequestLifecycle.class));
      all.add(A(DefaultRequestContextBuilder.class));
      
      all.add(A(UrlEncodedParameterProvider.class));
      all.add(A(MultipartParameterProvider.class));
      all.add(A(DefaultParameterProvider.class));
      
      all.add(A(DefaultModelHandler.class));
      all.add(A(XmlModelBuilder.class));
      all.add(A(JsonModelBuilder.class));

      return all;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }
}
