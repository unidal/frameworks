package org.unidal.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
   public static DateHelper from(Date date) {
      return new DateHelper(date);
   }

   public static DateHelper from(long millis) {
      return new DateHelper(millis);
   }

   public static DateHelper from(String date, String format) {
      try {
         return new DateHelper(new SimpleDateFormat(format).parse(date));
      } catch (ParseException e) {
         throw new RuntimeException(String.format("Unable to parse date(%s) with format(%s)!", date, format));
      }
   }

   public static DateHelper now() {
      return new DateHelper(System.currentTimeMillis());
   }

   public static class DateHelper {
      private Calendar m_cal = Calendar.getInstance();

      public DateHelper(Date date) {
         m_cal.setTime(date);
      }

      public DateHelper(long millis) {
         m_cal.setTimeInMillis(millis);
      }

      public Date asDate() {
         return m_cal.getTime();
      }

      public long asLong() {
         return m_cal.getTimeInMillis();
      }

      public String asString(String format) {
         return new SimpleDateFormat(format).format(m_cal.getTime());
      }

      /**
       * <ul>
       * <li>s: second</li>
       * <li>m: minute</li>
       * <li>h, H: hour</li>
       * <li>d: day</li>
       * <li>w: week</li>
       * <li>M: month</li>
       * <li>Y: year</li>
       * </ul>
       * 
       * @param field
       *           one of 's', 'm', 'h', 'H', 'd', 'w', 'M', 'Y'
       * @return Date helper instance
       */
      public DateHelper beginOf(char field) {
         switch (field) {
         case 'Y':
            m_cal.set(Calendar.MONTH, 0);
         case 'M':
            m_cal.set(Calendar.DATE, 1);
         case 'd':
            m_cal.set(Calendar.HOUR_OF_DAY, 0);
         case 'h':
         case 'H':
            m_cal.set(Calendar.MINUTE, 0);
         case 'm':
            m_cal.set(Calendar.SECOND, 0);
         case 's':
            m_cal.set(Calendar.MILLISECOND, 0);
            break;
         case 'w':
            int firstDayOfWeek = m_cal.getFirstDayOfWeek();
            int day = m_cal.get(Calendar.DAY_OF_WEEK);

            if (firstDayOfWeek <= day) {
               m_cal.add(Calendar.DATE, firstDayOfWeek - day);
            } else {
               m_cal.add(Calendar.DATE, firstDayOfWeek - day - 7);
            }

            m_cal.set(Calendar.HOUR_OF_DAY, 0);
            m_cal.set(Calendar.MINUTE, 0);
            m_cal.set(Calendar.SECOND, 0);
            m_cal.set(Calendar.MILLISECOND, 0);
            break;
         default:
            throw new RuntimeException(String.format("Unknown field(%s)!", field));
         }

         return this;
      }

      public int day() {
         return m_cal.get(Calendar.DATE);
      }
      
      public int dayOfWeek() {
         return m_cal.get(Calendar.DAY_OF_WEEK);
      }

      public DateHelper day(int delta) {
         m_cal.add(Calendar.DATE, delta);
         return this;
      }

      /**
       * <ul>
       * <li>s: second</li>
       * <li>m: minute</li>
       * <li>h, H: hour</li>
       * <li>d: day</li>
       * <li>w: week</li>
       * <li>M: month</li>
       * <li>Y: year</li>
       * </ul>
       * 
       * @param field
       *           one of 's', 'm', 'h', 'H', 'd', 'w', 'M', 'Y'
       * @return Date helper instance
       */
      public DateHelper endOf(char field) {
         switch (field) {
         case 'Y':
            m_cal.set(Calendar.MONTH, 11);
         case 'M':
            m_cal.set(Calendar.DATE, m_cal.getActualMaximum(Calendar.DAY_OF_MONTH));
         case 'd':
            m_cal.set(Calendar.HOUR_OF_DAY, 23);
         case 'h':
         case 'H':
            m_cal.set(Calendar.MINUTE, 59);
         case 'm':
            m_cal.set(Calendar.SECOND, 59);
         case 's':
            m_cal.set(Calendar.MILLISECOND, 999);
            break;
         case 'w':
            int firstDayOfWeek = m_cal.getFirstDayOfWeek();
            int day = m_cal.get(Calendar.DAY_OF_WEEK);

            if (firstDayOfWeek <= day) {
               m_cal.add(Calendar.DATE, 6 + firstDayOfWeek - day);
            } else {
               m_cal.add(Calendar.DATE, firstDayOfWeek - day - 1);
            }

            m_cal.set(Calendar.HOUR_OF_DAY, 23);
            m_cal.set(Calendar.MINUTE, 59);
            m_cal.set(Calendar.SECOND, 59);
            m_cal.set(Calendar.MILLISECOND, 999);
            break;
         default:
            throw new RuntimeException(String.format("Unknown field(%s)!", field));
         }

         return this;
      }

      public DateHelper firstDayOfWeek(int value) {
         m_cal.setFirstDayOfWeek(value);
         return this;
      }

      public int hour() {
         return m_cal.get(Calendar.HOUR_OF_DAY);
      }

      public DateHelper hour(int delta) {
         m_cal.add(Calendar.HOUR, delta);
         return this;
      }

      public int minute() {
         return m_cal.get(Calendar.MINUTE);
      }

      public DateHelper minute(int delta) {
         m_cal.add(Calendar.MINUTE, delta);
         return this;
      }
      
      public int month() {
         return m_cal.get(Calendar.MONTH);
      }

      public DateHelper month(int delta) {
         m_cal.add(Calendar.MONTH, delta);
         return this;
      }

      public int second() {
         return m_cal.get(Calendar.SECOND);
      }

      public DateHelper second(int delta) {
         m_cal.add(Calendar.SECOND, delta);
         return this;
      }
      
      public int year() {
         return m_cal.get(Calendar.YEAR);
      }

      public DateHelper year(int delta) {
         m_cal.add(Calendar.YEAR, delta);
         return this;
      }
   }
}
