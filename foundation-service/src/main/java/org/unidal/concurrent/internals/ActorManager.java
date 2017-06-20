package org.unidal.concurrent.internals;

import java.util.concurrent.atomic.AtomicBoolean;

import org.unidal.concurrent.Actor;
import org.unidal.concurrent.StageStatus;

public interface ActorManager<E> {
   public void addActor(Actor<E, ?> actor);

   public void distribute(E event, AtomicBoolean enabled) throws InterruptedException;

   public Actor<E, ?> getNextActor();

   public void report(StageStatus status);
}
