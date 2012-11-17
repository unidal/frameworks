package org.unidal.dal.jdbc.query.token;

import java.util.List;

public interface TokenParser {
   public List<Token> parse(String pattern);
}
