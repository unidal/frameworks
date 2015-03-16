package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.invokeMethod;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.model.entity.TransitionModel;

import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;

@Named(type = TransitionHandler.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultTransitionHandler implements TransitionHandler {
   @Inject
   private MessageProducer m_cat;

   private TransitionModel m_transition;

   public void handle(ActionContext<?> context) throws ActionException {
      Transaction t = m_cat.newTransaction("MVC", "TransitionPhase");

      try {
         invokeMethod(m_transition.getMethod(), m_transition.getModuleInstance(), context);
         t.setStatus(Transaction.SUCCESS);
      } catch (RuntimeException e) {
         String transitionName = m_transition.getTransitionName();

         m_cat.logError(e);
         t.setStatus(e);
         throw new ActionException("Error occured during handling transition(" + transitionName + ")", e);
      } finally {
         t.complete();
      }
   }

   public void initialize(TransitionModel transition) {
      m_transition = transition;
   }
}
