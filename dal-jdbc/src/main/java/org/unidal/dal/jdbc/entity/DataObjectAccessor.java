package org.unidal.dal.jdbc.entity;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;

public interface DataObjectAccessor {
   public <T extends DataObject> Object getFieldValue(T dataObject, DataField dataField);

   public <T extends DataObject> T newInstance(Class<T> clazz);

   public <T extends DataObject> void setFieldValue(T row, DataField field, Object value);
}
