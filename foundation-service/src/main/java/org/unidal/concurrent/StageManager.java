package org.unidal.concurrent;

public interface StageManager {
   public <T> Stage<T> getStage(String id);
}
