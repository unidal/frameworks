package org.unidal.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractActor<E, C extends ActorContext<E>> implements Actor<E, C> {
   private C m_ctx;

   private AtomicReference<ActorState> m_state = new AtomicReference<ActorState>(ActorState.WAITING);

   public AbstractActor(C ctx) {
      m_ctx = ctx;
   }

   @Override
   public ActorState checkState() {
      ActorState state = m_state.get();

      if (state.isWaiting()) {
         if (m_ctx.isBatchReady()) {
            state = ActorState.RUNNABLE;

            m_state.set(state);
         }
      }

      return state;
   }

   @Override
   public C getContext() {
      return m_ctx;
   }

   @Override
   public void play() throws InterruptedException {
      m_state.set(ActorState.RUNNING);

      run(m_ctx);

      if (m_ctx.isBatchReady()) {
         m_state.set(ActorState.RUNNABLE);
      } else {
         m_state.set(ActorState.WAITING);
      }
   }

   protected abstract void run(C ctx) throws InterruptedException;
}
