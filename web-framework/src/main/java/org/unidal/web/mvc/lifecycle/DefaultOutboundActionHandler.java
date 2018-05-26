package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.invokeMethod;

import org.unidal.cat.Cat;
import org.unidal.cat.message.Transaction;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.model.entity.OutboundActionModel;

@Named(type = OutboundActionHandler.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultOutboundActionHandler implements OutboundActionHandler, LogEnabled {
   private OutboundActionModel m_outboundAction;

   private Logger m_logger;

   public void handle(ActionContext<?> context) throws ActionException {
      Transaction t = Cat.newTransaction("MVC", "OutboundPhase");

      try {
         invokeMethod(m_outboundAction.getMethod(), m_outboundAction.getModuleInstance(), context);
         t.setStatus(Transaction.SUCCESS);
      } catch (RuntimeException e) {
         String actionName = m_outboundAction.getActionName();

         Cat.logError(e);
         t.setStatus(e);
         throw new ActionException("Error occured during handling outbound action(" + actionName + ")", e);
      } finally {
         t.complete();
      }
   }

   public void initialize(OutboundActionModel outboundAction) {
      m_outboundAction = outboundAction;
      m_logger.debug(getClass().getSimpleName() + " initialized for  " + outboundAction.getActionName());
   }

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }
}
