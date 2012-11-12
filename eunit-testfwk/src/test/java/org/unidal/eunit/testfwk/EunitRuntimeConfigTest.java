package org.unidal.eunit.testfwk;

import java.io.IOException;

import org.junit.Assert;

import org.junit.Test;

import org.unidal.eunit.testfwk.EunitRuntimeConfig.EunitProperties;

public class EunitRuntimeConfigTest {
   @Test
   public void testProperties() throws IOException {
      // name and value (optional)
      parseAndCheck("-n1 v1 -n2 v2 -n3 v3", "{n1=[v1], n2=[v2], n3=[v3]}");
      parseAndCheck("-n1 v1 -n2 -n3 v3", "{n1=[v1], n2=[], n3=[v3]}");
      parseAndCheck("-n1 v1 -n2 -n3", "{n1=[v1], n2=[], n3=[]}");
      parseAndCheck("-n1 -n2 -n3", "{n1=[], n2=[], n3=[]}");

      // multiple lines
      parseAndCheck("-n1\r\n\r\nv1\r\n-n2\r\n-n3\r\n", "{n1=[v1], n2=[], n3=[]}");

      // quoted and escaped
      parseAndCheck("-n1 '#v1' -n2 '-v2' -n3 v3", "{n1=[#v1], n2=[-v2], n3=[v3]}");
      parseAndCheck("-n1 '#v1' 'It\\\'s me' -n2 '-v2' -n3 v3", "{n1=[#v1, It\'s me], n2=[-v2], n3=[v3]}");

      // comments to the end of line
      parseAndCheck("-n1 v1 #-n2 v2 -n3 v3", "{n1=[v1]}");
      parseAndCheck("-n1 v1 #-n2 v2\r\n-n3 v3", "{n1=[v1], n3=[v3]}");

      // multiple values
      parseAndCheck("-n1 v11 v12 v13 -n2 v2 -n3 v3", "{n1=[v11, v12, v13], n2=[v2], n3=[v3]}");
      parseAndCheck("-n1 v11 '-v12' v13 -n2 v2 -n3 v3", "{n1=[v11, -v12, v13], n2=[v2], n3=[v3]}");
      parseAndCheck("-n1 v11 '-v12' v13 #comments\r\n -n2 v2 -n3 v3", "{n1=[v11, -v12, v13], n2=[v2], n3=[v3]}");
   }

   protected void parseAndCheck(String content, String expected) throws IOException {
      EunitProperties properties = new EunitProperties();

      properties.loadProperties(content);
      Assert.assertEquals("Error when parsing properties: " + content, expected, properties.toString());
   }
}
