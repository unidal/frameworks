package org.unidal.web.security.authorization;

import java.io.InputStream;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.web.authorization.ApplicationAuthenticationToken;
import org.unidal.web.authorization.MyApplication;
import org.unidal.web.authorization.MyAuthorization;
import org.unidal.web.authorization.MyRealm;
import org.unidal.web.security.authorization.transform.DefaultSaxParser;

public class TulipRealmTest extends ComponentTestCase {
   @Before
   public void before() throws Exception {
      define(MockTulipAuthorization.class);
      define(MockTulipApplication.class);
   }

   @Test
   public void test() {
      MyRealm realm = lookup(MyRealm.class);
      SecurityUtils.setSecurityManager(new DefaultSecurityManager(realm));
      Subject subject = SecurityUtils.getSubject();

      subject.login(new ApplicationAuthenticationToken("10001", "127.0.0.1"));

      Assert.assertEquals(true, subject.isPermitted("scenario:app_for_cat"));
      Assert.assertEquals(true, subject.isPermitted("scenario:org_for_cat"));
      Assert.assertEquals(true, subject.isPermitted("graphql:app_for_cat"));
      Assert.assertEquals(false, subject.isPermitted("graphql:org_for_cat"));
   }

   @Test
   public void test2() {
      MyRealm realm = lookup(MyRealm.class);
      SecurityUtils.setSecurityManager(new DefaultSecurityManager(realm));
      Subject subject = SecurityUtils.getSubject();

      subject.login(new ApplicationAuthenticationToken("10002", "127.0.0.1"));

      Assert.assertEquals(false, subject.isPermitted("scenario:org_for_cat"));
      Assert.assertEquals(true, subject.isPermitted("graphql:org_for_cat"));
   }
   
   @Test
   public void test3() {
      MyRealm realm = lookup(MyRealm.class);
      SecurityUtils.setSecurityManager(new DefaultSecurityManager(realm));
      Subject subject = SecurityUtils.getSubject();
      
      subject.login(new ApplicationAuthenticationToken("10003", "192.168.0.1"));
      
      Assert.assertEquals(true, subject.isPermitted("scenario:org_for_cat"));
      Assert.assertEquals(true, subject.isPermitted("graphql:org_for_cat"));
   }

   @Named(type = MyAuthorization.class)
   public static class MockTulipAuthorization extends MyAuthorization {
      @Override
      public void initialize() throws InitializationException {
         try {
            InputStream in = getClass().getResourceAsStream("authorization.xml");

            m_authorization = DefaultSaxParser.parse(in);
            m_authorization.accept(new Verification());
         } catch (Exception e) {
            throw new InitializationException("Error when reading resource authorization.xml!", e);
         }
      }
   }

   @Named(type = MyApplication.class)
   public static class MockTulipApplication extends MyApplication {
      @Override
      public void initialize() throws InitializationException {
         try {
            InputStream in = getClass().getResourceAsStream("authorization.xml");

            m_authorization = DefaultSaxParser.parse(in);
            m_authorization.accept(new Verification());
         } catch (Exception e) {
            throw new InitializationException("Error when reading resource authorization.xml!", e);
         }
      }
   }
}
