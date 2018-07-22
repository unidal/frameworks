package org.unidal.web.jsp.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.unidal.web.jsp.annotation.AttributeMeta;
import org.unidal.web.jsp.annotation.TagMeta;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ErrorObject;

/**
 * w:errors tag for JSP. A properties file can be specified to provide the template for given error code.
 * <p>
 * 
 * e:error tag can be nested inside.
 * 
 * Sample usage:<br>
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
 * @see ErrorTag
 */
@TagMeta(name = "errors", description = "Errors tag of MVC framework.")
public class ErrorsTag extends AbstractBodyTag {
   private static final String DEFAULT_ERROR_PROPERTIES = "/META-INF/error.properties";

   private static final long serialVersionUID = 1L;

   // key bundle => value properties
   private static Map<String, Properties> s_cache = new HashMap<String, Properties>();

   private String m_bundle = DEFAULT_ERROR_PROPERTIES;

   // used to keep coordinate data of child error tags, so that we can have chance to handle unprocessed errors.
   private Set<String> m_processedErrors = new HashSet<String>();

   public String getMessagePattern(String code) {
      Properties properties = s_cache.get(m_bundle);

      if (properties != null) {
         return properties.getProperty(code);
      }

      return null;
   }

   public Set<String> getProcessedErrors() {
      return m_processedErrors;
   }

   @Override
   public int doStartTag() throws JspException {
      if (m_bundle != null) {
         if (!s_cache.containsKey(m_bundle)) {
            Properties properties = new Properties();
            InputStream in = getClass().getResourceAsStream(m_bundle);

            s_cache.put(m_bundle, properties);

            if (in != null) {
               try {
                  properties.load(in);
               } catch (IOException e) {
                  throw new RuntimeException(String.format("Error when loading resource bundle(%s)!", m_bundle), e);
               }
            } else if (!DEFAULT_ERROR_PROPERTIES.equals(m_bundle)) {
               throw new RuntimeException(String.format("No resource bundle(%s) is found!", m_bundle));
            }
         }
      }

      return super.doStartTag();
   }

   @Override
   protected void handleBody() throws JspException {
      boolean hasError = hasErrors();

      if (hasError) {
         String body = bodyContent.getString();

         try {
            write(body);
         } catch (Exception e) {
            throw new JspException(String.format("Error when flushing body(%s)!", body), e);
         }
      }
   }

   private boolean hasErrors() {
      Object ctx = pageContext.findAttribute("ctx");

      if (ctx instanceof ActionContext<?>) {
         List<ErrorObject> errors = ((ActionContext<?>) ctx).getErrors();

         return !errors.isEmpty();
      }

      return false;
   }

   @AttributeMeta
   public void setBundle(String bundle) {
      m_bundle = bundle;
   }
}
