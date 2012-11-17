package org.unidal.dal.jdbc.raw;

import org.unidal.dal.jdbc.DataField;
import org.unidal.dal.jdbc.Readset;
import org.unidal.dal.jdbc.Updateset;
import org.unidal.dal.jdbc.annotation.Entity;

@Entity(logicalName = "raw", alias = "raw")
public class RawEntity {
   public static final Readset<RawDataObject> READSET_FULL = new Readset<RawDataObject>(new DataField[0]);

   public static final Updateset<RawDataObject> UPDATESET_FULL = new Updateset<RawDataObject>(new DataField[0]);
}