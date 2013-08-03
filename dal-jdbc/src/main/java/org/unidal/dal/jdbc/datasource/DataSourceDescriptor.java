package org.unidal.dal.jdbc.datasource;

import java.util.Map;

public interface DataSourceDescriptor {
   public String getId();

   public String getType();

   public Map<String, Object> getProperties();

   public String getProperty(String name, String defaultValue);

   public boolean getBooleanProperty(String name, boolean defaultValue);

   public double getDoubleProperty(String name, double defaultValue);

   public int getIntProperty(String name, int defaultValue);

   public long getLongProperty(String name, long defaultValue);
}
