package org.unidal.lookup.container.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unidal.lookup.ComponentLookupException;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.container.ComponentKey;
import org.unidal.lookup.container.ComponentManager;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.logging.Logger;

public class ComponentLifecycle {
   private List<LifecycleHandler> m_handlers = new ArrayList<LifecycleHandler>();

   private ComponentManager m_manager;

   public ComponentLifecycle(ComponentManager manager) {
      m_manager = manager;

      m_handlers.add(ComponentHandlers.REQUIREMENTS);
      m_handlers.add(ComponentHandlers.ENABLE_LOG);
      m_handlers.add(ComponentHandlers.ENABLE_ROLE_HINT);
      m_handlers.add(ComponentHandlers.CONTEXTUALIZABLE);
      m_handlers.add(ComponentHandlers.CONFIGURATION);
      m_handlers.add(ComponentHandlers.INITIALIZABLE);
      m_handlers.add(ComponentHandlers.DISPOSABLE);
   }

   public void addHandle(LifecycleHandler handler) {
      m_handlers.add(handler);
   }

   public void onStarted(ComponentModel model) {
      m_manager.log("Loaded component(%s:%s) with class(%s) ...", model.getRole(), model.getHint(),
            model.getImplementation());
   }

   public void onStarting(ComponentModel model) {
      m_manager.log("Loading component(%s:%s) with class(%s) ...", model.getRole(), model.getHint(),
            model.getImplementation());
   }

   public void start(Object component, ComponentModel model) throws ComponentLookupException {
      LifecycleContext ctx = new ComponentContext(component).setComponentModel(model);

      for (LifecycleHandler handler : m_handlers) {
         handler.handleStart(ctx);
      }
   }

   public void stop(Object component) {
      LifecycleContext ctx = new ComponentContext(component);

      for (LifecycleHandler handler : m_handlers) {
         handler.handleStop(ctx);
      }
   }

   private class ComponentContext implements LifecycleContext {
      private Object m_component;

      private ComponentModel m_model;

      public ComponentContext(Object component) {
         m_component = component;
      }

      @Override
      public Object getComponent() {
         return m_component;
      }

      @Override
      public ComponentModel getComponentModel() {
         return m_model;
      }

      @Override
      public PlexusContainer getContainer() {
         return m_manager.getContainer();
      }

      @Override
      public Logger getLogger(String role) {
         return m_manager.getLoggerManager().getLoggerForComponent(role);
      }

      @Override
      public Object lookup(String role, String roleHint) throws ComponentLookupException {
         return m_manager.lookup(new ComponentKey(role, roleHint));
      }

      @Override
      public List<Object> lookupList(String role) throws ComponentLookupException {
         return m_manager.lookupList(role);
      }

      @Override
      public Map<String, Object> lookupMap(String role) throws ComponentLookupException {
         return m_manager.lookupMap(role);
      }

      public ComponentContext setComponentModel(ComponentModel model) {
         m_model = model;
         return this;
      }
   }
}
