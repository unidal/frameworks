package org.unidal.helper;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class DatesTest {
   private int m_firstDayOfWeek = Calendar.SUNDAY;

   private void checkBeginOf(String expected, char field, String source) {
      String format = "yyyy-MM-dd HH:mm:ss";

      Assert.assertEquals(expected, Dates.from(source, format).firstDayOfWeek(m_firstDayOfWeek).beginOf(field).asString(format));
   }

   private void checkEndOf(String expected, char field, String source) {
      String format = "yyyy-MM-dd HH:mm:ss";

      Assert.assertEquals(expected, Dates.from(source, format).firstDayOfWeek(m_firstDayOfWeek).endOf(field).asString(format));
   }
   @Test
   public void test() {
      long now = System.currentTimeMillis();
      long last = Dates.from(now).hour(-1).asLong(); // one hour ago

      Assert.assertEquals(now - 3600 * 1000L, last);

      long time = Dates.from(now).day(1).hour(1).minute(1).asLong(); // 1d1h1m later

      Assert.assertEquals(now + 25 * 3600 * 1000L + 60 * 1000L, time);
   }

   @Test
   public void testBeginOf() {
      checkBeginOf("2013-05-25 11:48:05", 's', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-25 11:48:00", 'm', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-25 11:00:00", 'h', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-25 00:00:00", 'd', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-01 00:00:00", 'M', "2013-05-25 11:48:05");
      checkBeginOf("2013-01-01 00:00:00", 'Y', "2013-05-25 11:48:05");

      m_firstDayOfWeek = Calendar.MONDAY;
      checkBeginOf("2013-05-13 00:00:00", 'w', "2013-05-19 11:48:05");
      checkBeginOf("2013-05-20 00:00:00", 'w', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-20 00:00:00", 'w', "2013-05-26 11:48:05");

      m_firstDayOfWeek = Calendar.SUNDAY;
      checkBeginOf("2013-05-19 00:00:00", 'w', "2013-05-19 11:48:05");
      checkBeginOf("2013-05-19 00:00:00", 'w', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-26 00:00:00", 'w', "2013-05-26 11:48:05");

      m_firstDayOfWeek = Calendar.SATURDAY;
      checkBeginOf("2013-05-18 00:00:00", 'w', "2013-05-19 11:48:05");
      checkBeginOf("2013-05-25 00:00:00", 'w', "2013-05-25 11:48:05");
      checkBeginOf("2013-05-25 00:00:00", 'w', "2013-05-26 11:48:05");
   }

   @Test
   public void testBugfixs() {
      checkBeginOf("2013-05-25 00:00:00", 'd', "2013-05-25 15:48:05");
      checkEndOf("2013-05-25 23:59:59", 'd', "2013-05-25 15:48:05");
      
      m_firstDayOfWeek = Calendar.MONDAY;

      checkBeginOf("2013-05-20 00:00:00", 'w', "2013-05-25 15:48:05");
      checkEndOf("2013-05-26 23:59:59", 'w', "2013-05-25 15:48:05");
   }

   @Test
   public void testEndOf() {
      checkEndOf("2013-05-25 11:48:05", 's', "2013-05-25 11:48:05");
      checkEndOf("2013-05-25 11:48:59", 'm', "2013-05-25 11:48:05");
      checkEndOf("2013-05-25 11:59:59", 'h', "2013-05-25 11:48:05");
      checkEndOf("2013-05-25 23:59:59", 'd', "2013-05-25 11:48:05");
      checkEndOf("2013-05-31 23:59:59", 'M', "2013-05-25 11:48:05");
      checkEndOf("2013-12-31 23:59:59", 'Y', "2013-05-25 11:48:05");

      m_firstDayOfWeek = Calendar.MONDAY;
      checkEndOf("2013-05-19 23:59:59", 'w', "2013-05-19 11:48:05");
      checkEndOf("2013-05-26 23:59:59", 'w', "2013-05-25 11:48:05");
      checkEndOf("2013-05-26 23:59:59", 'w', "2013-05-26 11:48:05");

      m_firstDayOfWeek = Calendar.SUNDAY;
      checkEndOf("2013-05-25 23:59:59", 'w', "2013-05-19 11:48:05");
      checkEndOf("2013-05-25 23:59:59", 'w', "2013-05-25 11:48:05");
      checkEndOf("2013-06-01 23:59:59", 'w', "2013-05-26 11:48:05");

      m_firstDayOfWeek = Calendar.SATURDAY;
      checkEndOf("2013-05-24 23:59:59", 'w', "2013-05-19 11:48:05");
      checkEndOf("2013-05-31 23:59:59", 'w', "2013-05-25 11:48:05");
      checkEndOf("2013-05-31 23:59:59", 'w', "2013-05-26 11:48:05");
   }
}
