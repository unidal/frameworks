package org.unidal.web.jsp.tag;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.unidal.web.jsp.annotation.AttributeMeta;
import org.unidal.web.jsp.annotation.TagMeta;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ErrorObject;

/**
 * w:error tag for JSP.
 * <p>
 * 
 * It can be used independently, or be nested into the w:errors tag.
 * 
 * Sample usage:<br>
 * 
 * <pre>
 * &lt;%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%&gt;
 * 
 * &lt;w:errors&gt; 
 *    &lt;w:error code="dal.user.add"&gt;Error while inserting user(\${userId}) to database. Exception: \${exception}&lt;/w:error&gt; 
 *    &lt;w:error code="biz.user.add"&gt;Error while adding user(\${userId}).&lt;/w:error&gt; 
 *    &lt;w:error code="*" enabled="true"&gt;Error(\${code}) occurred.&lt;br&gt;&lt;/w:error&gt; 
 * &lt;/w:errors&gt;
 * 
 * &lt;w:errors bundle="/META-INF/error_en_US.properties"&gt; 
 *    &lt;w:error code="*" enabled="true"/&gt; 
 * &lt;/w:errors&gt; 
 * </pre>
 * 
 * @see ErrorsTag
 */
@TagMeta(name = "error", description = "Error tag of MVC framework.")
public class ErrorTag extends AbstractBodyTag {
   private static final long serialVersionUID = 1L;

   private String m_code;

   private boolean m_enabled = true;

   private ErrorObject findError(String code) {
      List<ErrorObject> errors = getErrors();

      for (ErrorObject error : errors) {
         if (code.equals(error.getCode())) {
            return error;
         }
      }

      return null;
   }

   private String findMessagePattern(String code, ErrorsTag parent) {
      if (bodyContent != null) {
         String body = bodyContent.getString();

         if (body.length() > 0) {
            return body;
         }
      }

      if (parent != null) {
         return parent.getMessagePattern(code);
      }

      return null;
   }

   private List<ErrorObject> getErrors() {
      Object ctx = pageContext.findAttribute("ctx");

      if (ctx instanceof ActionContext<?>) {
         return ((ActionContext<?>) ctx).getErrors();
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   protected void handleBody() throws JspException {
      if (m_enabled) {
         ErrorsTag parent = (ErrorsTag) super.findAncestorWithClass(this, ErrorsTag.class);
         ErrorObject error = findError(m_code);
         String body = findMessagePattern(m_code, parent);

         if (body != null && error != null) {
            String result = processBody(body, error);

            out(result);

            if (parent != null) {
               parent.getProcessedErrors().add(error.getCode()); // register itself to parent for '*' case
            }
         } else if ("*".equals(m_code)) { // all others
            Set<String> processedErrors = parent == null ? Collections.<String> emptySet() : parent.getProcessedErrors();
            boolean first = true;

            for (ErrorObject e : getErrors()) {
               String pattern = findMessagePattern(e.getCode(), parent);

               if (!processedErrors.contains(e.getCode())) {
                  String result = processBody(pattern == null ? "${code}" : pattern, e);

                  if (first) {
                     first = false;
                  } else {
                     out(",");
                  }

                  out(result);
               }
            }
         }
      }
   }

   private void out(String str) throws JspException {
      try {
         write(str);
      } catch (Exception e) {
         throw new JspException("Error when writing out!", e);
      }
   }

   String processBody(String body, ErrorObject error) {
      StringBuilder sb = new StringBuilder(2048);
      int len = body.length();
      boolean dollar = false;
      int bracketStart = -1;

      for (int i = 0; i < len; i++) {
         char ch = body.charAt(i);

         switch (ch) {
         case '$':
            if (dollar) {
               sb.append(ch);
            }

            dollar = true;
            break;
         case '{':
            if (dollar) {
               bracketStart = i;
               dollar = false;
            } else {
               sb.append(ch);
            }
            break;
         case '}':
            if (bracketStart >= 0) {
               String name = body.substring(bracketStart + 1, i);
               String value = processPlaceholder(name.trim(), error);

               if (value != null) {
                  sb.append(value);
               }
            }

            bracketStart = -1;
            break;
         case '\\':
            if (i + 1 < len) {
               char ch2 = body.charAt(i + 1);

               if (ch2 == '\\' || ch2 == '$') {
                  sb.append(ch2);
                  i++;
                  break;
               }
            }

            // break through
         default:
            if (bracketStart < 0) {
               if (dollar) {
                  sb.append('$');
               }

               sb.append(ch);
               dollar = false;
            }

            break;
         }
      }

      return sb.toString();
   }

   String processPlaceholder(String name, ErrorObject error) {
      if (name.equals("exception")) {
         Exception exception = error.getException();

         if (exception != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            exception.printStackTrace(pw);

            return sw.toString();
         }
      } else if (name.equals("exception.message")) {
         Exception exception = error.getException();

         if (exception != null) {
            return exception.getMessage();
         }
      } else if (name.equals("code")) {
         return error.getCode();
      } else {
         Object value = error.getArgument(name);

         if (value != null) {
            return value.toString();
         }
      }

      return null;
   }

   @AttributeMeta(required = true)
   public void setCode(String code) {
      m_code = code;
   }

   @AttributeMeta
   public void setEnabled(boolean enabled) {
      m_enabled = enabled;
   }
}
