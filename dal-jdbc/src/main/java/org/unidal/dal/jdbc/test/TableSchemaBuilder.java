package org.unidal.dal.jdbc.test;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.test.meta.entity.EntityModel;
import org.unidal.dal.jdbc.test.meta.entity.MemberModel;
import org.unidal.dal.jdbc.test.meta.entity.PrimaryKeyModel;
import org.unidal.dal.jdbc.test.meta.transform.BaseVisitor2;
import org.unidal.helper.Splitters;
import org.unidal.lookup.annotation.Named;

@Named(instantiationStrategy = Named.PER_LOOKUP)
public class TableSchemaBuilder extends BaseVisitor2 {
   private List<String> m_statements = new ArrayList<String>();

   private StringBuilder m_sb;

   public List<String> getStatements() {
      return m_statements;
   }

   private String guessType(MemberModel member) {
      String type = member.getValueType();
      Integer len = member.getLength();

      if ("int".equals(type)) {
         return String.format("int(%s)", len);
      } else if ("long".equals(type)) {
         return "bigint";
      } else if ("double".equals(type)) {
         return "double";
      } else if ("String".equals(type)) {
         if (len == 2147483647) {
            return "longtext";
         } else if (len == 16777215) {
            return "mediumtext";
         } else if (len == 65535) {
            return "text";
         }

         return String.format("varchar(%s)", len);
      } else if ("Date".equals(type)) {
         return "datetime";
      } else if ("byte[]".equals(type)) {
         if (len == 2147483647) {
            return "longblob";
         } else if (len == 16777215) {
            return "mediumblob";
         } else if (len == 65535) {
            return "blob";
         }
      }

      throw new IllegalStateException("Unable to guest field type for " + type + "!");
   }

   private String quote(String field) {
      return "`" + field + "`";
   }

   private String quotes(String fieldList) {
      List<String> fields = Splitters.by(',').trim().split(fieldList);
      StringBuilder sb = new StringBuilder(32);

      for (String field : fields) {
         if (sb.length() > 0) {
            sb.append(", ");
         }

         sb.append("`").append(field).append("`");
      }

      return sb.toString();
   }

   private void trimComma() {
      int len = m_sb.length();

      if (len >= 3) {
         char ch0 = m_sb.charAt(len - 3);
         char ch1 = m_sb.charAt(len - 2);
         char ch2 = m_sb.charAt(len - 1);

         if (ch0 == ',' && ch1 == '\r' && ch2 == '\n') {
            m_sb.setCharAt(len - 3, ch1);
            m_sb.setCharAt(len - 2, ch2);
            m_sb.setLength(len - 1);
         }
      }
   }

   @Override
   protected void visitEntityChildren(EntityModel entity) {
      m_sb = new StringBuilder(1024);
      m_sb.append("CREATE TABLE ").append(quote(entity.getTable())).append(" (\r\n");

      super.visitEntityChildren(entity);

      trimComma();
      m_sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;\r\n");
      m_statements.add(m_sb.toString());
   }

   @Override
   protected void visitMemberChildren(MemberModel member) {
      m_sb.append("  ").append(quote(member.getField())).append(" ");
      m_sb.append(guessType(member));

      if (member.isNullable()) {
         m_sb.append(" DEFAULT NULL");
      } else {
         m_sb.append(" NOT NULL");
      }

      if (member.isAutoIncrement()) {
         m_sb.append(" AUTO_INCREMENT");
      }

      m_sb.append(",\r\n");
   }

   @Override
   protected void visitPrimaryKeyChildren(PrimaryKeyModel primaryKey) {
      m_sb.append("  PRIMARY KEY (");
      m_sb.append(quotes(primaryKey.getMembers()));
      m_sb.append("),\r\n");
   }
}
