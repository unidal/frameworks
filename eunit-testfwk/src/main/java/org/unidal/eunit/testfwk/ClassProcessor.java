package org.unidal.eunit.testfwk;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.eunit.testfwk.spi.IAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassProcessor;
import org.unidal.eunit.testfwk.spi.IDeferredAnnotationHandler;
import org.unidal.eunit.testfwk.spi.IDeferredAnnotationHandler.IDeferredHandler;
import org.unidal.eunit.testfwk.spi.IMetaAnnotationHandler;
import org.unidal.eunit.testfwk.spi.Parameter;
import org.unidal.eunit.testfwk.spi.Registry;
import org.unidal.eunit.testfwk.spi.event.Event;
import org.unidal.eunit.testfwk.spi.event.EventType;
import org.unidal.eunit.testfwk.spi.event.IEventListener;

public class ClassProcessor implements IClassProcessor {
   private List<IDeferredHandler> m_deferredHandlers = new ArrayList<IDeferredHandler>();

   protected void fireEvent(IClassContext ctx, EventType type, AnnotatedElement source) {
      Registry registry = ctx.getRegistry();
      Event event = new Event(type, source);

      for (IEventListener listener : registry.getListeners()) {
         listener.onEvent(ctx, event);
      }
   }

   @Override
   public void process(IClassContext ctx) {
      Registry registry = ctx.getRegistry();
      List<IAnnotationHandler<?, ?>> typeHandlers = registry.getAnnotationHandlers(ElementType.TYPE);
      List<IMetaAnnotationHandler<?, ?>> metaHandlers = registry.getMetaAnnotationHandlers(ElementType.ANNOTATION_TYPE);
      Class<?> clazz = ctx.getTestClass();

      fireEvent(ctx, EventType.BEFORE_CLASS, clazz);
      processAnnotations(ctx, typeHandlers, metaHandlers, clazz, false);
      processFields(ctx, metaHandlers, Scanner.INSTANCE.getFields(clazz));
      processMethods(ctx, metaHandlers, Scanner.INSTANCE.getMethods(clazz));
      processDeferredHandlers();
      processAnnotations(ctx, typeHandlers, metaHandlers, clazz, true);
      fireEvent(ctx, EventType.AFTER_CLASS, clazz);
   }

   @SuppressWarnings( { "unchecked" })
   protected void processAnnotations(IClassContext ctx, List<IAnnotationHandler<?, ?>> handlers,
         List<IMetaAnnotationHandler<?, ?>> metaHandlers, AnnotatedElement annotated, boolean after) {
      if (!after) {
         fireEvent(ctx, EventType.BEFORE_ANNOTATIONS, annotated);
      }

      Annotation[] annotations = annotated.getAnnotations();
      Set<Annotation> done = new HashSet<Annotation>();

      if (annotations.length > 0) {
         // Annotation Handlers
         for (IAnnotationHandler<?, ?> handler : handlers) {
            if (handler.isAfter() == after) {
               boolean deferred = (handler instanceof IDeferredAnnotationHandler);
               Class<?> targetAnnotation = handler.getTargetAnnotation();

               for (Annotation annotation : annotations) {
                  if (annotation.annotationType() == targetAnnotation) {
                     if (deferred) {
                        @SuppressWarnings("rawtypes")
                        IDeferredHandler h = ((IDeferredAnnotationHandler) handler).createDeferredHandler(ctx,
                              annotation, annotated);

                        m_deferredHandlers.add(h);
                     } else {
                        ((IAnnotationHandler<Annotation, AnnotatedElement>) handler).handle(ctx, annotation, annotated);
                     }

                     // only first handler get called
                     done.add(annotation);
                     break;
                  }
               }
            }
         }
      }

      if (annotations.length > done.size()) {
         // Meta Annotation Handlers
         for (IMetaAnnotationHandler<?, ?> metaHandler : metaHandlers) {
            Class<? extends Annotation> targetAnnotation = metaHandler.getTargetAnnotation();

            for (Annotation annotation : annotations) {
               if (!done.contains(annotation)) {
                  Annotation meta = annotation.annotationType().getAnnotation(targetAnnotation);

                  if (meta != null) {
                     IMetaAnnotationHandler<Annotation, AnnotatedElement> handler = (IMetaAnnotationHandler<Annotation, AnnotatedElement>) metaHandler;

                     handler.handle(ctx, annotation, meta, annotated, after);
                  }
               }
            }
         }
      }

      if (after) {
         fireEvent(ctx, EventType.AFTER_ANNOTATIONS, annotated);
      }
   }

   protected void processDeferredHandlers() {
      for (IDeferredHandler deferredHandler : m_deferredHandlers) {
         deferredHandler.execute();
      }
   }

   protected void processFields(IClassContext ctx, List<IMetaAnnotationHandler<?, ?>> metaHandlers,
         Collection<Field> fields) {
      Registry registry = ctx.getRegistry();
      List<IAnnotationHandler<?, ?>> fieldHandlers = registry.getAnnotationHandlers(ElementType.FIELD);

      for (Field field : fields) {
         fireEvent(ctx, EventType.BEFORE_FIELD, field);
         processAnnotations(ctx, fieldHandlers, metaHandlers, field, false);
         processAnnotations(ctx, fieldHandlers, metaHandlers, field, true);
         fireEvent(ctx, EventType.AFTER_FIELD, field);
      }
   }

   protected void processMethods(IClassContext ctx, List<IMetaAnnotationHandler<?, ?>> metaHandlers,
         Collection<Method> methods) {
      Registry registry = ctx.getRegistry();
      List<IAnnotationHandler<?, ?>> methodHandlers = registry.getAnnotationHandlers(ElementType.METHOD);
      List<IAnnotationHandler<?, ?>> paramHandlers = registry.getAnnotationHandlers(ElementType.PARAMETER);

      for (Method method : methods) {
         fireEvent(ctx, EventType.BEFORE_METHOD, method);
         processAnnotations(ctx, methodHandlers, metaHandlers, method, false);

         Class<?>[] types = method.getParameterTypes();
         Annotation[][] annotations = method.getParameterAnnotations();
         int len = types.length;

         for (int i = 0; i < len; i++) {
            Parameter parameter = new Parameter(method, i, types[i], annotations[i]);

            fireEvent(ctx, EventType.BEFORE_PARAMETER, parameter);
            processAnnotations(ctx, paramHandlers, metaHandlers, parameter, false);
            processAnnotations(ctx, paramHandlers, metaHandlers, parameter, true);
            fireEvent(ctx, EventType.AFTER_PARAMETER, parameter);
         }

         processAnnotations(ctx, methodHandlers, metaHandlers, method, true);
         fireEvent(ctx, EventType.AFTER_METHOD, method);
      }
   }

   public static interface IScanner {
      public Collection<Field> getFields(Class<?> clazz);

      public Collection<Method> getMethods(Class<?> clazz);
   }

   enum Scanner implements IScanner {
      INSTANCE;

      protected void collectFields(Map<String, Field> result, Class<?> clazz) {
         Field[] fields = clazz.getDeclaredFields();

         for (Field field : fields) {
            String name = field.getName();

            if (!result.containsKey(name)) {
               result.put(name, field);
            }
         }

         Class<?> parent = clazz.getSuperclass();

         if (parent != null && parent != Object.class) {
            collectFields(result, parent);
         }
      }

      protected void collectMethods(Map<String, Method> result, Class<?> clazz) {
         Method[] methods = clazz.getDeclaredMethods();

         for (Method method : methods) {
            String name = method.getName();

            if (!result.containsKey(name)) {
               result.put(name, method);
            }
         }

         Class<?> parent = clazz.getSuperclass();

         if (parent != null && parent != Object.class) {
            collectMethods(result, parent);
         }
      }

      @Override
      public Collection<Field> getFields(Class<?> clazz) {
         Map<String, Field> fields = new LinkedHashMap<String, Field>();

         collectFields(fields, clazz);

         return fields.values();
      }

      @Override
      public Collection<Method> getMethods(Class<?> clazz) {
         Map<String, Method> methods = new LinkedHashMap<String, Method>();

         collectMethods(methods, clazz);

         return methods.values();
      }
   }
}
