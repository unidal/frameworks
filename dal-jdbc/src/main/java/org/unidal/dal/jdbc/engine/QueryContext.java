package org.unidal.dal.jdbc.engine;

import java.util.List;
import java.util.Map;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.query.Parameter;

public interface QueryContext {
   public void addOutField(DataField field);

   public void addOutSubObjectName(String subObjectName);

   public void addParameter(Parameter value);

   public String getDataSourceName();

   public EntityInfo getEntityInfo();

   public int getFetchSize();

   public List<DataField> getOutFields();

   public List<String> getOutSubObjectNames();

   public List<Parameter> getParameters();

   public Object[] getParameterValues();

   public DataObject getProto();

   public QueryDef getQuery();

   public Map<String, Object> getQueryHints();

   public Readset<?> getReadset();

   public String getSqlStatement();

   public Updateset<?> getUpdateset();

   public boolean isSqlResolveDisabled();

   public boolean isTableResolved();

   public boolean isWithinIfToken();

   public boolean isWithinInToken();

   public void setDataSourceName(String dataSourceName);

   public void setEntityInfo(EntityInfo entityInfo);

   public void setFetchSize(int fetchSize);

   public void setParameterValues(Object[] values);

   public void setProto(DataObject proto);

   public void setQuery(QueryDef query);

   public void setQueryHints(Map<String, Object> queryHints);

   public void setReadset(Readset<?> readset);

   public void setSqlResolveDisabled(boolean sqlResolveDisabled);

   public void setSqlStatement(String sqlStatement);

   public void setTableResolved(boolean tableResolved);

   public void setUpdateset(Updateset<?> updateset);

   public void setWithinIfToken(boolean withinIfToken);

   public void setWithinInToken(boolean withinInToken);
}
