package org.unidal.web.jsp.function;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.unidal.helper.Reflects;
import org.unidal.web.jsp.annotation.FunctionMeta;

public class ObjectFunction {
	@FunctionMeta(description = "Length of String, or array", example = "${w:length(obj)}")
	public static Object length(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length();
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		} else {
			return Reflects.forMethod().invokeMethod(obj, "getLength", (Object[]) null);
		}
	}

	@FunctionMeta(description = "size of colection, or map", example = "${w:size(obj)}")
	public static Object size(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		} else {
			return Reflects.forMethod().invokeMethod(obj, "getSize", (Object[]) null);
		}
	}
}
