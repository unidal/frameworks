package org.unidal.dal.jdbc;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class BizObjectHelper {
   @SuppressWarnings("unchecked")
   private static <T extends BizObject> Constructor<T> getBoConstructor(Class<T> boClass) {
      try {
         Constructor<T>[] cs = (Constructor<T>[]) boClass.getConstructors();

         for (int i = 0; i < cs.length; i++) {
            Class<?>[] paramTypes = cs[i].getParameterTypes();

            if (paramTypes.length == 1 && DataObject.class.isAssignableFrom(paramTypes[0])) {
               return cs[i];
            }
         }
      } catch (Exception e) {
         // ignore it
      }

      throw new RuntimeException("Can't find a constructor for creating a BO instance from " + boClass);
   }

   public static <S extends DataObject, T extends BizObject> List<S> unwrap(List<T> bos, Class<S> doClass) {
      List<S> dos = new ArrayList<S>(bos.size());

      for (T bo : bos) {
         dos.add(unwrap(bo, doClass));
      }

      return dos;
   }

   @SuppressWarnings("unchecked")
   public static <S extends DataObject, T extends BizObject> S unwrap(T bo, Class<S> doClass) {
      return (S) bo.getDo();
   }

   public static <S extends DataObject, T extends BizObject> List<T> wrap(List<S> rows, Class<T> boClass) {
      List<T> bos = new ArrayList<T>(rows.size());

      for (S row : rows) {
         bos.add(wrap(row, boClass));
      }

      return bos;
   }

   public static <S extends DataObject, T extends BizObject> T wrap(S row, Class<T> boClass) {
      try {
         Constructor<T> c = getBoConstructor(boClass);

         return c.newInstance(new Object[] { row });
      } catch (Exception e) {
         throw new RuntimeException("Can't create a BO instance from " + boClass, e);
      }
   }
}