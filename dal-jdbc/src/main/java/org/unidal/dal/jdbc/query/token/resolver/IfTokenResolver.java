package org.unidal.dal.jdbc.query.token.resolver;

import java.util.HashMap;
import java.util.Map;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.DataObjectAccessor;
import org.unidal.dal.jdbc.query.token.EndTagToken;
import org.unidal.dal.jdbc.query.token.StartTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;


/**
 * &lt;IF field="..." type="NULL|NOT_NULL|ZERO|NOT_ZERO|EQ|NE|GT|GE|LT|LE" [value="..."]&gt;<br>
 * &lt;/IF&gt;
 */
@Named(type = TokenResolver.class, value = "IF")
public class IfTokenResolver implements TokenResolver, Initializable {
   @Inject
   private DataObjectAccessor m_accessor;

   private Map<String, Expression> m_expressions = new HashMap<String, Expression>();

   private boolean evaluate(StartTagToken token, QueryContext ctx) {
      String type = token.getAttribute("type", "").toUpperCase();
      Expression expression = m_expressions.get(type);

      if (expression != null) {
         String fieldName = token.getAttribute("field", "");
         DataField dataField = ctx.getEntityInfo().getFieldByName(fieldName);
         DataObject proto = ctx.getProto();
         Object fieldValue = m_accessor.getFieldValue(proto, dataField);
         String tokenValue = token.getAttribute("value", null);
         boolean fieldUsed = proto.isFieldUsed(dataField);

         return expression.evaluate(token, fieldValue, tokenValue, fieldUsed);
      } else {
         throw new DalRuntimeException(String.format("Unsupported type: %s, please use one of %s instead", type,
               m_expressions.keySet()));
      }
   }

   @Override
   public void initialize() throws InitializationException {
      for (SimpleExpression e : SimpleExpression.values()) {
         m_expressions.put(e.getType(), e);
      }
   }

   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.IF) {
         throw new DalRuntimeException("Internal error: only IF token is supported by " + getClass());
      }

      if (token instanceof StartTagToken) {
         if (ctx.isWithinIfToken()) {
            throw new DalRuntimeException("IF token can't be nested");
         }

         ctx.setWithinIfToken(true);
         ctx.setSqlResolveDisabled(!evaluate((StartTagToken) token, ctx));
         return "";
      } else if (token instanceof EndTagToken) {
         ctx.setWithinIfToken(false);
         ctx.setSqlResolveDisabled(false);
         return "";
      } else {
         throw new DalRuntimeException("Internal error: IF token can only be used as <IF ...> or </IF>");
      }
   }

   public interface Expression {
      public String getType();

      public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed);
   }

   enum SimpleExpression implements Expression {
      NOT_NULL {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return fieldUsed && fieldValue != null;
         }
      },

      NULL {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return !fieldUsed || fieldValue == null;
         }
      },

      NOT_ZERO {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, "0") != 0;
         }
      },

      ZERO {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, "0") == 0;
         }
      },

      EQ {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, tokenValue) == 0;
         }
      },

      GT {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, tokenValue) > 0;
         }
      },

      GE {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, tokenValue) >= 0;
         }
      },

      LT {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, tokenValue) < 0;
         }
      },

      LE {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, tokenValue) <= 0;
         }
      },

      NE {
         @Override
         public boolean evaluate(StartTagToken token, Object fieldValue, String tokenValue, boolean fieldUsed) {
            return compare(fieldValue, tokenValue) != 0;
         }
      };

      protected int compare(Object v1, String v2) {
         if (v1 == null && v2 == null) {
            return 0;
         } else if (v1 == null || v2 == null) {
            return v1 == null ? -1 : 1;
         } else if (v1 instanceof String) {
            return ((String) v1).compareTo(v2);
         } else if (v1 instanceof Boolean) {
            return ((Boolean) v1).compareTo(Boolean.valueOf(v2));
         } else if (v1 instanceof Integer) {
            return ((Integer) v1).intValue() - Double.valueOf(v2).intValue();
         } else if (v1 instanceof Long) {
            return (int) (((Long) v1).longValue() - Double.valueOf(v2).longValue());
         } else if (v1 instanceof Double) {
            return ((Double) v1).compareTo(Double.parseDouble(v2));
         } else {
            return v1.toString().compareTo(v2);
         }
      }

      @Override
      public String getType() {
         return name();
      }
   }
}
