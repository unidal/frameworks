package org.unidal.web.jsp;

import java.io.File;

import org.unidal.web.jsp.annotation.TaglibMeta;
import org.unidal.web.jsp.function.CalculatorFunction;
import org.unidal.web.jsp.function.CodecFunction;
import org.unidal.web.jsp.function.FormFunction;
import org.unidal.web.jsp.function.FormatterFunction;
import org.unidal.web.jsp.function.ObjectFunction;

@TaglibMeta(uri = "http://www.unidal.org/web/core", shortName = "w", name = "web-core", description = "web-core JSP tag library", //
funcitons = { CalculatorFunction.class, CodecFunction.class, FormFunction.class, FormatterFunction.class,
      ObjectFunction.class })
public class WebCoreTagLibrary extends AbstractTagLibrary {
	public static void main(String[] args) {
		new WebCoreTagLibrary().generateTldFile(new File("."));
	}
}
