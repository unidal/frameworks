package org.unidal.web.jsp.function;

import java.util.List;

import org.unidal.lookup.util.ReflectUtils;
import org.unidal.web.jsp.annotation.FunctionMeta;

public class FormFunction {
   private static Object getPropertyValue(Object instance, String property) {
      if (instance == null) {
         return null;
      } else if (property == null || property.length() == 0) {
         return instance.toString();
      } else {
         return ReflectUtils.invokeGetter(instance, property);
      }
   }

   private static String getSelectedOrChecked(Object selected, Object value, boolean selectedOrChecked) {
      if (value != null && selected != null) {
         if (selected instanceof Object[]) {
            for (Object e : (Object[]) selected) {
               if (value.toString().equals(e.toString())) {
                  return selectedOrChecked ? " selected" : " checked";
               }
            }
         } else if (selected instanceof List) {
            for (Object e : (List<?>) selected) {
               if (value.toString().equals(e.toString())) {
                  return selectedOrChecked ? " selected" : " checked";
               }
            }
         } else if (value.toString().equals(selected.toString())) {
            return selectedOrChecked ? " selected" : " checked";
         }
      }

      return "";
   }

   @FunctionMeta(description = "Show checkbox from an object's properties in a form", example = "${w:showCheckbox('groupBy', groupBy, payload.groupBy, 'name', 'description')}")
   public static String showCheckbox(String inputName, Object item, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder(256);
      Object value = getPropertyValue(item, valueName);
      Object text = getPropertyValue(item, textName);
      String id = inputName + '-' + value;

      sb.append("<input type=\"checkbox\" name=\"").append(inputName).append("\" value=\"").append(value).append("\" id=\"")
            .append(id).append("\"");
      sb.append(getSelectedOrChecked(selected, value, false));
      sb.append("><label for=\"").append(id).append("\">").append(text).append("</label>");

      return sb.toString();
   }

   @FunctionMeta(description = "Show multiple checkboxes from a list or array object's properties in a form", example = "${w:showCheckboxes('groupBy', groupBys, payload.groupBy, 'name', 'description')}")
   public static String showCheckboxes(String inputName, Object items, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder();

      if (items instanceof Object[]) {
         for (Object item : (Object[]) items) {
            sb.append(showCheckbox(inputName, item, selected, valueName, textName));
            sb.append("\r\n");
         }
      } else if (items instanceof List) {
         for (Object item : (List<?>) items) {
            sb.append(showCheckbox(inputName, item, selected, valueName, textName));
            sb.append("\r\n");
         }
      } else if (items != null) {
         throw new RuntimeException("Object[] or List expected, but was: " + items.getClass());
      }

      return sb.toString();
   }

   @FunctionMeta(description = "Show select option from an object's properties in a form", example = "${w:showOption(groupBy, payload.groupBy, 'name', 'description')}")
   public static String showOption(Object item, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder(256);
      Object value = getPropertyValue(item, valueName);
      Object text = getPropertyValue(item, textName);

      sb.append("<option value=\"").append(value).append("\"");
      sb.append(getSelectedOrChecked(selected, value, true));
      sb.append(">").append(text).append("</option>");

      return sb.toString();
   }

   @FunctionMeta(description = "Show multiple select options from a list or array object's properties in a form", example = "${w:showOptions(groupBys, payload.groupBy, 'name', 'description')}")
   public static String showOptions(Object items, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder();

      if (items instanceof Object[]) {
         for (Object item : (Object[]) items) {
            sb.append(showOption(item, selected, valueName, textName));
            sb.append("\r\n");
         }
      } else if (items instanceof List) {
         for (Object item : (List<?>) items) {
            sb.append(showOption(item, selected, valueName, textName));
            sb.append("\r\n");
         }
      } else if (items != null) {
         throw new RuntimeException("Object[] or List expected, but was: " + items.getClass());
      }

      return sb.toString();
   }

   @FunctionMeta(description = "Show radio from an object's properties in a form", example = "${w:showRadio('groupBy', groupBy, payload.groupBy, 'name', 'description')}")
   public static String showRadio(String inputName, Object item, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder(256);
      Object value = getPropertyValue(item, valueName);
      Object text = getPropertyValue(item, textName);
      String id = inputName + '-' + value;

      sb.append("<input type=\"radio\" name=\"").append(inputName).append("\" value=\"").append(value).append("\" id=\"")
            .append(id).append("\"");
      sb.append(getSelectedOrChecked(selected, value, false));
      sb.append("><label for=\"").append(id).append("\">").append(text).append("</label>");

      return sb.toString();
   }

   @FunctionMeta(description = "Show multiple radios from a list or array object's properties in a form", example = "${w:showRadios('groupBy', groupBys, payload.groupBy, 'name', 'description')}")
   public static String showRadios(String inputName, Object items, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder();

      if (items instanceof Object[]) {
         for (Object item : (Object[]) items) {
            sb.append(showRadio(inputName, item, selected, valueName, textName));
            sb.append("\r\n");
         }
      } else if (items instanceof List) {
         for (Object item : (List<?>) items) {
            sb.append(showRadio(inputName, item, selected, valueName, textName));
            sb.append("\r\n");
         }
      } else if (items != null) {
         throw new RuntimeException("Object[] or List expected, but was: " + items.getClass());
      }

      return sb.toString();
   }

   @FunctionMeta(description = "Show selected result of checkbox, radio or option from an object's properties in a form", example = "${w:showResult(groupBys, payload.groupBy, 'name', 'description')}")
   public static Object showResult(Object items, Object selected, String valueName, String textName) {
      StringBuilder sb = new StringBuilder();

      if (selected != null) {
         if (items instanceof Object[]) {
            for (Object item : (Object[]) items) {
               Object value = getPropertyValue(item, valueName);

               if (value != null && value.toString().equals(selected.toString())) {
                  Object text = getPropertyValue(item, textName);

                  return text;
               }
            }
         } else if (items instanceof List) {
            for (Object item : (List<?>) items) {
               Object value = getPropertyValue(item, valueName);

               if (value != null && value.toString().equals(selected.toString())) {
                  Object text = getPropertyValue(item, textName);

                  return text;
               }
            }
         } else if (items != null) {
            throw new RuntimeException("Object[] or List expected, but was: " + items.getClass());
         }
      }

      return sb.toString();
   }
}
