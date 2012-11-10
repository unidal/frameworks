package org.unidal.web.mvc;

public interface Validator<T extends ActionContext<?>> {
   public void validate(T context) throws Exception;
}
