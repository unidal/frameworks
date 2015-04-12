package org.unidal.dal.jdbc.raw;

import java.util.List;

import org.unidal.dal.jdbc.AbstractDao;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.QueryDef;
import org.unidal.dal.jdbc.QueryType;
import org.unidal.dal.jdbc.mapping.RawTableProvider;
import org.unidal.lookup.annotation.Named;

@Named
public class RawDao extends AbstractDao {
   @Override
   protected Class<?>[] getEntityClasses() {
      return new Class<?>[0];
   }

   public List<RawDataObject> executeQuery(String dataSource, String sql) throws DalException {
      RawDataObject proto = new RawDataObject();
      QueryDef query = new QueryDef("raw", RawEntity.class, QueryType.SELECT, sql);

      RawTableProvider.setDataSourceName(dataSource);

      try {
         return getQueryEngine().queryMultiple(query, proto, RawEntity.READSET_FULL);
      } finally {
         RawTableProvider.reset();
      }
   }

   public int executeUpdate(String dataSource, String sql) throws DalException {
      RawDataObject proto = new RawDataObject();
      QueryDef query = new QueryDef("raw", RawEntity.class, QueryType.UPDATE, sql);

      RawTableProvider.setDataSourceName(dataSource);

      try {
         return getQueryEngine().updateSingle(query, proto, RawEntity.UPDATESET_FULL);
      } finally {
         RawTableProvider.reset();
      }
   }
}
