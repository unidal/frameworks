package org.unidal.spring.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidal.spring.Trackable;

@Service("FirstService")
@Trackable
public class DefaultFirstService implements IFirstService {
   @Autowired
   private ISecondService m_secondSerivce;

   @Autowired
   private IThirdService m_thirdSerivce;

   @Override
   public String introduction() {
      return "This is the first service.";
   }

   @Override
   public void greeting(String message) {
      m_secondSerivce.greeting(message);
   }

   @Override
   public boolean toggle(int threshold) {
      return m_thirdSerivce.toggle(threshold);
   }
}
