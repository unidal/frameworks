package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.invokeMethod;

import org.unidal.cat.Cat;
import org.unidal.cat.message.Transaction;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.model.entity.TransitionModel;

@Named(type = TransitionHandler.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultTransitionHandler implements TransitionHandler {
   private TransitionModel m_transition;

   public void handle(ActionContext<?> context) throws ActionException {
      Transaction t = Cat.newTransaction("MVC", "TransitionPhase");

      try {
         invokeMethod(m_transition.getMethod(), m_transition.getModuleInstance(), context);
         t.setStatus(Transaction.SUCCESS);
      } catch (RuntimeException e) {
         String transitionName = m_transition.getTransitionName();

         Cat.logError(e);
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
