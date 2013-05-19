package org.unidal.spring.beans;

import org.springframework.stereotype.Service;
import org.unidal.spring.Trackable;

@Service("ThirdService")
@Trackable
public class DefaultThirdService implements IThirdService {
   @Override
   public boolean toggle(int threshold) {
      return threshold > 0;
   }
}
