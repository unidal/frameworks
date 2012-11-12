package org.unidal.lookup.configuration;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

public class Configurators {
   private Configurators() {
   }

   public static final Configurators forPlexus() {
      return new Configurators();
   }

   private XmlPlexusConfiguration generateComponent(Component c) {
      XmlPlexusConfiguration component = new XmlPlexusConfiguration("component");
      ComponentDescriptor<Object> d = c.getDescriptor();

      XmlPlexusConfiguration role = new XmlPlexusConfiguration("role");
      role.setValue(d.getRole());
      component.addChild(role);

      if (d.getRoleHint() != null && !d.getRoleHint().equals("default")) {
         XmlPlexusConfiguration roleHint = new XmlPlexusConfiguration("role-hint");
         roleHint.setValue(d.getRoleHint());
         component.addChild(roleHint);
      }

      XmlPlexusConfiguration implementation = new XmlPlexusConfiguration("implementation");
      implementation.setValue(d.getImplementation());
      component.addChild(implementation);

      if (d.getInstantiationStrategy() != null) {
         XmlPlexusConfiguration instantiationStrategy = new XmlPlexusConfiguration("instantiation-strategy");
         instantiationStrategy.setValue(d.getInstantiationStrategy());
         component.addChild(instantiationStrategy);
      }

      if (d.getLifecycleHandler() != null) {
         XmlPlexusConfiguration lifecycleHandler = new XmlPlexusConfiguration("lifecycle-handler");
         lifecycleHandler.setValue(d.getLifecycleHandler());
         component.addChild(lifecycleHandler);
      }

      if (c.getConfiguration() != null) {
         component.addChild(c.getConfiguration());
      }

      if (!c.getRequirements().isEmpty()) {
         XmlPlexusConfiguration requirements = new XmlPlexusConfiguration("requirements");

         for (ComponentRequirement r : c.getRequirements()) {
            requirements.addChild(generateComponentRequirement(r));
         }

         component.addChild(requirements);
      }

      return component;
   }

   private XmlPlexusConfiguration generateComponentRequirement(ComponentRequirement r) {
      XmlPlexusConfiguration requirement = new XmlPlexusConfiguration("requirement");

      XmlPlexusConfiguration role = new XmlPlexusConfiguration("role");
      role.setValue(r.getRole());
      requirement.addChild(role);

      if (r.getRoleHint() != null && !r.getRoleHint().equals("default")) {
         if (r instanceof ComponentRequirementList) {
            ComponentRequirementList list = (ComponentRequirementList) r;
            XmlPlexusConfiguration roleHints = new XmlPlexusConfiguration("role-hints");

            for (String hint : (List<String>) list.getRoleHints()) {
               XmlPlexusConfiguration roleHint = new XmlPlexusConfiguration("role-hint");
               roleHint.setValue(hint);
               roleHints.addChild(roleHint);
            }
            requirement.addChild(roleHints);
         } else {
            XmlPlexusConfiguration roleHint = new XmlPlexusConfiguration("role-hint");
            roleHint.setValue(r.getRoleHint());
            requirement.addChild(roleHint);
         }
      }

      if (r.getFieldName() != null) {
         XmlPlexusConfiguration fieldName = new XmlPlexusConfiguration("field-name");
         fieldName.setValue(r.getFieldName());
         requirement.addChild(fieldName);
      }

      return requirement;
   }

   public String generateXmlConfiguration(List<Component> all) {
      XmlPlexusConfiguration plexus = new XmlPlexusConfiguration("plexus");
      XmlPlexusConfiguration components = new XmlPlexusConfiguration("components");

      for (Component item : all) {
         components.addChild(generateComponent(item));
      }

      plexus.addChild(components);

      return toString(plexus);
   }

   public String toString(XmlPlexusConfiguration plexus) {
      StringWriter sw = new StringWriter();
      XmlPlexusConfigurationWriter xw = new XmlPlexusConfigurationWriter();

      try {
         xw.write(sw, plexus);
      } catch (IOException e) {
         // will not happen with StringWriter
      }

      return sw.toString();
   }
}
