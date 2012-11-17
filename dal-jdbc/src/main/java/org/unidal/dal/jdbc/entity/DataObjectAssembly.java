package org.unidal.dal.jdbc.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.engine.QueryContext;

public interface DataObjectAssembly {
   public <T extends DataObject> List<T> assemble(QueryContext ctx, ResultSet rs) throws SQLException;
}
