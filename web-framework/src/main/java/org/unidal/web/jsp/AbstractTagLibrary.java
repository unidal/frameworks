package org.unidal.web.jsp;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.unidal.helper.Files;
import org.unidal.lookup.configuration.XmlPlexusConfigurationWriter;
import org.unidal.web.jsp.annotation.AttributeMeta;
import org.unidal.web.jsp.annotation.FunctionMeta;
import org.unidal.web.jsp.annotation.TagMeta;
import org.unidal.web.jsp.annotation.TaglibMeta;

public abstract class AbstractTagLibrary {
	protected void buildFunctions(XmlPlexusConfiguration taglib, Class<?> clazz) {
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			FunctionMeta meta = method.getAnnotation(FunctionMeta.class);

			if (meta != null) {
				int modifier = method.getModifiers();

				if (Modifier.isStatic(modifier)) {
					XmlPlexusConfiguration function = new XmlPlexusConfiguration("function");

					taglib.addChild(function);
					function.addChild("description", meta.description());
					function.addChild("name", method.getName());
					function.addChild("function-class", clazz.getName());
					function.addChild("function-signature", buildSignature(method));
					function.addChild("example", meta.example());
				} else {
					System.out.println(String.format("JSP function(%s) is not static method: %s, IGNORED!",
					      method.getName(), method));
				}
			}
		}
	}

	protected String buildSignature(Method method) {
		StringBuilder sb = new StringBuilder(256);
		boolean first = true;

		sb.append(method.getReturnType().getName()).append(' ');
		sb.append(method.getName()).append('(');

		for (Class<?> parameterType : method.getParameterTypes()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}

			sb.append(parameterType.getName());
		}

		sb.append(')');

		return sb.toString();
	}

	protected void buildTag(XmlPlexusConfiguration taglib, Class<?> clazz) {
		TagMeta meta = clazz.getAnnotation(TagMeta.class);

		if (meta != null) {
			XmlPlexusConfiguration tag = new XmlPlexusConfiguration("tag");

			taglib.addChild(tag);
			tag.addChild("name", meta.name());
			tag.addChild("description", meta.description());
			tag.addChild("tag-class", clazz.getName());
			tag.addChild("body-content", meta.bodyContent());
			tag.addChild("dynamic-attributes", String.valueOf(meta.dynamicAttributes()));

			buildTagAttributes(tag, clazz);
		} else {
			throw new RuntimeException(String.format("Class(%s) should be annotated by %s!", clazz.getName(),
			      TagMeta.class));
		}
	}

	protected void buildTagAttributes(XmlPlexusConfiguration tag, Class<?> clazz) {
		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			AttributeMeta meta = method.getAnnotation(AttributeMeta.class);

			if (meta != null) {
				int modifier = method.getModifiers();
				String name = method.getName();

				if (!Modifier.isStatic(modifier)) {
					if (name.startsWith("set") && method.getParameterTypes().length == 1) {
						XmlPlexusConfiguration attribute = new XmlPlexusConfiguration("attribute");

						tag.addChild(attribute);

						if (meta.name().length() > 0) {
							attribute.addChild("name", meta.name());
						} else if (name.length() > 3) {
							attribute.addChild("name", Character.toLowerCase(name.charAt(3)) + name.substring(4));
						}

						attribute.addChild("description", meta.description());
						attribute.addChild("required", String.valueOf(meta.required()));
						attribute.addChild("rtexprvalue", String.valueOf(meta.rtexprvalue()));
						attribute.addChild("type", method.getParameterTypes()[0].toString());
					} else {
						System.out.println(String.format("The method(%s) for tag attribute(%s) is not a setter. IGNORED!",
						      name, meta.name()));
					}
				} else {
					System.out.println(String.format("The method(%s) for tag attribute(%s) should not be static. IGNORED!",
					      name, meta.name()));
				}
			}
		}
	}

	protected XmlPlexusConfiguration buildTaglib(TaglibMeta meta) {
		XmlPlexusConfiguration taglib = new XmlPlexusConfiguration("taglib");

		taglib.setAttribute("xmlns", "http://java.sun.com/xml/ns/javaee");
		taglib.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		taglib.setAttribute("xsi:schemaLocation",
		      "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd");
		taglib.setAttribute("version", "2.1");

		taglib.addChild("description", meta.description());
		taglib.addChild("display-name", meta.name());
		taglib.addChild("tlib-version", "1.2");
		taglib.addChild("short-name", meta.shortName());
		taglib.addChild("uri", meta.uri());

		for (Class<?> tag : meta.tags()) {
			buildTag(taglib, tag);
		}

		for (Class<?> function : meta.funcitons()) {
			buildFunctions(taglib, function);
		}

		return taglib;
	}

	protected void generateTldFile(File baseDir) {
		TaglibMeta meta = getClass().getAnnotation(TaglibMeta.class);

		if (meta == null) {
			throw new RuntimeException(String.format("Class(%s) should be annotated by %s!", getClass().getName(),
			      TaglibMeta.class.getName()));
		}

		File taglibFile = new File(baseDir, "src/main/resources/META-INF/" + meta.name() + ".tld");
		XmlPlexusConfiguration taglib = buildTaglib(meta);

		try {
			saveToFile(taglibFile, taglib);

			System.out.println(String.format("File %s generated. File length is %s.", taglibFile.getCanonicalPath(),
			      taglibFile.length()));
		} catch (IOException e) {
			System.err.println(String.format("Error when generating %s file.", taglibFile));
			e.printStackTrace();
		}
	}

	protected void saveToFile(File file, XmlPlexusConfiguration taglib) throws IOException {
		// create parent directory if not there
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		StringWriter sw = new StringWriter();
		XmlPlexusConfigurationWriter xw = new XmlPlexusConfigurationWriter();

		sw.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n\r\n");

		try {
			xw.write(sw, taglib);
		} catch (IOException e) {
			// will not happen with StringWriter
		}

		Files.forIO().writeTo(file, sw.toString());
	}
}
