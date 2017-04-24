package org.unidal.lookup.container;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.LoggerManager;
import org.unidal.lookup.container.lifecycle.ComponentHandlers;
import org.unidal.lookup.container.lifecycle.ComponentLifecycle;
import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;
import org.unidal.lookup.logger.TimedConsoleLoggerManager;

public class ComponentManager {
   // role => map (role hint => component)
   private Map<String, ComponentBox<?>> m_map = new HashMap<String, ComponentBox<?>>();

   private PlexusContainer m_container;

   private ComponentLifecycle m_lifecycle;

   private ComponentModelManager m_modelManager;

   private TimedConsoleLoggerManager m_loggerManager;

   // for test purpose
   private PlexusModel m_plexus = new PlexusModel();

   private List<ComponentDescriptor<?>> m_descriptors = new ArrayList<ComponentDescriptor<?>>();

   public ComponentManager(PlexusContainer container, InputStream in) throws Exception {
      m_container = container;
      m_modelManager = new ComponentModelManager(in);
      m_lifecycle = new ComponentLifecycle(this);

      m_lifecycle.addHandle(ComponentHandlers.REQUIREMENTS);
      m_lifecycle.addHandle(ComponentHandlers.ENABLE_LOG);
      m_lifecycle.addHandle(ComponentHandlers.ENABLE_ROLE_HINT);
      m_lifecycle.addHandle(ComponentHandlers.CONTEXTUALIZABLE);
      m_lifecycle.addHandle(ComponentHandlers.CONFIGURATION);
      m_lifecycle.addHandle(ComponentHandlers.INITIALIZABLE);

      m_loggerManager = new TimedConsoleLoggerManager();
      m_loggerManager.setShowClass(true);
      m_loggerManager.initialize();

      register(new ComponentKey(LoggerManager.class, null), m_loggerManager);
   }

   public void addComponentDescriptor(ComponentDescriptor<?> descriptor) {
      m_descriptors.add(descriptor);
   }

   public void addComponentDescriptor(ComponentModel component) {
      m_plexus.addComponent(component);
   }

   public void destroy() {
      m_plexus = new PlexusModel();
      m_descriptors.clear();
      m_map.clear();
   }

   private ComponentModel getComponentModel(ComponentDescriptor<?> descriptor) {
      ComponentModel model = new ComponentModel();

      model.setRole(descriptor.getRole());
      model.setRoleHint(descriptor.getRoleHint());
      model.setImplementation(descriptor.getImplementation());

      if (descriptor.getInstantiationStrategy() != null) {
         model.setInstantiationStrategy(descriptor.getInstantiationStrategy());
      }

      PlexusConfiguration c = descriptor.getConfiguration();

      if (c != null) {
         ConfigurationModel config = new ConfigurationModel();
         List<Any> properties = config.getDynamicElements();

         for (PlexusConfiguration p : c.getChildren()) {
            properties.add(new Any().setName(p.getName()).setValue(p.getValue("")));
         }

         model.setConfiguration(config);
      }

      for (ComponentRequirement r : descriptor.getRequirements()) {
         RequirementModel req = new RequirementModel();

         req.setRole(r.getRole());
         req.setRoleHint(r.getRoleHint());
         req.setFieldName(r.getFieldName());
         model.addRequirement(req);
      }

      return model;
   }

   public PlexusContainer getContainer() {
      return m_container;
   }

   public LoggerManager getLoggerManager() {
      return m_loggerManager;
   }

   public boolean hasComponent(ComponentKey key) {
      for (ComponentModel component : m_plexus.getComponents()) {
         if (key.matches(component.getRole(), component.getRoleHint())) {
            return true;
         }
      }

      for (ComponentDescriptor<?> descriptor : m_descriptors) {
         if (key.getRole().equals(descriptor.getRole()) && key.getRoleHint().equals(descriptor.getRoleHint())) {
            return true;
         }
      }

      return m_modelManager.hasComponentModel(key);
   }

   @SuppressWarnings("unchecked")
   public <T> T lookup(ComponentKey key) throws ComponentLookupException {
      String role = key.getRole();
      ComponentBox<?> box = m_map.get(role);

      if (box == null) {
         box = new ComponentBox<T>(m_lifecycle);
         m_map.put(role, box);
      }

      ComponentModel model = null;

      for (ComponentModel component : m_plexus.getComponents()) {
         if (key.matches(component.getRole(), component.getRoleHint())) {
            model = component;
            break;
         }
      }

      if (model == null) {
         for (ComponentDescriptor<?> descriptor : m_descriptors) {
            if (key.getRole().equals(descriptor.getRole()) && key.getRoleHint().equals(descriptor.getRoleHint())) {
               model = getComponentModel(descriptor);
               break;
            }
         }
      }

      if (model == null) {
         model = m_modelManager.getComponentModel(key);
      }

      if (model != null) {
         return (T) box.lookup(model);
      } else {
         throw new ComponentLookupException("No component defined!", role, key.getRoleHint());
      }
   }

   public <T> List<T> lookupList(String role) throws ComponentLookupException {
      List<String> roleHints = m_modelManager.getRoleHints(role);
      List<T> components = new ArrayList<T>();

      for (String roleHint : roleHints) {
         T component = lookup(new ComponentKey(role, roleHint));

         components.add(component);
      }

      return components;
   }

   public <T> Map<String, T> lookupMap(String role) throws ComponentLookupException {
      List<String> roleHints = m_modelManager.getRoleHints(role);
      Map<String, T> components = new LinkedHashMap<String, T>();

      for (String roleHint : roleHints) {
         T component = lookup(new ComponentKey(role, roleHint));

         components.put(roleHint, component);
      }

      return components;
   }

   public void register(ComponentKey key, Object component) {
      m_map.put(key.getRole(), new ComponentBox<Object>(key, component));
      m_modelManager.setComponentModel(key, component.getClass());
   }

   public void release(Object component) {
      // do nothing here
   }
}
