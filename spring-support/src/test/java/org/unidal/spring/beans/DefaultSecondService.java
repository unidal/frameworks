package org.unidal.spring.beans;

import org.springframework.stereotype.Service;
import org.unidal.spring.Trackable;

@Service("SecondService")
@Trackable
public class DefaultSecondService implements ISecondService {
   @Override
   public void greeting(String message) {
//      System.out.println(message);
   }
}
