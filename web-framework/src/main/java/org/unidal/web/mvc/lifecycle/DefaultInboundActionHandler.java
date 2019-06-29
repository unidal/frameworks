package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.createInstance;
import static org.unidal.lookup.util.ReflectUtils.invokeMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unidal.cat.Cat;
import org.unidal.cat.message.Transaction;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.util.ReflectUtils;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.PayloadProvider;
import org.unidal.web.mvc.Validator;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.payload.annotation.PayloadProviderMeta;

@Named(type = InboundActionHandler.class, instantiationStrategy = Named.PER_LOOKUP)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DefaultInboundActionHandler extends ContainerHolder implements InboundActionHandler, LogEnabled {
   private InboundActionModel m_inboundAction;

   private Class<?> m_payloadClass;

   private PayloadProvider m_payloadProvider;

   private List<Validator<ActionContext<?>>> m_preValidators;

   private List<Validator<ActionContext<?>>> m_validators;

   private List<Validator<ActionContext<?>>> m_postValidators;

   private Logger m_logger;

   private PayloadProvider createPayloadProviderInstance(Class<? extends PayloadProvider> clazz) {
      if (hasComponent(clazz)) {
         return lookup(clazz);
      } else {
         // create a POJO instance with default constructor
         return ReflectUtils.createInstance(clazz);
      }
   }

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public void handle(ActionContext ctx) throws ActionException {
      Transaction t = Cat.newTransaction("MVC", "InboundPhase");

      try {
         for (Validator<ActionContext<?>> validator : m_preValidators) {
            validator.validate(ctx);
         }

         if (ctx.getPayload() == null) {
            preparePayload(ctx);
         }

         for (Validator<ActionContext<?>> validator : m_validators) {
            validator.validate(ctx);
         }

         invokeMethod(m_inboundAction.getActionMethod(), m_inboundAction.getModuleInstance(), ctx);

         for (Validator<ActionContext<?>> validator : m_postValidators) {
            validator.validate(ctx);
         }

         t.setStatus(Transaction.SUCCESS);
      } catch (Exception e) {
         String actionName = m_inboundAction.getActionName();

         Cat.logError(e);
         t.setStatus(e);
         throw new ActionException("Error occured during handling inbound action(" + actionName + ")!", e);
      } finally {
         t.complete();
      }
   }

   public void initialize(InboundActionModel inboundAction) {
      m_inboundAction = inboundAction;
      m_payloadClass = inboundAction.getPayloadClass();

      if (m_payloadClass != null) {
         PayloadProviderMeta providerMeta = m_payloadClass.getAnnotation(PayloadProviderMeta.class);

         if (providerMeta == null) {
            m_payloadProvider = createPayloadProviderInstance(PayloadProvider.class);
         } else {
            m_payloadProvider = createPayloadProviderInstance((Class<? extends PayloadProvider>) providerMeta.value());
         }

         m_payloadProvider.register(m_payloadClass);
      }

      prepareValidators(inboundAction);

      m_logger.debug(getClass().getSimpleName() + " initialized for  " + inboundAction.getActionName());
   }

   @Override
   public void preparePayload(ActionContext ctx) {
      if (m_payloadClass != null) {
         RequestContext requestContext = ctx.getRequestContext();
         ActionPayload payload = createInstance(m_payloadClass);

         payload.setPage(requestContext.getAction());
         m_payloadProvider.process(requestContext.getUrlMapping(), requestContext.getParameterProvider(), payload);
         payload.validate(ctx);
         ctx.setPayload(payload);
      }
   }

   private void prepareValidators(InboundActionModel inboundAction) {
      Map<String, Validator> validators = lookupMap(Validator.class);

      m_preValidators = new ArrayList<Validator<ActionContext<?>>>();
      m_validators = new ArrayList<Validator<ActionContext<?>>>();
      m_postValidators = new ArrayList<Validator<ActionContext<?>>>();

      for (Class<?> validatorClass : inboundAction.getValidationClasses()) {
         Validator<ActionContext<?>> validator;

         if (hasComponent(validatorClass)) {
            validator = (Validator<ActionContext<?>>) lookup(validatorClass);
         } else {
            validator = createInstance(validatorClass);
         }

         m_validators.add(validator);
      }

      for (Map.Entry<String, Validator> e : validators.entrySet()) {
         if (e.getKey().startsWith("^")) {
            m_preValidators.add(e.getValue());
         } else if (e.getKey().startsWith("$")) {
            m_postValidators.add(e.getValue());
         } else {
            m_validators.add(e.getValue());
         }
      }
   }
}
