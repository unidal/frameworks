package org.unidal.web.jsp.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class AbstractBodyTag extends BodyTagSupport {
   private static final long serialVersionUID = 1L;
   
   // Indicate whether there is a body content found
   private boolean m_hasBody;

   @Override
   public int doAfterBody() throws JspException {
      m_hasBody = true;
      handleBody();

      return SKIP_BODY;
   }

   @Override
   public int doEndTag() throws JspException {
      // In case of no body actually provided, we should allow it.
      if (!m_hasBody) {
         handleBody();
      }

      m_hasBody = false;
      return EVAL_PAGE;
   }

   protected abstract void handleBody() throws JspException;

   protected void write(String data) throws IOException {
      final JspWriter out = pageContext.getOut();

      if (bodyContent != null && out instanceof BodyContent) {
         ((BodyContent) out).getEnclosingWriter().write(data);
      } else {
         out.write(data);
      }
   }
}