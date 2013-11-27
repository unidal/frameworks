package org.unidal.web.mvc.view.model;

import java.lang.reflect.Field;
import java.util.List;

public interface ModelDescriptor {
   public String getModelName();

   public List<Field> getAttributeFields();

   public List<Field> getElementFields();

   public List<Field> getEntityFields();
   
   public List<Field> getPojoFields();
}
