package org.unidal.web.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.lifecycle.ActionResolver;
import org.unidal.web.lifecycle.DefaultActionResolver;
import org.unidal.web.lifecycle.RequestLifecycle;
import org.unidal.web.mvc.lifecycle.ActionHandlerManager;
import org.unidal.web.mvc.lifecycle.DefaultActionHandlerManager;
import org.unidal.web.mvc.lifecycle.DefaultErrorHandler;
import org.unidal.web.mvc.lifecycle.DefaultInboundActionHandler;
import org.unidal.web.mvc.lifecycle.DefaultOutboundActionHandler;
import org.unidal.web.mvc.lifecycle.DefaultRequestLifecycle;
import org.unidal.web.mvc.lifecycle.DefaultTransitionHandler;
import org.unidal.web.mvc.lifecycle.ErrorHandler;
import org.unidal.web.mvc.lifecycle.InboundActionHandler;
import org.unidal.web.mvc.lifecycle.OutboundActionHandler;
import org.unidal.web.mvc.lifecycle.TransitionHandler;
import org.unidal.web.mvc.model.AnnotationMatrix;
import org.unidal.web.mvc.model.ModelManager;
import org.unidal.web.mvc.model.ModuleRegistry;
import org.unidal.web.mvc.payload.DefaultPayloadProvider;
import org.unidal.web.mvc.payload.MultipartParameterProvider;
import org.unidal.web.mvc.payload.ParameterProvider;
import org.unidal.web.mvc.payload.UrlEncodedParameterProvider;
import org.unidal.web.mvc.view.model.DefaultModelHandler;
import org.unidal.web.mvc.view.model.ModelBuilder;
import org.unidal.web.mvc.view.model.ModelHandler;
import org.unidal.web.mvc.view.model.XmlModelBuilder;

import com.dianping.cat.message.MessageProducer;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(C(AnnotationMatrix.class).is(PER_LOOKUP));
      all.add(C(ModelManager.class).req(ModuleRegistry.class, AnnotationMatrix.class));
      all.add(C(ActionResolver.class, DefaultActionResolver.class));
      all.add(C(InboundActionHandler.class, DefaultInboundActionHandler.class).is(PER_LOOKUP) //
            .req(MessageProducer.class));
      all.add(C(OutboundActionHandler.class, DefaultOutboundActionHandler.class).is(PER_LOOKUP) //
            .req(MessageProducer.class));
      all.add(C(TransitionHandler.class, DefaultTransitionHandler.class).is(PER_LOOKUP) //
            .req(MessageProducer.class));
      all.add(C(ErrorHandler.class, DefaultErrorHandler.class));
      all.add(C(DefaultPayloadProvider.class));
      all.add(C(ActionHandlerManager.class, DefaultActionHandlerManager.class));
      all.add(C(RequestLifecycle.class, "mvc", DefaultRequestLifecycle.class) //
            .req(ModelManager.class, ActionHandlerManager.class));

      all.add(C(ParameterProvider.class, "application/x-www-form-urlencoded", UrlEncodedParameterProvider.class) //
            .is(PER_LOOKUP));
      all.add(C(ParameterProvider.class, "multipart/form-data", MultipartParameterProvider.class) //
            .is(PER_LOOKUP));

      all.add(C(ModelHandler.class, DefaultModelHandler.class) //
            .req(ModelBuilder.class, "xml", "m_xmlBuilder"));
      all.add(C(ModelBuilder.class, "xml", XmlModelBuilder.class));

      return all;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }
}
