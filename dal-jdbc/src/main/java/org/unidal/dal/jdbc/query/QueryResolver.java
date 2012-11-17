package org.unidal.dal.jdbc.query;

import org.unidal.dal.jdbc.engine.QueryContext;

public interface QueryResolver {
   public void resolve(QueryContext ctx);
}
