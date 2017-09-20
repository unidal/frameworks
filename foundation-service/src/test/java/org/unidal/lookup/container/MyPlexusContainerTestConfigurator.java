package org.unidal.lookup.container;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.lookup.container.MyPlexusContainerTest.C11;
import org.unidal.lookup.container.MyPlexusContainerTest.C12;
import org.unidal.lookup.container.MyPlexusContainerTest.C13;
import org.unidal.lookup.container.MyPlexusContainerTest.C21;
import org.unidal.lookup.container.MyPlexusContainerTest.C22;
import org.unidal.lookup.container.MyPlexusContainerTest.C23;
import org.unidal.lookup.container.MyPlexusContainerTest.C24;
import org.unidal.lookup.container.MyPlexusContainerTest.C25;
import org.unidal.lookup.container.MyPlexusContainerTest.C26;
import org.unidal.lookup.container.MyPlexusContainerTest.E1;

public class MyPlexusContainerTestConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(C11.class));
      all.add(A(C12.class));
      all.add(A(C13.class));

      for (E1 value : E1.values()) {
         all.add(A(E1.class, value.name()));
      }

      all.add(A(C21.class));
      all.add(A(C22.class));
      all.add(A(C23.class));
      all.add(A(C24.class).config( //
            E("boolean").value("true"), //
            E("byte").value("1"), //
            E("char").value("1"), //
            E("short").value("1"), //
            E("int").value("1"), //
            E("long").value("1"), //
            E("float").value("1"), //
            E("double").value("1"), //
            E("string-value").value("string-value"), //
            null));
      all.add(A(C25.class));
      all.add(A(C26.class));

      return all;
   }

   @Override
   protected Class<?> getTestClass() {
      return MyPlexusContainerTest.class;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new MyPlexusContainerTestConfigurator());
   }
}
