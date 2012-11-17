package org.unidal.dal.jdbc.query;

import java.util.List;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.engine.QueryContext;

public interface QueryExecutor {

   public <T extends DataObject> List<T> executeQuery(QueryContext ctx) throws DalException;

   public int executeUpdate(QueryContext ctx) throws DalException;

   public <T extends DataObject> int[] executeUpdateBatch(QueryContext ctx, T[] protos) throws DalException;
}
