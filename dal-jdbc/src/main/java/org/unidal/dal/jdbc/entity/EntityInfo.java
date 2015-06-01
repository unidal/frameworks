package org.unidal.dal.jdbc.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.annotation.Entity;
import org.unidal.dal.jdbc.annotation.Relation;
import org.unidal.dal.jdbc.annotation.SubObjects;
import org.unidal.dal.jdbc.annotation.Variable;

public class EntityInfo {
   private Entity m_entity;

   private Map<DataField, Relation> m_relations;

   private Map<DataField, Attribute> m_attributes;

   private Map<DataField, Variable> m_variables;

   private Map<Readset<?>, SubObjects> m_subobjects;

   private DataField m_autoIncrementField;

   public EntityInfo(Entity entity, Map<DataField, Relation> relations, Map<DataField, Attribute> attributes,
         Map<DataField, Variable> variables, Map<Readset<?>, SubObjects> subobjects) {
      m_entity = entity;
      m_relations = relations;
      m_attributes = attributes;
      m_variables = variables;
      m_subobjects = subobjects;

      for (Map.Entry<DataField, Attribute> e : attributes.entrySet()) {
         Attribute attribute = e.getValue();

         if (attribute != null && attribute.autoIncrement()) {
            m_autoIncrementField = e.getKey();
            break;
         }
      }
   }

   public String getAlias() {
      return m_entity.alias();
   }

   public Attribute getAttribute(DataField dataField) {
      return m_attributes.get(dataField);
   }

   public Attribute getAttribute(String fieldName) {
      DataField dataField = null;

      for (DataField e : m_attributes.keySet()) {
         if (e.getName().equals(fieldName)) {
            dataField = e;
         }
      }

      if (dataField != null) {
         return m_attributes.get(dataField);
      } else {
         return null;
      }
   }

   public List<DataField> getAttributeFields() {
      List<DataField> dataFields = new ArrayList<DataField>(m_attributes.size());

      dataFields.addAll(m_attributes.keySet());
      return dataFields;
   }

   public DataField getAutoIncrementField() {
      return m_autoIncrementField;
   }

   public DataField getFieldByName(String fieldName) {
      for (DataField e : m_variables.keySet()) {
         if (e.getName().equals(fieldName)) {
            return e;
         }
      }

      for (DataField e : m_attributes.keySet()) {
         if (e.getName().equals(fieldName)) {
            return e;
         }
      }

      throw new DalRuntimeException("No DataField with name(" + fieldName + ") defined");
   }

   public String getJoinClause(Readset<?> readset) {
      SubObjects subobject = m_subobjects.get(readset);

      if (subobject == null) {
         return "1=1";
      }

      StringBuilder sb = new StringBuilder(1024);
      String[] names = subobject.value();

      for (String name : names) {
         if (name != null && name.length() > 0) {
            for (Map.Entry<DataField, Relation> e : m_relations.entrySet()) {
               if (e.getKey().getName().equals(name)) {
                  if (sb.length() > 0) {
                     sb.append(" and ");
                  }

                  sb.append(e.getValue().join());
               }
            }
         }
      }

      return sb.toString();
   }

   public String getLogicalName() {
      return m_entity.logicalName();
   }

   public String[] getLogicalNameAndAlias(String name) {
      if (m_entity.logicalName().equals(name)) {
         return new String[] { name, m_entity.alias() };
      } else {
         // is it a Relation?
         for (Map.Entry<DataField, Relation> e : m_relations.entrySet()) {
            if (e.getKey().getName().equals(name)) {
               return new String[] { e.getValue().logicalName(), e.getValue().alias() };
            }
         }
      }

      throw new DalRuntimeException("Table(" + name + ") has no relationship with table(" + m_entity.logicalName()
            + ")");
   }

   public Relation getRelation(String logicalName) {
      for (Map.Entry<DataField, Relation> e : m_relations.entrySet()) {
         if (e.getKey().getName().equals(logicalName)) {
            return e.getValue();
         }
      }

      return null;
   }

   public SubObjects getSubobjects(Readset<?> readset) {
      return m_subobjects.get(readset);
   }

   public Variable getVariable(DataField dataField) {
      return m_variables.get(dataField);
   }

   public boolean isRelation(String logicalName) {
      for (DataField dataField : m_relations.keySet()) {
         if (dataField.getName().equals(logicalName)) {
            return true;
         }
      }

      return false;
   }
}
