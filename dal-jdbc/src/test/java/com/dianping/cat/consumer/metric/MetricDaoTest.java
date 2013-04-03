package com.dianping.cat.consumer.metric;

import java.util.Date;

import org.junit.Test;
import org.unidal.dal.jdbc.cat.metrics.BusinessReport;
import org.unidal.dal.jdbc.cat.metrics.BusinessReportDao;
import org.unidal.lookup.ComponentTestCase;

public class MetricDaoTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      BusinessReportDao dao = lookup(BusinessReportDao.class);
      BusinessReport r = dao.createLocal();

      r.setName("metric");
      r.setGroup("group");
      r.setPeriod(new Date());
      r.setIp("127.0.0.1");
      r.setType(1);
      r.setContent("content".getBytes());
      r.setCreationDate(new Date());

      dao.insert(r);
   }
}