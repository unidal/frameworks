package org.unidal.dal.jdbc.test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.cat.Cat;
import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.dal.jdbc.test.data.entity.ColModel;
import org.unidal.dal.jdbc.test.data.entity.DatabaseModel;
import org.unidal.dal.jdbc.test.data.entity.RowModel;
import org.unidal.dal.jdbc.test.data.entity.TableModel;
import org.unidal.dal.jdbc.test.data.transform.BaseVisitor2;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named
public class DatabaseDumper {
   @Inject
   private RawDao m_dao;

   public DatabaseModel dump(DatabaseModel base, String ds, String... tables) throws DalException {
      DatabaseModel model = new DatabaseModel();

      for (String table : tables) {
         List<RawDataObject> rowset = m_dao.executeQuery(ds, String.format("select * from `%s`", table));
         TableBuilder builder = new TableBuilder(table, rowset);

         model.accept(builder);
      }

      if (base != null) {
         base.accept(new DeltaRemoval(model, ds));
      }

      return model;
   }

   @SuppressWarnings("unused")
   class DeltaRemoval extends BaseVisitor2 {
      private DatabaseModel m_database;

      private TableModel m_table;

      private Set<String> m_keys = new LinkedHashSet<String>();

      private String m_ds;

      public DeltaRemoval(DatabaseModel database, String ds) {
         m_database = database;
         m_ds = ds;
      }

      @Override
      protected void visitRowChildren(RowModel row) {
         super.visitRowChildren(row);
      }

      @Override
      protected void visitTableChildren(TableModel table) {
         m_table = table;

         fetchIndexColumns(table.getName());

         super.visitTableChildren(table);
      }

      private void fetchIndexColumns(String table) {
         m_keys.clear();

         try {
            String sql = String.format("select COLUMN_NAME from INFORMATION_SCHEMA.INDEXES "
                  + "where TABLE_NAME='%s' and PRIMARY_KEY='true' order by PRIMARY_KEY", table.toUpperCase());

            List<RawDataObject> list = m_dao.executeQuery(m_ds, sql);

            for (RawDataObject item : list) {
               Object key = item.getFieldValue("COLUMN_NAME");

               m_keys.add(key.toString());
            }
         } catch (DalException e) {
            Cat.logError(e);
         }
      }
   }

   static class TableBuilder extends BaseVisitor2 {
      private String m_table;

      private List<RawDataObject> m_list;

      public TableBuilder(String table, List<RawDataObject> list) {
         m_table = table;
         m_list = list;
      }

      @Override
      protected void visitDatabaseChildren(DatabaseModel database) {
         TableModel table = new TableModel(m_table);

         database.addTable(table);
         super.visitDatabaseChildren(database);
      }

      @Override
      protected void visitTableChildren(TableModel table) {
         for (RawDataObject item : m_list) {
            RowModel row = new RowModel();

            for (Map.Entry<String, Object> e : item.getFields()) {
               String name = e.getKey();
               Object value = e.getValue();
               ColModel col = new ColModel().setName(name);

               if (value != null) {
                  col.setText(value.toString());
               }

               row.addCol(col);
            }

            table.addRow(row);
         }
      }
   }
}
