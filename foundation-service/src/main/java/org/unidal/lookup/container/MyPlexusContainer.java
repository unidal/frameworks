package org.unidal.lookup.container;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.unidal.lookup.container.model.entity.ComponentModel;

public class MyPlexusContainer implements PlexusContainer {
   private ComponentManager m_manager;

   private Context m_context;

   public MyPlexusContainer() throws Exception {
      this(null);
   }

   public MyPlexusContainer(InputStream in) throws Exception {
      m_manager = new ComponentManager(this, in);
      m_context = new MyPlexusContainerContext(this);
   }

   @Override
   public void addComponent(Object component, String role) throws CycleDetectedInComponentGraphException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> void addComponent(T component, Class<?> role, String roleHint) {
      m_manager.register(new ComponentKey(role, roleHint), component);
   }

   @Override
   public void addComponentDescriptor(ComponentDescriptor<?> componentDescriptor)
         throws CycleDetectedInComponentGraphException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addComponentModel(ComponentModel component)
         throws CycleDetectedInComponentGraphException {
      m_manager.addComponentModel(component);
   }

   @Override
   public void addContextValue(Object key, Object value) {
      m_context.put(key, value);
   }

   @Override
   public ClassRealm createChildRealm(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ComponentDescriptor<?>> discoverComponents(ClassRealm childRealm) throws PlexusConfigurationException,
         CycleDetectedInComponentGraphException {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ComponentDescriptor<?>> discoverComponents(ClassRealm realm, Object data)
         throws PlexusConfigurationException, CycleDetectedInComponentGraphException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void dispose() {
      m_manager.destroy();
   }

   @Override
   public <T> ComponentDescriptor<T> getComponentDescriptor(Class<T> type, String role, String roleHint) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ComponentDescriptor<?> getComponentDescriptor(String role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ComponentDescriptor<?> getComponentDescriptor(String role, String roleHint) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<ComponentDescriptor<T>> getComponentDescriptorList(Class<T> type, String role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<ComponentDescriptor<?>> getComponentDescriptorList(String role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap(Class<T> type, String role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<String, ComponentDescriptor<?>> getComponentDescriptorMap(String role) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ClassRealm getComponentRealm(String realmId) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ClassRealm getContainerRealm() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Context getContext() {
      return m_context;
   }

   @Override
   public ClassRealm getLookupRealm() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ClassRealm getLookupRealm(Object component) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean hasComponent(Class<?> type) {
      return hasComponent(type.getName(), null);
   }

   @Override
   public boolean hasComponent(Class<?> type, String roleHint) {
      return hasComponent(type.getName(), roleHint);
   }

   @Override
   public boolean hasComponent(Class<?> type, String role, String roleHint) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean hasComponent(String role) {
      return hasComponent(role, null);
   }

   @Override
   public boolean hasComponent(String role, String roleHint) {
      return m_manager.hasComponent(new ComponentKey(role, roleHint));
   }

   @Override
   public <T> T lookup(Class<T> type) throws ComponentLookupException {
      return lookup(type, null);
   }

   @Override
   public <T> T lookup(Class<T> type, String roleHint) throws ComponentLookupException {
      return m_manager.lookup(new ComponentKey(type, roleHint));
   }

   @Override
   public <T> T lookup(Class<T> type, String role, String roleHint) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> T lookup(ComponentDescriptor<T> componentDescriptor) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object lookup(String role) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object lookup(String role, String roleHint) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> List<T> lookupList(Class<T> type) throws ComponentLookupException {
      return m_manager.lookupList(type.getName());
   }

   @Override
   public <T> List<T> lookupList(Class<T> type, List<String> roleHints) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<Object> lookupList(String role) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<Object> lookupList(String role, List<String> roleHints) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> Map<String, T> lookupMap(Class<T> type) throws ComponentLookupException {
      return m_manager.lookupMap(type.getName());
   }

   @Override
   public <T> Map<String, T> lookupMap(Class<T> type, List<String> roleHints) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<String, Object> lookupMap(String role) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<String, Object> lookupMap(String role, List<String> roleHints) throws ComponentLookupException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void registerComponentDiscoveryListener(ComponentDiscoveryListener listener) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void release(Object component) throws ComponentLifecycleException {
      m_manager.release(component);
   }

   @Override
   public void releaseAll(List<?> components) throws ComponentLifecycleException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void releaseAll(Map<String, ?> components) throws ComponentLifecycleException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removeComponentDiscoveryListener(ComponentDiscoveryListener listener) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removeComponentRealm(ClassRealm componentRealm) throws PlexusContainerException {
      throw new UnsupportedOperationException();
   }

   @Override
   public ClassRealm setLookupRealm(ClassRealm realm) {
      throw new UnsupportedOperationException();
   }
}
