package org.unidal.converter.dom;

import java.lang.annotation.ElementType;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.unidal.converter.ConverterManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NodeConverterTest extends TestCase {
   ConverterManager m_manager = ConverterManager.getInstance();

   private Element createElement(Document doc, String name) {
      return createElement(doc, name, null);
   }

   private Element createElement(Document doc, String name, String text) {
      return createElement(doc, name, text, false);
   }

   private Element createElement(Document doc, String name, String text, boolean isCdata) {
      Element element = doc.createElement(name);

      if (text != null) {
         if (isCdata) {
            element.appendChild(doc.createCDATASection(text));
         } else {
            element.setTextContent(text);
         }
      }

      return element;
   }

   @SuppressWarnings("unchecked")
   public void testArrayAndList() {
      Document doc = createDocument();
      Element node = doc.createElement("tag");

      Element field5 = createElement(doc, "field5");
      node.appendChild(field5);
      field5.appendChild(createElement(doc, "item", "2"));
      field5.appendChild(createElement(doc, "item", "3"));
      field5.appendChild(createElement(doc, "item", "4"));

      Element field6 = createElement(doc, "field6");
      node.appendChild(field6);
      field6.appendChild(createElement(doc, "item", "2"));
      field6.appendChild(createElement(doc, "item", "3"));
      field6.appendChild(createElement(doc, "item", "4"));

      Model model1 = (Model) m_manager.convert(node, Model.class);

      int[] intArray = model1.getField5();
      assertEquals(3, intArray.length);
      assertEquals(2, intArray[0]);
      assertEquals(3, intArray[1]);
      assertEquals(4, intArray[2]);

      List<Integer> integerList = model1.getField6();
      assertEquals(3, integerList.size());

      assertEquals(new Integer(2), integerList.get(0));
      assertEquals(new Integer(3), integerList.get(1));
      assertEquals(new Integer(4), integerList.get(2));

      Element field8 = createElement(doc, "field8");
      node.appendChild(field8);
      field8.appendChild(createElement(doc, "field1", "3"));
      field8.appendChild(createElement(doc, "field2", "4", true));

      Node[] nodeArray = (Node[]) m_manager.convert(field8, Node[].class);
      assertEquals(2, nodeArray.length);

      List<Node> nodeList = (List<Node>) m_manager.convert(field8, List.class);
      assertEquals(2, nodeList.size());
   }

   private Document createDocument() {
      try {
         return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   public void testNestedModel() {
      Document doc = createDocument();
      Element node = doc.createElement("tag");

      Element field7 = createElement(doc, "field7");
      node.appendChild(field7);
      field7.appendChild(createElement(doc, "field1", "3"));
      field7.appendChild(createElement(doc, "field2", "4", true));

      Element field8 = createElement(doc, "field8");
      node.appendChild(field8);
      field8.appendChild(createElement(doc, "field1", "3"));
      field8.appendChild(createElement(doc, "field2", "4", true));

      Model model1 = (Model) m_manager.convert(node, Model.class);

      Node f7 = model1.getField7();
      assertEquals(2, f7.getChildNodes().getLength());

      List<Node> f8 = model1.getField8();
      assertEquals(2, f8.size());
   }

   public void testSimple() {
      Document doc = createDocument();
      Element node = doc.createElement("tag");

      node.appendChild(createElement(doc, "field1", "1"));
      node.appendChild(createElement(doc, "field2", "2", true));
      node.setAttribute("field3", "3");
      node.appendChild(createElement(doc, "field4", "true"));
      node.appendChild(createElement(doc, "field9", "FIELD"));

      Model model1 = (Model) m_manager.convert(node, Model.class);
      assertEquals(1, model1.getField1());
      assertEquals("2", model1.getField2());
      assertEquals(3L, model1.getField3());
      assertEquals(true, model1.isField4());
      assertEquals(ElementType.FIELD, model1.getField9());
   }

   public void testSimpleModel() {
      Document doc = createDocument();
      Element node = doc.createElement("tag");

      Element child = createElement(doc, "child");
      node.appendChild(child);
      child.appendChild(createElement(doc, "field1", "3"));
      child.appendChild(createElement(doc, "field2", "4", true));

      Model model1 = (Model) m_manager.convert(node, Model.class);

      Model childModel1 = model1.getChild();
      assertNotNull(childModel1);
      assertEquals(3, childModel1.getField1());
      assertEquals("4", childModel1.getField2());
   }

   public static class Model {
      private int m_field1;
      private String m_field2;
      private long m_field3;
      private boolean m_field4;
      private int[] m_field5;
      private List<Integer> m_field6;
      private Node m_field7;
      private List<Node> m_field8;
      private ElementType m_field9;

      private Model m_child;

      public Model getChild() {
         return m_child;
      }

      public int getField1() {
         return m_field1;
      }

      public String getField2() {
         return m_field2;
      }

      public long getField3() {
         return m_field3;
      }

      public int[] getField5() {
         return m_field5;
      }

      public List<Integer> getField6() {
         return m_field6;
      }

      public Node getField7() {
         return m_field7;
      }

      public List<Node> getField8() {
         return m_field8;
      }

      public ElementType getField9() {
         return m_field9;
      }

      public boolean isField4() {
         return m_field4;
      }

      public void setChild(Model child) {
         m_child = child;
      }

      public void setField1(int field1) {
         m_field1 = field1;
      }

      public void setField2(String field2) {
         m_field2 = field2;
      }

      public void setField3(long field3) {
         m_field3 = field3;
      }

      public void setField4(boolean field4) {
         m_field4 = field4;
      }

      public void setField5(int[] field5) {
         m_field5 = field5;
      }

      public void setField6(List<Integer> field6) {
         m_field6 = field6;
      }

      public void setField7(Node field7) {
         m_field7 = field7;
      }

      public void setField8(List<Node> field8) {
         m_field8 = field8;
      }

      public void setField9(ElementType field9) {
         m_field9 = field9;
      }
   }

   public static class Table {

   }
}
