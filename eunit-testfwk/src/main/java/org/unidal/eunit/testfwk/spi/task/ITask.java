package org.unidal.eunit.testfwk.spi.task;

import org.unidal.eunit.model.entity.EunitMethod;

public interface ITask<T extends ITaskType> {
   public <S> S getAttribute(String name);

   public EunitMethod getEunitMethod();
   
   public T getType();

   public boolean hasAttribute(String name);

   public void setAttribute(String name, Object value);
}