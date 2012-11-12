package org.unidal.converter.advanced;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.unidal.converter.ConverterManager;

public class ConstructorConverterTest extends TestCase {
   ConverterManager m_manager = ConverterManager.getInstance();

   public void testSingleParameterConstrcutorClass() throws Exception {
      assertEquals(new StringBuffer(256).capacity(), ((StringBuffer)m_manager.convert(256, StringBuffer.class)).capacity());
      assertEquals(new File("."), m_manager.convert(".", File.class));
      assertEquals(new Date(1), m_manager.convert(1L, Date.class));
      assertEquals(new MessageFormat("{0}"), m_manager.convert("{0}", MessageFormat.class));
      assertEquals(new URL("http://www.example.org/"), m_manager.convert("http://www.example.org/", URL.class));
   }
}
