package org.unidal.web.mvc.payload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.unidal.helper.Joiners;
import org.unidal.lookup.annotation.InjectAttribute;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

@Named(type = ParameterProvider.class, value = "multipart/form-data", instantiationStrategy = Named.PER_LOOKUP)
public class MultipartParameterProvider implements ParameterProvider, LogEnabled {
   @InjectAttribute
   private int m_sizeThreshold = 100 * 1024; // 100K

   @InjectAttribute
   private int m_maxUploadFileSize = 5 * 1024 * 1024; // 5M

   private Map<String, List<String>> m_parameters = new HashMap<String, List<String>>();

   private Map<String, FileItem> m_files = new HashMap<String, FileItem>();

   private Logger m_logger;

   private HttpServletRequest m_request;

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   @Override
   public InputStream getFile(String name) throws IOException {
      FileItem file = m_files.get(name);

      if (file == null) {
         return null;
      } else {
         return new ItemStream(file);
      }
   }

   @Override
   public String getModuleName() {
      final String path = m_request.getServletPath();

      if (path != null && path.length() > 0) {
         int index = path.indexOf('/', 1);

         if (index > 0) {
            return path.substring(1, index);
         } else {
            return path.substring(1);
         }
      }

      return "default";
   }

   @Override
   public String getParameter(String name) {
      List<String> values = m_parameters.get(name);

      if (values == null) {
         return null;
      } else {
         return Joiners.by(',').join(values);
      }
   }

   @Override
   public String[] getParameterNames() {
      int size = m_parameters.size();
      String[] names = new String[size];
      int index = 0;

      for (String name : m_parameters.keySet()) {
         names[index++] = name;
      }

      return names;
   }

   @Override
   public String[] getParameterValues(String name) {
      List<String> values = m_parameters.get(name);

      if (values == null) {
         return null;
      } else {
         return values.toArray(new String[0]);
      }
   }

   @Override
   public HttpServletRequest getRequest() {
      return m_request;
   }

   private void initialize(HttpServletRequest request) {
      DiskFileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      File tmpDir = new File(System.getProperty("java.io.tmpdir"));
      File repository = new File(tmpDir, "upload");

      repository.mkdirs();
      factory.setRepository(repository);
      factory.setSizeThreshold(m_sizeThreshold);
      upload.setSizeMax(m_maxUploadFileSize);

      try {
         List<FileItem> items = upload.parseRequest(request);

         for (FileItem item : items) {
            String name = item.getFieldName();

            if (item.isFormField()) {
               List<String> values = m_parameters.get(name);
               String value = item.getString();

               if (values == null) {
                  values = new ArrayList<String>(3);
                  m_parameters.put(name, values);
               }

               values.add(value);
            } else {
               m_files.put(name, item);
            }
         }
      } catch (SizeLimitExceededException e) {
         m_logger.warn("Uplaod file size exceeds the limit: " + m_maxUploadFileSize + " byte.", e);
      } catch (FileUploadException e) {
         m_logger.error("Error when uploading file.", e);
      }

      processQueryString();
   }

   @SuppressWarnings("deprecation")
   private void processQueryString() {
      String qs = m_request.getQueryString();

      if (qs != null) {
         String[] pairs = qs.split(Pattern.quote("&"));
         Set<String> added = new HashSet<String>();

         for (String pair : pairs) {
            int pos = pair.indexOf('=');

            if (pos > 0) {
               String name = pair.substring(0, pos);
               String value = pair.substring(pos + 1);

               if (!m_parameters.containsKey(name) || added.contains(name)) {
                  List<String> values = m_parameters.get(name);

                  if (values == null) {
                     values = new ArrayList<String>(3);
                     m_parameters.put(name, values);
                  }

                  try {
                     values.add(URLDecoder.decode(value, "utf-8"));
                  } catch (UnsupportedEncodingException e) {
                     values.add(URLDecoder.decode(value));
                  }

                  added.add(name);
               }
            }
         }
      }
   }

   public void setMaxUploadFileSize(int maxUploadFileSize) {
      m_maxUploadFileSize = maxUploadFileSize;
   }

   @Override
   public MultipartParameterProvider setRequest(HttpServletRequest request) {
      m_request = request;

      initialize(request);
      return this;
   }

   public void setSizeThreshold(int sizeThreshold) {
      m_sizeThreshold = sizeThreshold;
   }

   public static final class ItemStream extends InputStream {
      private FileItem m_file;

      private InputStream m_stream;

      public ItemStream(FileItem file) throws IOException {
         m_file = file;
         m_stream = file.getInputStream();
      }

      @Override
      public void close() throws IOException {
         m_stream.close();
      }

      @Override
      public int read() throws IOException {
         return m_stream.read();
      }

      @Override
      public int read(byte[] b) throws IOException {
         return m_stream.read(b);
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException {
         return m_stream.read(b, off, len);
      }

      @Override
      public String toString() {
         return m_file.getName();
      }

      public void write(File file) throws Exception {
         m_file.write(file);
      }
   }
}
