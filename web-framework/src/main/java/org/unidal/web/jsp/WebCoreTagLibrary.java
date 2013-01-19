package org.unidal.web.jsp;

import java.io.File;

import org.unidal.web.jsp.annotation.TaglibMeta;
import org.unidal.web.jsp.function.CalculatorFunction;
import org.unidal.web.jsp.function.CodecFunction;
import org.unidal.web.jsp.function.FormFunction;
import org.unidal.web.jsp.function.FormatFunction;
import org.unidal.web.jsp.function.MappingFunction;
import org.unidal.web.jsp.function.ObjectFunction;
import org.unidal.web.jsp.tag.ErrorTag;
import org.unidal.web.jsp.tag.ErrorsTag;

@TaglibMeta(uri = "http://www.unidal.org/web/core", shortName = "w", name = "web-core", description = "web-core JSP tag library", //
funcitons = { CalculatorFunction.class, CodecFunction.class, FormFunction.class, FormatFunction.class, MappingFunction.class,
      ObjectFunction.class }, //
tags = { ErrorsTag.class, ErrorTag.class })
public class WebCoreTagLibrary extends AbstractTagLibrary {
   public static void main(String[] args) {
      new WebCoreTagLibrary().generateTldFile(new File("."));
   }
}
