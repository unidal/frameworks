package org.unidal.converter;

import junit.framework.TestSuite;

import org.unidal.converter.advanced.ConstructorConverterTest;
import org.unidal.converter.basic.BasicConverterTest;
import org.unidal.converter.collection.ArrayConverterTest;
import org.unidal.converter.collection.ListConverterTest;
import org.unidal.converter.dom.NodeConverterTest;

public class AllTests extends TestSuite {
   public static TestSuite suite() {
      TestSuite suite = new TestSuite();
      
      suite.addTestSuite(TypeUtilTest.class);
      suite.addTestSuite(BasicConverterTest.class);
      suite.addTestSuite(ArrayConverterTest.class);
      suite.addTestSuite(ListConverterTest.class);
      suite.addTestSuite(NodeConverterTest.class);
      suite.addTestSuite(ConstructorConverterTest.class);
      
      return suite;
   }
}
