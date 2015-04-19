package org.unidal.dal.jdbc.query.token;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unidal.dal.jdbc.DalRuntimeException;
import org.unidal.lookup.annotation.Named;

@Named(type = TokenParser.class)
public class DefaultTokenParser implements TokenParser {
   public List<Token> parse(String pattern) {
      List<Token> tokens = new ArrayList<Token>();
      int len = pattern.length();
      StringBuilder sb = new StringBuilder(256);
      StringBuilder attrName = new StringBuilder(32);
      StringBuilder attrValue = new StringBuilder(64);
      int numTags = 0; // number of <...>
      int numBrackets = 0; // number of {...}
      boolean hasWhiteSpace = false; // ' ', '\t', '\r', '\n'
      boolean hasStartSlash = false; // '/' of </...>
      boolean hasEndSlash = false; // '/' of <.../>
      boolean hasDollarSign = false; // '$', used for IN parameter
      boolean hasNumberSign = false; // '#', used for OUT parameter
      boolean inTag = false;
      boolean inAttrName = false;
      boolean inAttrValue = false;
      Map<String, String> attributes = new LinkedHashMap<String, String>(3);

      for (int i = 0; i < len; i++) {
         char ch = pattern.charAt(i);

         switch (ch) {
         case ' ':
         case '\t':
         case '\r':
         case '\n':
            if (inTag) {
               if (sb.length() == 0) {
                  sb.append('<');
                  sb.append(' ');
                  inTag = false;
               } else {
                  inTag = false;
                  inAttrName = true;
               }
            } else if (!hasWhiteSpace) { // only one white space is
               // counted
               sb.append(' ');
            }

            hasWhiteSpace = true;
            break;
         case '/':
            if (numTags > 0) {
               if (sb.length() == 0 && pattern.charAt(i - 1) == '<') { // </...>
                  hasStartSlash = true;
               } else if (inTag || hasWhiteSpace) { // <.../>
                  hasEndSlash = true;
               } else {
                  sb.append(ch);
               }
            } else {
               sb.append(ch);
            }

            hasWhiteSpace = false;
            break;
         case '>':
            if (numTags > 0) {
               if (hasStartSlash || hasEndSlash) {
                  numTags--;
               }

               if (sb.length() > 0) {
                  if (hasStartSlash) {
                     tokens.add(new EndTagToken(sb.toString()));
                  } else if (hasEndSlash) {
                     tokens.add(new SimpleTagToken(sb.toString(), attributes));
                  } else if (Character.isLetter(sb.charAt(0))) {
                     tokens.add(new StartTagToken(sb.toString(), attributes));
                  } else {
                     sb.append(ch);
                     tokens.add(new StringToken(sb.toString()));
                  }

                  sb.setLength(0);
                  attributes.clear();
                  hasStartSlash = false;
                  hasEndSlash = false;
               } else {
                  throw new DalRuntimeException("Illegal TAG usage, parsed tokens: " + tokens + ". Statement: "
                        + pattern);
               }
            } else {
               sb.append(ch);
            }

            inAttrName = false;
            inAttrValue = false;
            inTag = false;
            hasWhiteSpace = false;
            break;
         case '$':
            hasDollarSign = true;
            hasWhiteSpace = false;
            break;
         case '#':
            hasNumberSign = true;
            hasWhiteSpace = false;
            break;
         case '{':
            if ((hasDollarSign || hasNumberSign) && numBrackets == 0) {
               int size = sb.length();

               if (size > 0) {
                  tokens.add(new StringToken(sb.substring(0, size)));

                  sb.setLength(0);
               }

               numBrackets++;
               hasWhiteSpace = false;
               continue;
            }

            sb.append(ch);
            hasWhiteSpace = false;
            break;
         case '}':
            if (numBrackets > 0) {
               tokens.add(new ParameterToken(sb.toString(), hasDollarSign, hasNumberSign));
               sb.setLength(0);
               numBrackets--;
            } else {
               sb.append(ch);
            }

            hasDollarSign = false;
            hasNumberSign = false;
            hasWhiteSpace = false;
            break;
         case '=':
            if (inAttrName) {
               inAttrName = false;
               inAttrValue = true;
            } else {
               sb.append(ch);
            }

            hasWhiteSpace = false;
            break;
         case '\'':
         case '"':
            if (inAttrValue) {
               while (i + 1 < len) {
                  char ch2 = pattern.charAt(++i);

                  if (ch2 == ch) {
                     break;
                  } else {
                     attrValue.append(ch2);
                  }
               }

               attributes.put(attrName.toString(), attrValue.toString());
               attrName.setLength(0);
               attrValue.setLength(0);
               inTag = true;
               inAttrValue = false;
            } else {
               sb.append(ch);

               while (i + 1 < len) {
                  char ch2 = pattern.charAt(++i);

                  sb.append(ch2);

                  if (ch2 == ch) {
                     break;
                  }
               }

               if (i + 1 > len) {
                  throw new DalRuntimeException("Quote(" + ch + ") is not paired. Statement: " + pattern);
               }
            }

            hasWhiteSpace = false;
            break;
         case '\\':
            if (i + 1 < len) {
               char ch2 = pattern.charAt(i + 1);

               if (inTag || inAttrName || inAttrValue) {
                  throw new DalRuntimeException("Escaping is not supported in token: " + pattern);
               } else {
                  switch (ch2) {
                  case '>':
                  case '<':
                  case '\\':
                     sb.append(ch2);
                     i++;
                     break;
                  }
               }
            }

            break;
         case '<':
            boolean followByLetter = false;

            if (i + 1 < len) {
               char ch2 = pattern.charAt(i + 1);

               if (Character.isLetter(ch2)) {
                  followByLetter = true;
               } else if (ch2 == '/') {
                  if (sb.length() > 0) {
                     tokens.add(new StringToken(sb.toString()));
                     sb.setLength(0);
                  }

                  hasStartSlash = true;
                  break;
               }
            }

            if (followByLetter) {
               if (sb.length() > 0) {
                  tokens.add(new StringToken(sb.toString()));
                  sb.setLength(0);
               }

               numTags++;
               inTag = true;
               hasWhiteSpace = false;
               break;
            }
         default:
            if (inTag) {
               if (hasWhiteSpace) {
                  inTag = false;
                  inAttrName = true;
               } else {
                  sb.append(ch);
               }
            } else if (inAttrName) {
               attrName.append(ch);
            } else if (inAttrValue) {
               attrValue.append(ch);
            } else {
               if (numBrackets == 0) {
                  if (hasDollarSign) {
                     sb.append('$');
                     hasDollarSign = false;
                  }

                  if (hasNumberSign) {
                     sb.append('#');
                     hasNumberSign = false;
                  }
               }

               sb.append(ch);
            }

            hasWhiteSpace = false;
            break;
         }
      }

      if (sb.length() > 0) {
         tokens.add(new StringToken(sb.toString()));
      }

      return tokens;
   }
}
