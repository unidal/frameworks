package org.unidal.eunit.testfwk.spi;

import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.spi.task.ITask;
import org.unidal.eunit.testfwk.spi.task.ITaskType;

public interface ICaseContext {
   public Object findAttributeFor(EunitParameter eunitParameter);

   public Object getAttribute(Class<?> targetType, String id);

   public IClassContext getClassContext();

   public EunitClass getEunitClass();

   public EunitMethod getEunitMethod();

   public <M> M getModel();

   public <T extends ITaskType> ITask<T> getTask();

   public Object getTestInstance();

   public Object invokeWithInjection(EunitMethod eunitMethod) throws Throwable;

   public Object removeAttribute(Class<?> type, String id);

   public Object removeAttribute(Object attribute, String id);

   public void setAttribute(Class<?> type, Object attribute, String id);

   public void setAttribute(Object attribute, String id);

   public <T extends ITaskType> void setTask(ITask<T> task);
}
