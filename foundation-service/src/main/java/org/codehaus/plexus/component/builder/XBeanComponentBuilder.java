/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.component.builder;

import static org.apache.xbean.recipe.RecipeHelper.toClass;

import org.apache.xbean.recipe.AbstractRecipe;
import org.apache.xbean.recipe.ConstructionException;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.RecipeHelper;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.ComponentRegistry;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.plexus.component.collections.ComponentList;
import org.codehaus.plexus.component.collections.ComponentMap;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.MapConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

// TODO hack of plexus-container-default-1.6, need to follow up bug fix in the future versions
@SuppressWarnings({"serial", "rawtypes", "unchecked", "unused"})
public class XBeanComponentBuilder<T> implements ComponentBuilder<T> {
    private static final ThreadLocal<LinkedHashSet<ComponentDescriptor<?>>> STACK =
        new ThreadLocal<LinkedHashSet<ComponentDescriptor<?>>>()
        {
            protected LinkedHashSet<ComponentDescriptor<?>> initialValue()
            {
                return new LinkedHashSet<ComponentDescriptor<?>>();
            }
        };

    private ComponentManager<T> componentManager;

    public XBeanComponentBuilder() {
    }

    public XBeanComponentBuilder(ComponentManager<T> componentManager) {
        setComponentManager(componentManager);
    }

    public ComponentManager<T> getComponentManager() {
        return componentManager;
    }

    public void setComponentManager(ComponentManager<T> componentManager) {
        this.componentManager = componentManager;
    }

    protected MutablePlexusContainer getContainer() {
        return componentManager.getContainer();
    }

    public T build( ComponentDescriptor<T> descriptor, ClassRealm realm, ComponentBuildListener listener )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        LinkedHashSet<ComponentDescriptor<?>> stack = STACK.get();
        if ( stack.contains( descriptor ) )
        {
            // create list of circularity
            List<ComponentDescriptor<?>> circularity = new ArrayList<ComponentDescriptor<?>>( stack );
            circularity.subList( circularity.indexOf( descriptor ), circularity.size() );
            circularity.add( descriptor );

            // nice circularity message
            String message = "Creation circularity: ";
            for ( ComponentDescriptor<?> componentDescriptor : circularity )
            {
                message += "\n\t[" + componentDescriptor.getRole() + ", " + componentDescriptor.getRoleHint() + "]";
            }
            throw new ComponentInstantiationException( message );
        }
        stack.add( descriptor );
        try
        {
            if (listener != null) {
                listener.beforeComponentCreate(descriptor, realm);
            }

            T component = createComponentInstance(descriptor, realm);

            if (listener != null) {
                listener.componentCreated(descriptor, component, realm);
            }

            startComponentLifecycle(component, realm);

            if (listener != null) {
                listener.componentConfigured(descriptor, component, realm);
            }

            return component;
        }
        finally
        {
            stack.remove( descriptor );
        }
    }

    protected T createComponentInstance(ComponentDescriptor<T> descriptor, ClassRealm realm) throws ComponentInstantiationException, ComponentLifecycleException {
        MutablePlexusContainer container = getContainer();
        if (realm == null) {
            realm = descriptor.getRealm();
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(realm);
        try {
            ObjectRecipe recipe;

            T instance;
            ComponentFactory componentFactory = container.getComponentFactoryManager().findComponentFactory(descriptor.getComponentFactory());
            if (JavaComponentFactory.class.equals(componentFactory.getClass())) {
                // xbean-reflect will create object and do injection
                recipe = createObjectRecipe( null, descriptor, realm );
                instance = (T) recipe.create();
            } else {
                // todo figure out how to easily let xbean use the factory to construct the component
                // use object factory to construct component and then inject into that object
                instance = (T) componentFactory.newInstance(descriptor, realm, container);
                recipe = createObjectRecipe( instance, descriptor, realm );
                recipe.setProperties( instance );
            }

            // todo figure out how to easily let xbean do this map oriented stuff (if it is actually used in plexus)
            if ( instance instanceof MapOrientedComponent) {
                MapOrientedComponent mapOrientedComponent = (MapOrientedComponent) instance;
                processMapOrientedComponent(descriptor, mapOrientedComponent, realm);
            }

            return instance;
        } catch (Exception e) {
            throw new ComponentLifecycleException("Error constructing component " + descriptor.getHumanReadableKey(), e);
        } catch (LinkageError e) {
            throw new ComponentLifecycleException("Error constructing component " + descriptor.getHumanReadableKey(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    public ObjectRecipe createObjectRecipe(T instance, ComponentDescriptor<T> descriptor, ClassRealm realm) throws ComponentInstantiationException, PlexusConfigurationException {
        String factoryMethod = null;
        String[] constructorArgNames = null;
        Class[] constructorArgTypes = null;

        Class<?> implClass = ( instance != null ) ? instance.getClass() : descriptor.getImplementationClass();

        if ( implClass == null || implClass == Object.class )
        {
            // if the descriptor could not load the class, it's time to report this up to the caller now
            try
            {
                realm.loadClass( descriptor.getImplementation() );
            }
            catch ( ClassNotFoundException e )
            {
                throw new ComponentInstantiationException( "Could not load implementation class for component "
                    + descriptor.getHumanReadableKey() + " from class realm " + realm, e );
            }
            catch ( LinkageError e )
            {
                throw new ComponentInstantiationException( "Could not load implementation class for component "
                    + descriptor.getHumanReadableKey() + " from class realm " + realm, e );
            }
        }

        ObjectRecipe recipe = new ObjectRecipe( implClass,
                factoryMethod,
                constructorArgNames,
                constructorArgTypes);
        recipe.allow(Option.FIELD_INJECTION);
        recipe.allow(Option.PRIVATE_PROPERTIES);

        // MapOrientedComponents don't get normal injection
        if (!MapOrientedComponent.class.isAssignableFrom( implClass )) {
            for (ComponentRequirement requirement : descriptor.getRequirements() ) {
                String name = requirement.getFieldName();
                RequirementRecipe requirementRecipe = new RequirementRecipe(descriptor, requirement, getContainer(), name == null);

                if (name != null) {
                    recipe.setProperty(name, requirementRecipe);
                } else {
                    recipe.setAutoMatchProperty(requirement.getRole(), requirementRecipe);
                }
            }

            // add configuration data
            if (shouldConfigure(descriptor )) {
                PlexusConfiguration configuration = descriptor.getConfiguration();
                if (configuration != null) {
                    for (String name : configuration.getAttributeNames()) {
                        String value;
                        try {
                            value = configuration.getAttribute(name);
                        } catch (PlexusConfigurationException e) {
                            throw new ComponentInstantiationException("Error getting value for attribute " + name, e);
                        }
                        name = fromXML(name);
                        recipe.setProperty(name, value);
                    }
                    for (PlexusConfiguration child : configuration.getChildren()) {
                        String name = child.getName();
                        name = fromXML(name);
                        if ( StringUtils.isNotEmpty( child.getValue( null ) ) )
                        {
                            recipe.setProperty( name, child.getValue() );
                        }
                        else
                        {
                            recipe.setProperty( name, new PlexusConfigurationRecipe( child ) );
                        }
                    }
                }
            }
        }
        return recipe;
    }

    protected boolean shouldConfigure( ComponentDescriptor<T> descriptor ) {
        String configuratorId = descriptor.getComponentConfigurator();

        if (StringUtils.isEmpty(configuratorId)) {
            return true;
        }

        try {
            ComponentConfigurator componentConfigurator = getContainer().lookup(ComponentConfigurator.class, configuratorId);
            return componentConfigurator == null || componentConfigurator.getClass().equals(BasicComponentConfigurator.class);
        } catch (ComponentLookupException e) {
        }

        return true;
    }
    protected String fromXML(String elementName) {
        return StringUtils.lowercaseFirstLetter(StringUtils.removeAndHump(elementName, "-"));
    }

    protected void startComponentLifecycle(Object component, ClassRealm realm) throws ComponentLifecycleException {
        try {
            componentManager.start(component);
        } catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error starting component", e);
        }
    }

    public static class RequirementRecipe<T> extends AbstractRecipe {
        private ComponentDescriptor<T> componentDescriptor;
        private ComponentRequirement requirement;
        private MutablePlexusContainer container;
        private boolean autoMatch;

        public RequirementRecipe(ComponentDescriptor<T> componentDescriptor, ComponentRequirement requirement, MutablePlexusContainer container, boolean autoMatch) {
            this.componentDescriptor = componentDescriptor;
            this.requirement = requirement;
            this.container = container;
            this.autoMatch = autoMatch;
        }

        public boolean canCreate(Type expectedType) {
            if (!autoMatch)
            {
                return true;
            }

            Class<?> propertyType = toClass(expectedType);

            // Never auto match array, map or collection
            if (propertyType.isArray() || Map.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType) || requirement instanceof ComponentRequirementList) {
                return false;
            }

            // if the type to be created is an instance of the expected type, return true
            try {
                ComponentRegistry componentRegistry = container.getComponentRegistry();

                return componentRegistry.getComponentDescriptor(propertyType, requirement.getRole(), requirement.getRoleHint()) != null;
            } catch (Exception e) {
            }

            return false;
        }

        @Override
        protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException {
            Class<?> propertyType = toClass(expectedType);

            try {
                String role = requirement.getRole();
                List<String> roleHints = null;
                if (requirement instanceof ComponentRequirementList) {
                    roleHints = ((ComponentRequirementList) requirement).getRoleHints();
                }

                Object assignment;
                if (propertyType.isArray()) {
                    assignment = new ArrayList<Object>(container.lookupList(role, roleHints));
                }

                // Map.class.isAssignableFrom( clazz ) doesn't make sense, since Map.class doesn't really
                // have a meaningful superclass.
                else {
                    if (Map.class.equals(propertyType)) {
                        // todo this is a lazy map

                        // get component type
                        Type keyType = Object.class;
                        Type valueType = Object.class;
                        Type[] typeParameters = RecipeHelper.getTypeParameters(Collection.class, expectedType);
                        if (typeParameters != null && typeParameters.length == 2) {
                            if (typeParameters[0] instanceof Class) {
                                keyType = typeParameters[0];
                            }
                            if (typeParameters[1] instanceof Class) {
                                valueType = typeParameters[1];
                            }
                        }

                        // todo verify key type is String

                        assignment = new ComponentMap(container,
                                toClass(valueType),
                                role,
                                roleHints,
                                componentDescriptor.getHumanReadableKey());
                    }
                    // List.class.isAssignableFrom( clazz ) doesn't make sense, since List.class doesn't really
                    // have a meaningful superclass other than Collection.class, which we'll handle next.
                    else if (List.class.equals(propertyType)) {
                        // todo this is a lazy list

                        // get component type
                        Type[] typeParameters = RecipeHelper.getTypeParameters(Collection.class, expectedType);
                        Type componentType = Object.class;
                        if (typeParameters != null && typeParameters.length == 1 && typeParameters[0] instanceof Class) {
                            componentType = typeParameters[0];
                        }

                        assignment = new ComponentList(container,
                                toClass( componentType ),
                                role,
                                roleHints,
                                componentDescriptor.getHumanReadableKey());
                    }
                    // Set.class.isAssignableFrom( clazz ) doesn't make sense, since Set.class doesn't really
                    // have a meaningful superclass other than Collection.class, and that would make this
                    // if-else cascade unpredictable (both List and Set extend Collection, so we'll put another
                    // check in for Collection.class.
                    else if (Set.class.equals(propertyType) || Collection.class.isAssignableFrom(propertyType)) {
                        // todo why isn't this lazy as above?
                        assignment = container.lookupMap(role, roleHints);
                    } else if (Logger.class.equals(propertyType)) {
                        // todo magic reference
                        assignment = container.getLoggerManager().getLoggerForComponent(componentDescriptor.getRole());
                    } else if (PlexusContainer.class.equals(propertyType)) {
                        // todo magic reference
                        assignment = container;
                    } else {
                        String roleHint = requirement.getRoleHint();
                        assignment = container.lookup(propertyType, role, roleHint);
                    }
                }

                return assignment;
            } catch (ComponentLookupException e) {
                if ( requirement.isOptional() )
                {
                    return null;
                }

                throw new ConstructionException("Composition failed of field " + requirement.getFieldName() + " "
                        + "in object of type " + componentDescriptor.getImplementation() + " because the requirement "
                        + requirement + " was missing)", e);
            }
        }

        @Override
        public String toString() {
            return "Requirement[fieldName=" + requirement.getFieldName() + ", role=" + requirement.getRole() + "]"; // TODO FIXED by Frankie
        }
    }

    private class PlexusConfigurationRecipe extends AbstractRecipe {
        private final PlexusConfiguration child;

        public PlexusConfigurationRecipe(PlexusConfiguration child) {
            this.child = child;
        }

        public boolean canCreate(Type type) {
            try {
                ConverterLookup lookup = createConverterLookup();
                lookup.lookupConverterForType(toClass(type));
                return true;
            } catch (ComponentConfigurationException e) {
                return false;
            }
        }

        @Override
        protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException {
            try {
                ConverterLookup lookup = createConverterLookup();
                ConfigurationConverter converter = lookup.lookupConverterForType(toClass(expectedType));

                // todo this will not work for static factories
                ObjectRecipe caller = (ObjectRecipe) RecipeHelper.getCaller();
                Class parentClass = toClass(caller.getType());

                Object value = converter.fromConfiguration(lookup, child, toClass(expectedType), parentClass, Thread.currentThread().getContextClassLoader(), new DefaultExpressionEvaluator());
                return value;
            } catch (ComponentConfigurationException e) {
                throw new ConstructionException("Unable to convert configuration for property " + child.getName() + " to " + toClass(expectedType).getName());
            }
        }

        private ConverterLookup createConverterLookup() {
            ClassRealm realm = (ClassRealm) Thread.currentThread().getContextClassLoader();
            ConverterLookup lookup = new DefaultConverterLookup();
            lookup.registerConverter( new ClassRealmConverter(realm) );
            return lookup;
        }
    }


    private void processMapOrientedComponent(ComponentDescriptor<?> descriptor, MapOrientedComponent mapOrientedComponent, ClassRealm realm) throws ComponentConfigurationException, ComponentLookupException {
        MutablePlexusContainer container = getContainer();

        for (ComponentRequirement requirement : descriptor.getRequirements()) {
            String role = requirement.getRole();
            String hint = requirement.getRoleHint();
            String mappingType = requirement.getFieldMappingType();

            Object value;

            // if the hint is not empty (and not default), we don't care about mapping type...
            // it's a single-value, not a collection.
            if (StringUtils.isNotEmpty(hint) && !hint.equals(PlexusConstants.PLEXUS_DEFAULT_HINT)) {
                value = container.lookup(role, hint);
            } else if ("single".equals(mappingType)) {
                value = container.lookup(role, hint);
            } else if ("map".equals(mappingType)) {
                value = container.lookupMap(role);
            } else if ("set".equals(mappingType)) {
                value = new HashSet<Object>(container.lookupList(role));
            } else {
                value = container.lookup(role, hint);
            }

            mapOrientedComponent.addComponentRequirement(requirement, value);
        }

        MapConverter converter = new MapConverter();
        ConverterLookup converterLookup = new DefaultConverterLookup();
        DefaultExpressionEvaluator expressionEvaluator = new DefaultExpressionEvaluator();
        PlexusConfiguration configuration = container.getConfigurationSource().getConfiguration( descriptor );

        if ( configuration != null )
        {
            Map context = (Map) converter.fromConfiguration(converterLookup,
                                                            configuration,
                                                            null,
                                                            null,
                                                            realm,
                                                            expressionEvaluator,
                                                            null );

            mapOrientedComponent.setComponentConfiguration( context );
        }
    }
}
