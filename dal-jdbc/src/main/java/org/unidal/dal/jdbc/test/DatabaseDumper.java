package org.unidal.dal.jdbc.test;

import java.util.List;
import java.util.Map;

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

   public DatabaseModel dump(String ds, String... tables) throws DalException {
      DatabaseModel model = new DatabaseModel();

      for (String table : tables) {
         List<RawDataObject> rowset = m_dao.executeQuery(ds, String.format("select * from `%s`", table));
         TableBuilder builder = new TableBuilder(table, rowset);

         model.accept(builder);
      }

      return model;
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
