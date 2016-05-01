package org.unidal.web.mvc.view.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.unidal.lookup.annotation.Named;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Named(type = ModelBuilder.class, value = "json")
public class JsonModelBuilder implements ModelBuilder {
   private FieldNamingStrategy m_fieldNamingStrategy = new FieldNamingStrategy() {
      @Override
      public String translateName(Field f) {
         String name = f.getName();

         if (name.startsWith("m_")) {
            return name.substring(2);
         } else {
            return name;
         }
      }
   };

   @Override
   public String build(ModelDescriptor descriptor, Object model) {
      Gson gson = new GsonBuilder() //
            .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()) //
            .registerTypeAdapter(Double.class, new DoubleTypeAdapter()) //
            .setDateFormat("yyyy-MM-dd HH:mm:ss") //
            .setFieldNamingStrategy(m_fieldNamingStrategy) //
            .setPrettyPrinting() //
            .create();

      return gson.toJson(model);
   }

   static class DoubleTypeAdapter implements JsonSerializer<Double> {
      @Override
      public JsonElement serialize(Double d, Type type, JsonSerializationContext context) {
         DecimalFormat format = new DecimalFormat("0.0000");
         String temp = format.format(d);
         JsonPrimitive pri = new JsonPrimitive(temp);

         return pri;
      }
   }

   static class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
      private DateFormat m_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
         }

         try {
            Date date = m_format.parse(json.getAsString());

            return new Timestamp(date.getTime());
         } catch (ParseException e) {
            throw new JsonParseException(e);
         }
      }

      public JsonElement serialize(Timestamp src, Type arg1, JsonSerializationContext arg2) {
         String str = m_format.format(src);

         return new JsonPrimitive(str);
      }
   }
}
