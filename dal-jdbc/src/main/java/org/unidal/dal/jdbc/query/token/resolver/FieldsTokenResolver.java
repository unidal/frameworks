package org.unidal.dal.jdbc.query.token.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.DataObject;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.annotation.Attribute;
import org.unidal.dal.jdbc.annotation.Relation;
import org.unidal.dal.jdbc.annotation.SubObjects;
import org.unidal.dal.jdbc.engine.QueryContext;
import org.unidal.dal.jdbc.entity.EntityInfo;
import org.unidal.dal.jdbc.entity.EntityInfoManager;
import org.unidal.dal.jdbc.query.Parameter;
import org.unidal.dal.jdbc.query.QueryNaming;
import org.unidal.dal.jdbc.query.token.SimpleTagToken;
import org.unidal.dal.jdbc.query.token.Token;
import org.unidal.dal.jdbc.query.token.TokenType;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

/**
 * &lt;FIELDS /&gt;
 */
@Named(type = TokenResolver.class, value = "FIELDS")
public class FieldsTokenResolver implements TokenResolver {
   @Inject
   private EntityInfoManager m_manager;

   @Inject
   private ExpressionResolver m_expressionResolver;
   
   @Inject
   private QueryNaming m_naming;

   @SuppressWarnings("unchecked")
   public String resolve(Token token, QueryContext ctx) {
      if (token.getType() != TokenType.FIELDS) {
         throw new DalRuntimeException("Internal error: only FIELDS token is supported by " + getClass());
      }

      EntityInfo entityInfo = ctx.getEntityInfo();
      SimpleTagToken fields = (SimpleTagToken) token;
      String output = fields.getAttribute("output", "true");
      List<String> excludes = split(fields.getAttribute("excludes", ""), ",");
      StringBuilder sb = new StringBuilder(1024);
      DataObject proto = ctx.getProto();

      switch (ctx.getQuery().getType()) {
      case SELECT:
         SubObjects subobjects = entityInfo.getSubobjects(ctx.getReadset());
         String[] names;
         List<Readset<Object>> readsets;

         if (subobjects != null) {
            names = subobjects.value();
            readsets = ((Readset<Object>) ctx.getReadset()).getChildren();
         } else {
            names = null;
            readsets = new ArrayList<Readset<Object>>(1);
            readsets.add((Readset<Object>) ctx.getReadset());
         }

         int size = readsets.size();

         for (int i = 0; i < size; i++) {
            Readset<Object> readset = readsets.get(i);
            Relation relation = (names == null ? null : entityInfo.getRelation(names[i]));
            String alias = (relation == null ? entityInfo.getAlias() : relation.alias());
            String subObjectName = (relation == null ? null : names[i]);

            for (DataField field : readset.getFields()) {
               EntityInfo ei = m_manager.getEntityInfo(field.getEntityClass());
               Attribute attribute = ei.getAttribute(field);

               if (attribute != null) {
                  if (excludes.contains(field.getName())) {
                     continue;
                  }

                  if (sb.length() > 0) {
                     sb.append(',');
                  }

                  if (attribute.selectExpr().length() > 0) {
                     sb.append(m_expressionResolver.resolve(ctx, attribute.selectExpr()));
                  } else {
                     sb.append(alias).append('.').append(m_naming.getField(attribute.field()));
                  }

                  if ("true".equals(output)) {
                     ctx.addOutField(field);
                     ctx.addOutSubObjectName(subObjectName);
                  }
               } else {
                  throw new DalRuntimeException("Internal error: No Attribute annotation defined for field: " + field);
               }
            }
         }

         break;
      case INSERT:
         for (DataField field : entityInfo.getAttributeFields()) {
            Attribute attribute = entityInfo.getAttribute(field);

            if (attribute != null) {
               if (attribute.field().length() > 0 && !(attribute.autoIncrement() && !proto.isFieldUsed(field))) {
                  if (sb.length() > 0) {
                     sb.append(',');
                  }

                  sb.append(m_naming.getField(attribute.field()));
               }
            } else {
               throw new DalRuntimeException("Internal error: No Attribute annotation defined for field: " + field);
            }
         }

         break;
      case UPDATE:
         for (DataField field : ctx.getUpdateset().getFields()) {
            Attribute attribute = entityInfo.getAttribute(field);

            if (attribute != null) {
               if (proto.isFieldUsed(field) || attribute.updateExpr().length() > 0) {
                  if (sb.length() > 0) {
                     sb.append(',');
                  }

                  if (!proto.isFieldUsed(field) && attribute.updateExpr().length() > 0) {
                     sb.append(m_naming.getField(attribute.field())).append('=').append(m_expressionResolver.resolve(ctx, attribute.updateExpr()));
                  } else {
                     sb.append(m_naming.getField(attribute.field())).append("=?");
                     ctx.addParameter(new Parameter(field));
                  }
               }
            } else {
               throw new DalRuntimeException("Internal error: No Attribute annotation defined for field: " + field);
            }
         }

         break;
      case DELETE:
         throw new DalRuntimeException("FIELDS token does not support query type: " + ctx.getQuery().getType());
      default:
         throw new DalRuntimeException("FIELDS token does not support query type: " + ctx.getQuery().getType());
      }

      return sb.toString();
   }

   private List<String> split(String data, String delimiter) {
      if (data != null && data.length() > 0) {
         String[] parts = data.split(Pattern.quote(delimiter));

         return Arrays.asList(parts);
      } else {
         return Collections.emptyList();
      }
   }
}
