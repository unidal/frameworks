package org.unidal.eunit.testfwk;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitField;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.ClassContext.EunitContext;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassContext.IEunitContext;
import org.unidal.eunit.testfwk.spi.Parameter;
import org.unidal.eunit.testfwk.spi.event.Event;
import org.unidal.eunit.testfwk.spi.event.IEventListener;
import org.unidal.eunit.testfwk.spi.filter.IGroupFilter;
import org.unidal.eunit.testfwk.spi.filter.RunOption;

public enum EunitEventListener implements IEventListener {
   INSTANCE;

   @Override
   public void onEvent(IClassContext classContext, Event event) {
      AnnotatedElement source = event.getSource();
      EunitContext ctx = (EunitContext) classContext.forEunit();
      EunitClass eunitClass;
      EunitMethod eunitMethod;

      switch (event.getType()) {
      case BEFORE_CLASS:
         Class<?> type = (Class<?>) source;

         eunitClass = new EunitClass();
         eunitClass.setType(type);
         ctx.setEunitClass(eunitClass);

         for (Annotation annotation : type.getAnnotations()) {
            eunitClass.addAnnotation(annotation);
         }

         ctx.push(eunitClass);
         break;
      case BEFORE_METHOD:
         Method method = (Method) source;

         eunitClass = ctx.peek();
         eunitMethod = new EunitMethod(method.getName());
         eunitMethod.setMethod(method);
         eunitMethod.setEunitClass(eunitClass);
         eunitClass.addMethod(eunitMethod);

         for (Annotation annotation : method.getAnnotations()) {
            eunitMethod.addAnnotation(annotation);
         }

         ctx.push(eunitMethod);
         break;
      case BEFORE_FIELD:
         Field field = (Field) source;
         EunitField eunitField = new EunitField(field.getName());

         eunitClass = ctx.peek();
         eunitField.setType(field.getType());
         eunitField.setField(field);
         eunitField.setEunitClass(eunitClass);
         eunitClass.addField(eunitField);

         for (Annotation annotation : field.getAnnotations()) {
            eunitField.addAnnotation(annotation);
         }

         ctx.push(eunitField);
         break;
      case BEFORE_PARAMETER:
         Parameter parameter = (Parameter) source;
         EunitParameter eunitParameter = new EunitParameter();

         eunitMethod = ctx.peek();
         eunitParameter.setType(parameter.getArgumentType());
         eunitParameter.setEunitMethod(eunitMethod);
         eunitParameter.setIndex(eunitMethod.getParameters().size());
         eunitMethod.addParameter(eunitParameter);

         for (Annotation annotation : parameter.getAnnotations()) {
            eunitParameter.addAnnotation(annotation);
         }

         ctx.push(eunitParameter);
         break;
      case AFTER_PARAMETER:
         ctx.pop();
         break;
      case AFTER_FIELD:
         ctx.pop();
         break;
      case AFTER_METHOD:
         ctx.pop();
         break;
      case AFTER_CLASS:
         postClassProcess(ctx);

         ctx.pop();
         break;
      default:
         break;
      }
   }

   private void postClassProcess(IEunitContext ctx) {
      EunitClass eunitClass = ctx.getEunitClass();
      RunOption option = EunitRuntimeConfig.INSTANCE.getRunOption();
      IGroupFilter filter = EunitRuntimeConfig.INSTANCE.getGroupFilter();

      for (EunitMethod eunitMethod : eunitClass.getMethods()) {
         if (eunitMethod.isTest()) {
            if (filter == null || filter.matches(eunitMethod)) {
               switch (option) {
               case IGNORED_CASES_ONLY:
               case ALL_CASES:
                  if (eunitMethod.isIgnored()) {
                     eunitMethod.setIgnored(false);
                  } else if (option == RunOption.IGNORED_CASES_ONLY) {
                     eunitMethod.setIgnored(true);
                  }
                  break;
               default:
                  break;
               }
            } else {
               eunitMethod.setIgnored(true);
            }
         }
      }
   }
}
