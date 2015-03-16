package org.unidal.web.mvc.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.annotation.Named;

@Named(instantiationStrategy = Named.PER_LOOKUP)
public class AnnotationMatrix {
   private Map<Class<? extends Annotation>, Map<Class<? extends Annotation>, Integer>> m_map = new HashMap<Class<? extends Annotation>, Map<Class<? extends Annotation>, Integer>>();

   public void addToMatrix(Class<? extends Annotation> source, Class<? extends Annotation> target,
         int sourceCardinality, int targetCardinality) {
      addToMatrix(source, target, targetCardinality);
      addToMatrix(target, source, sourceCardinality);
   }

   public void addToMatrix(Class<? extends Annotation> source, Class<? extends Annotation> target, int cardinality) {
      Map<Class<? extends Annotation>, Integer> map = m_map.get(source);

      if (map == null) {
         map = new HashMap<Class<? extends Annotation>, Integer>();
         m_map.put(source, map);
      }

      map.put(target, cardinality);
   }

   public void checkMatrix(Method method, Class<? extends Annotation>[] classes) {
      int len = classes.length;

      for (int i = 0; i < len; i++) {
         for (int j = i + 1; j < len; j++) {
            Annotation sourceAnnotation = method.getAnnotation(classes[i]);
            Annotation targetAnnotation = method.getAnnotation(classes[j]);

            checkMatrix(method, classes[i], classes[j], sourceAnnotation, method.getAnnotation(classes[j]));
            checkMatrix(method, classes[j], classes[i], targetAnnotation, method.getAnnotation(classes[i]));
         }
      }
   }

   private void checkMatrix(Method method, Class<? extends Annotation> source, Class<? extends Annotation> target,
         Annotation sourceAnnotation, Annotation targetAnnotation) {
      Map<Class<? extends Annotation>, Integer> map = m_map.get(source);
      Integer cardinality = map == null ? null : map.get(target);

      if (sourceAnnotation == null) {
         return;
      }

      if (cardinality == null || cardinality == 0) { // may
         // it's okay
      } else if (cardinality == -1) { // can't
         if (targetAnnotation != null) {
            throw new RuntimeException("Annotations(" + target.getName() + ") can't be used together with annotation("
                  + source.getName() + ") to " + method);
         }
      } else if (cardinality == 1) { // must
         if (targetAnnotation == null) {
            throw new RuntimeException("Annotation(" + target.getName() + ") must be used together with annotation("
                  + source.getName() + ") to " + method);
         }
      }
   }
}
