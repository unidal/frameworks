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

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import com.site.lookup.annotation.Inject;
import com.site.lookup.util.StringUtils;

public class MultipartParameterProvider implements ParameterProvider, LogEnabled {
   @Inject
   private int m_sizeThreshold = 100 * 1024; // 100K

   @Inject
   private int m_maxUploadFileSize = 5 * 1024 * 1024; // 5M

   private Map<String, List<String>> m_parameters = new HashMap<String, List<String>>();

   private Map<String, DiskFileItem> m_files = new HashMap<String, DiskFileItem>();

   private Logger m_logger;

   private HttpServletRequest m_request;

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public InputStream getFile(String name) throws IOException {
      DiskFileItem file = m_files.get(name);

      if (file == null) {
         return null;
      } else {
         return new ItemStream(file);
      }
   }

   public String getParameter(String name) {
      List<String> values = m_parameters.get(name);

      if (values == null) {
         return null;
      } else {
         return StringUtils.join(values, ",");
      }
   }

   public String[] getParameterNames() {
      int size = m_parameters.size();
      String[] names = new String[size];
      int index = 0;

      for (String name : m_parameters.keySet()) {
         names[index++] = name;
      }

      return names;
   }

   public String[] getParameterValues(String name) {
      List<String> values = m_parameters.get(name);

      if (values == null) {
         return null;
      } else {
         return values.toArray(new String[0]);
      }
   }

   public HttpServletRequest getRequest() {
      return m_request;
   }

   @SuppressWarnings("unchecked")
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
         List<DiskFileItem> items = upload.parseRequest(request);

         for (DiskFileItem item : items) {
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

   public void setRequest(HttpServletRequest request) {
      m_request = request;

      initialize(request);
   }

   public void setSizeThreshold(int sizeThreshold) {
      m_sizeThreshold = sizeThreshold;
   }

   public static final class ItemStream extends InputStream {
      private DiskFileItem m_file;

      private InputStream m_stream;

      public ItemStream(DiskFileItem file) throws IOException {
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

      public void write(File file) throws Exception {
         m_file.write(file);
      }
   }
}
