package org.unidal.dal.jdbc.test;

import java.io.InputStream;

import org.unidal.dal.jdbc.raw.RawDao;
import org.unidal.dal.jdbc.test.meta.EntitiesModelHelper;
import org.unidal.dal.jdbc.test.meta.entity.EntitiesModel;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named
public class TableMaker extends ContainerHolder {
   @Inject
   private RawDao m_dao;

   public void make(String ds, InputStream in) throws Exception {
      EntitiesModel entities = EntitiesModelHelper.fromXml(in);
      TableSchemaBuilder builder = lookup(TableSchemaBuilder.class);

      entities.accept(builder);

      for (String statement : builder.getStatements()) {
         m_dao.executeUpdate(ds, statement);
      }

      release(builder);
   }
}
