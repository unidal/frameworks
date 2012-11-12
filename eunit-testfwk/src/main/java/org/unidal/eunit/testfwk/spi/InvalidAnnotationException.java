package org.unidal.eunit.testfwk.spi;


/**
 * Annotation Syntax Error
 *
 * @author XShao
 * @version 1.00 4/7/11 2:11 AM
 */
public class InvalidAnnotationException extends RuntimeException {

/**
    * 
    */
   private static final long serialVersionUID = -1366421535131197001L;

//   /**
//    * 
//    */
//   private static final long serialVersionUID = -5563015384988272304L;
//
//   private Annotation m_annotation;
//
//   private AnnotatedElement m_target;

   public InvalidAnnotationException(String message) {
      super(message);
//
//      m_target = target;
//      m_annotation = annotation;
   }
   
//   private static String shortName(Annotation annotation) {
//      if (annotation != null) {
//         return "@" + shortName(annotation.annotationType().getName());
//      }
//      else {
//         return "";
//      }
//   }
//   
//   private static String shortName(String className)
//   {
//     int index = className.lastIndexOf('.');
//     if (index > 0) {
//        return className.substring(index +1);
//     }
//     return className;
//   }
//   
//   public AnnotationSyntaxException(AnnotatedElement target, Annotation annotation, String format, Object ...arguments) {
//      this(target, annotation, target + "() --> " + shortName(annotation) + String.format(format, arguments));
//   }
//   
//   public AnnotationSyntaxException(AnnotatedElement target, String format, Object ...arguments) {
//      this(target, null, format, arguments);
//   }
//
//   public Annotation getAnnotation() {
//      return m_annotation;
//   }
//
//   public AnnotatedElement getTarget() {
//      return m_target;
//   }
}
