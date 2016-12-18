package org.unidal.web.mvc.view.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ViewModel;
import org.unidal.web.mvc.view.annotation.AttributeMeta;
import org.unidal.web.mvc.view.annotation.ElementMeta;
import org.unidal.web.mvc.view.annotation.EntityMeta;
import org.unidal.web.mvc.view.annotation.ModelMeta;
import org.unidal.web.mvc.view.annotation.PojoMeta;

@Named(type = ModelHandler.class)
public class DefaultModelHandler implements ModelHandler {
   @Inject(XmlModelBuilder.ID)
   private ModelBuilder m_xmlBuilder;

   @Inject(JsonModelBuilder.ID)
   private ModelBuilder m_jsonBuilder;

   private Map<Class<?>, ModelDescriptor> m_map = new HashMap<Class<?>, ModelDescriptor>();

   @Override
   public void handle(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      String forceDownload = req.getParameter("forceDownload");
      ViewModel<?, ?, ?> model = (ViewModel<?, ?, ?>) req.getAttribute("model");
      ActionContext<?> ctx = (ActionContext<?>) req.getAttribute("ctx");

      if ("xml".equals(forceDownload)) {
         handleXmlDownload(res, model, ctx);
      } else if ("json".equals(forceDownload)) {
         handleJsonDownload(res, model, ctx);
      }
   }

   protected void handleJsonDownload(HttpServletResponse res, ViewModel<?, ?, ?> model, ActionContext<?> ctx)
         throws IOException {
      Class<?> clazz = model.getClass();
      ModelDescriptor descriptor = m_map.get(clazz);

      if (descriptor == null) {
         descriptor = new AnnotationModelDescriptor(clazz);
         m_map.put(clazz, descriptor);
      }

      if (descriptor.getModelName() != null) {
         String json = m_jsonBuilder.build(descriptor, model);
         byte[] data = json.getBytes("utf-8");

         res.setContentType("application/json; charset=utf-8");
         res.setContentLength(data.length);
         res.getOutputStream().write(data);

         ctx.stopProcess();
      }
   }

   protected void handleXmlDownload(HttpServletResponse res, ViewModel<?, ?, ?> model, ActionContext<?> ctx)
         throws IOException {
      Class<?> clazz = model.getClass();
      ModelDescriptor descriptor = m_map.get(clazz);

      if (descriptor == null) {
         descriptor = new AnnotationModelDescriptor(clazz);
         m_map.put(clazz, descriptor);
      }

      if (descriptor.getModelName() != null) {
         String xml = m_xmlBuilder.build(descriptor, model);
         byte[] data = xml.getBytes("utf-8");

         res.setContentType("text/xml; charset=utf-8");
         res.setContentLength(data.length);
         res.getOutputStream().write(data);

         ctx.stopProcess();
      }
   }

   protected static class AnnotationModelDescriptor implements ModelDescriptor {
      private Class<?> m_clazz;

      private String m_modelName;

      private List<Field> m_attributeFields = new ArrayList<Field>();

      private List<Field> m_elementFields = new ArrayList<Field>();

      private List<Field> m_entityFields = new ArrayList<Field>();

      private List<Field> m_pojoFields = new ArrayList<Field>();

      public AnnotationModelDescriptor(Class<?> clazz) {
         m_clazz = clazz;
         initialize();
      }

      @Override
      public List<Field> getAttributeFields() {
         return m_attributeFields;
      }

      @Override
      public List<Field> getElementFields() {
         return m_elementFields;
      }

      @Override
      public List<Field> getEntityFields() {
         return m_entityFields;
      }

      @Override
      public String getModelName() {
         return m_modelName;
      }

      @Override
      public List<Field> getPojoFields() {
         return m_pojoFields;
      }

      private void initialize() {
         ModelMeta model = m_clazz.getAnnotation(ModelMeta.class);

         if (model != null) {
            Class<?> clazz = m_clazz;

            m_modelName = model.value();

            while (clazz != null && clazz != Object.class) {
               for (Field field : clazz.getDeclaredFields()) {
                  AttributeMeta attribute = field.getAnnotation(AttributeMeta.class);
                  ElementMeta element = field.getAnnotation(ElementMeta.class);
                  EntityMeta entity = field.getAnnotation(EntityMeta.class);
                  PojoMeta pojo = field.getAnnotation(PojoMeta.class);
                  int count = (attribute == null ? 0 : 1) + (element == null ? 0 : 1) + (entity == null ? 0 : 1)
                        + (pojo == null ? 0 : 1);

                  if (count > 1) {
                     throw new RuntimeException(String.format(
                           "Only one of %s, %s, %s or %s could be annotated to a model field!",
                           AttributeMeta.class.getSimpleName(), ElementMeta.class.getSimpleName(),
                           EntityMeta.class.getSimpleName(), PojoMeta.class.getSimpleName()));
                  }

                  if (attribute != null) {
                     m_attributeFields.add(field);
                  } else if (element != null) {
                     m_elementFields.add(field);
                  } else if (entity != null) {
                     m_entityFields.add(field);
                  } else if (pojo != null) {
                     m_pojoFields.add(field);
                  }
               }

               clazz = clazz.getSuperclass();
            }
         }
      }
   }
}
