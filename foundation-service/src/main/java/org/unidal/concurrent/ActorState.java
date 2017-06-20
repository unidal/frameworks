package org.unidal.concurrent;

public enum ActorState {
   WAITING,

   RUNNABLE,

   RUNNING;

   public boolean isRunnable() {
      return this == RUNNABLE;
   }

   public boolean isRunning() {
      return this == RUNNING;
   }

   public boolean isWaiting() {
      return this == WAITING;
   }
}
