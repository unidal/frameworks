package org.unidal.web.jsp.function;

import java.util.List;

import org.unidal.helper.Splitters;
import org.unidal.web.jsp.annotation.FunctionMeta;

public class MappingFunction {
   @FunctionMeta(description = "Translate code based on a mapping table", example = "${w:translate('2', '1|2|3', 'one|two|three', 'not found')}")
   public static String translate(String code, String codes, String values, String defaultValue) {
      List<String> codeList = Splitters.by('|').trim().split(codes);
      List<String> valueList = Splitters.by('|').trim().split(values);

      if (codeList != null && valueList != null) {
         int index = 0;

         for (String codeItem : codeList) {
            if (codeItem.equals(code)) {
               if (index < valueList.size()) {
                  return valueList.get(index);
               } else {
                  break;
               }
            }

            index++;
         }
      }

      return defaultValue;
   }
}
