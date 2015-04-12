package org.unidal.dal.jdbc.test;

import java.io.InputStream;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.test.data.entity.ColModel;
import org.unidal.dal.jdbc.test.data.entity.DatabaseModel;
import org.unidal.dal.jdbc.test.data.entity.RowModel;
import org.unidal.dal.jdbc.test.data.entity.TableModel;
import org.unidal.dal.jdbc.test.data.transform.BaseVisitor2;
import org.unidal.dal.jdbc.test.data.transform.DefaultSaxParser;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(instantiationStrategy = Named.PER_LOOKUP)
public class TableLoader extends BaseVisitor2 {
   @Inject
   private RawDao m_dao;

   private String m_ds;

   private DalException m_cause;

   private String buildSql(String table, RowModel row) {
      StringBuilder sb = new StringBuilder(2048);

      sb.append(String.format("INSERT INTO `%s` (", table));

      // field names
      boolean first = true;

      for (ColModel col : row.getCols()) {
         if (first) {
            first = false;
         } else {
            sb.append(',');
         }

         sb.append('`').append(col.getName()).append('`');
      }

      sb.append(") VALUES (");

      // field values
      first = true;

      for (ColModel col : row.getCols()) {
         if (first) {
            first = false;
         } else {
            sb.append(',');
         }

         sb.append('\'').append(col.getText()).append('\'');
      }

      sb.append(")");

      return sb.toString();
   }

   public void loadFrom(String ds, InputStream in) throws Exception {
      DatabaseModel database = DefaultSaxParser.parse(in);

      m_ds = ds;
      database.accept(this);

      if (m_cause != null) {
         throw m_cause;
      }
   }

   @Override
   protected void visitTableChildren(TableModel table) {
      try {
         for (RowModel row : table.getRows()) {
            String sql = buildSql(table.getName(), row);

            m_dao.executeUpdate(m_ds, sql);
         }
      } catch (DalException e) {
         m_cause = e;
      }
   }
}
