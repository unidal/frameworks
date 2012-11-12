package org.unidal.eunit.testfwk.spi.event;

import java.lang.reflect.AnnotatedElement;

public class Event {
   private EventType m_type;

   private AnnotatedElement m_source;

   public Event(EventType type, AnnotatedElement source) {
      m_type = type;
      m_source = source;
   }

   public AnnotatedElement getSource() {
      return m_source;
   }

   public EventType getType() {
      return m_type;
   }
}
