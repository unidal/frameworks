package org.unidal.concurrent.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.unidal.concurrent.Actor;
import org.unidal.concurrent.ActorContext;
import org.unidal.concurrent.ActorState;
import org.unidal.concurrent.StageStatus;
import org.unidal.lookup.annotation.Named;

@Named(type = ActorManager.class, instantiationStrategy = Named.PER_LOOKUP)
public class DefaultActorManager<E> implements ActorManager<E> {
   private List<Actor<E, ?>> m_actors = new ArrayList<Actor<E, ?>>();

   @Override
   public void addActor(Actor<E, ?> actor) {
      m_actors.add(actor);
   }

   private void distribute(ActorContext<E> ctx, E event, AtomicBoolean enabled) throws InterruptedException {
      boolean success = ctx.addLast(event);

      while (!success && enabled.get()) {
         success = ctx.addLast(event);
      }
   }

   @Override
   public void distribute(E event, AtomicBoolean enabled) throws InterruptedException {
      int len = m_actors.size();

      for (int i = 0; i < len; i++) {
         Actor<E, ?> actor = m_actors.get(i);

         distribute(actor.getContext(), event, enabled);
      }
   }

   @Override
   public Actor<E, ?> getNextActor() {
      int len = m_actors.size();
      int max = 0;
      int index = -1;

      for (int i = 0; i < len; i++) {
         Actor<E, ?> actor = m_actors.get(i);
         ActorState state = actor.checkState();

         if (state != null && state.isRunnable()) {
            int available = actor.getContext().available();

            if (available > max) {
               max = available;
               index = i;
            }
         }
      }

      if (index >= 0) {
         return m_actors.get(index);
      } else {
         return null;
      }
   }

   @Override
   public void report(StageStatus status) {
      int len = m_actors.size();
      int[] counts = new int[len];

      for (int i = 0; i < len; i++) {
         Actor<E, ?> actor = m_actors.get(i);
         int count = actor.getContext().available();

         counts[i] = count;
      }

      if (status instanceof DefaultStageStatus) {
         ((DefaultStageStatus) status).setAvailableCounts(counts);
      }
   }
}
