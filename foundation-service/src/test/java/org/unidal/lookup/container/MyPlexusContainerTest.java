package org.unidal.lookup.container;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.PlexusContainer;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Contextualizable;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.lookup.extension.RoleHintEnabled;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;
import org.unidal.lookup.logging.LoggerManager;

public class MyPlexusContainerTest {
	private MyPlexusContainer m_container;

	@Before
	public void before() throws Exception {
		m_container = new MyPlexusContainer(getClass().getResourceAsStream(getClass().getSimpleName() + ".xml"));
	}

	// container
	@Test
	public void testContainer01() throws Exception {
		m_container.addContextValue("key", "Value");

		Assert.assertEquals("Value", m_container.getContext().get("key"));
	}

	// lookup LoggerManager.class
	@Test
	public void testContainer02() throws Exception {
		LoggerManager loggerManager = m_container.lookup(LoggerManager.class);

		Assert.assertNotNull(loggerManager);
		Assert.assertNotNull(loggerManager.getLoggerForComponent(""));
	}
	
	// lookup PlexusContainer.class
	@Test
	public void testContainer04() throws Exception {
		PlexusContainer container = m_container.lookup(PlexusContainer.class);
		
		Assert.assertNotNull(container);
	}

	// release component
	@Test
	public void testContainer03() throws Exception {
		I1 a1 = m_container.lookup(I1.class, "singleton");
		I1 b1 = m_container.lookup(I1.class, "per-lookup");
		I1 c1 = m_container.lookup(I1.class, "F1");

		m_container.release(a1);
		m_container.release(b1);
		m_container.release(c1);

		I1 a2 = m_container.lookup(I1.class, "singleton");
		I1 b2 = m_container.lookup(I1.class, "per-lookup");
		I1 c2 = m_container.lookup(I1.class, "F1");

		Assert.assertSame(a1, a2);
		Assert.assertNotSame(b1, b2);
		Assert.assertSame(c1, c2);
	}

	// singleton instantiation strategy
	@Test
	public void testLookup11() throws Exception {
		I1 a = m_container.lookup(I1.class, "singleton");
		I1 b = m_container.lookup(I1.class, "singleton");

		Assert.assertEquals(C11.class, a.getClass());
		Assert.assertSame(a, b);
	}

	// per-lookup instantiation strategy
	@Test
	public void testLookup12() throws Exception {
		I1 a = m_container.lookup(I1.class, "per-lookup");
		I1 b = m_container.lookup(I1.class, "per-lookup");

		Assert.assertEquals(C12.class, a.getClass());
		Assert.assertNotSame(a, b);
	}

	// enum instantiation strategy
	@Test
	public void testLookup13() throws Exception {
		I1 a = m_container.lookup(I1.class, "F1");
		I1 b = m_container.lookup(I1.class, "F2");

		Assert.assertEquals(E1.F1, a);
		Assert.assertEquals(E1.F2, b);
	}

	// enable logger, role hint, contextualizable
	@Test
	public void testLookup14() throws Exception {
		I1 a = m_container.lookup(I1.class, "logger:role-hint");

		Assert.assertEquals(C13.class, a.getClass());
		Assert.assertEquals("LRCI", ((C13) a).m_sb.toString());

		Assert.assertEquals("logger:role-hint", ((C13) a).m_roleHint);
		Assert.assertNotNull(((C13) a).m_logger);
		Assert.assertNotNull(((C13) a).m_container);
	}

	// inject dependency
	@Test
	public void testLookup21() throws Exception {
		I2 a = m_container.lookup(I2.class, "one");

		Assert.assertEquals(C21.class, a.getClass());
		Assert.assertEquals(C11.class, ((C21) a).m_a.getClass());
	}

	// inject two dependencies
	@Test
	public void testLookup22() throws Exception {
		I2 a = m_container.lookup(I2.class, "two");
		I2 b = m_container.lookup(I2.class, "two");

		Assert.assertEquals(C22.class, a.getClass());
		Assert.assertEquals(C11.class, ((C22) a).m_a.getClass());
		Assert.assertEquals(C12.class, ((C22) a).m_b.getClass());
		Assert.assertSame(((C22) a).m_a, ((C22) b).m_a);
		Assert.assertNotSame(((C22) a).m_b, ((C22) b).m_b);
	}

	// inject dependency with implementation
	@Test
	public void testLookup23() throws Exception {
		I2 a = m_container.lookup(I2.class, "three");

		Assert.assertEquals(C23.class, a.getClass());
		Assert.assertEquals(C11.class, ((C23) a).m_a.getClass());
	}

	// inject dependency with configuration
	@Test
	public void testLookup24() throws Exception {
		I2 a = m_container.lookup(I2.class, "four");

		Assert.assertEquals(C24.class, a.getClass());
		Assert.assertEquals(255, ((C24) a).m_value);
		Assert.assertEquals("string-value", ((C24) a).m_stringValue);
	}

	// inject dependency with multiple roles
	@Test
	public void testLookup25() throws Exception {
		I2 a = m_container.lookup(I2.class, "five");

		Assert.assertEquals(C25.class, a.getClass());
		Assert.assertEquals("[F1, F2]", ((C25) a).m_a.toString());
	}
	
	// inject dependency with multiple roles
	@Test
	public void testLookup26() throws Exception {
		I2 a = m_container.lookup(I2.class, "six");
		
		Assert.assertEquals(C26.class, a.getClass());
		Assert.assertEquals("MyPlexusContainer", ((C26) a).m_container.toString());
		Assert.assertEquals("TimedConsoleLoggerManager", ((C26) a).m_loggerManager.toString());
		Assert.assertEquals("TimedConsoleLogger", ((C26) a).m_logger.toString());
	}

	// lookup list
	@Test
	public void testLookup31() throws Exception {
		List<I2> a = m_container.lookupList(I2.class);

		Assert.assertEquals(6, a.size());
		Assert.assertEquals("[C21, C22, C23, C24, C25, C26]", a.toString());
	}

	// lookup list
	@Test
	public void testLookup32() throws Exception {
		Map<String, I2> a = m_container.lookupMap(I2.class);

		Assert.assertEquals(6, a.size());
		Assert.assertEquals("{one=C21, two=C22, three=C23, four=C24, five=C25, six=C26}", a.toString());
	}

	@Named(type = I1.class, value = "singleton")
	public static class C11 implements I1 {
	}

	@Named(type = I1.class, value = "per-lookup", instantiationStrategy = Named.PER_LOOKUP)
	public static class C12 implements I1 {
	}

	@Named(type = I1.class, value = "logger:role-hint")
	public static class C13 implements I1, LogEnabled, RoleHintEnabled, Contextualizable, Initializable {
		private Logger m_logger;

		private String m_roleHint;

		private PlexusContainer m_container;

		private StringBuilder m_sb = new StringBuilder();

		@Override
		public void enableLogging(Logger logger) {
			m_logger = logger;
			m_sb.append("L");
		}

		@Override
		public void enableRoleHint(String roleHint) {
			m_roleHint = roleHint;
			m_sb.append("R");
		}

		@Override
		public void contextualize(Map<String, Object> context) {
			m_container = (PlexusContainer) context.get("plexus");

			context.put("first", "First");
			m_sb.append("C");

			Assert.assertEquals(true, context.containsKey("first"));
			Assert.assertEquals("First", context.get("first"));
		}

		@Override
		public void initialize() throws InitializationException {
			m_sb.append("I");
		}
	}

	@Named(type = I2.class, value = "one")
	public static class C21 implements I2 {
		@Inject("singleton")
		private I1 m_a;

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	@Named(type = I2.class, value = "two", instantiationStrategy = Named.PER_LOOKUP)
	public static class C22 implements I2 {
		@Inject("singleton")
		private I1 m_a;

		@Inject("per-lookup")
		private I1 m_b;

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	@Named(type = I2.class, value = "three")
	public static class C23 implements I2 {
		@Inject(type = I1.class, value = "singleton")
		private C11 m_a;

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	@Named(type = I2.class, value = "four")
	public static class C24 implements I2 {
		private int m_value;

		private String m_stringValue;

		public void setBoolean(boolean b) {
			m_value += 1;
		}

		public void setByte(byte b) {
			m_value += 2;
		}

		public void setChar(char s) {
			m_value += 4;
		}

		public void setShort(short s) {
			m_value += 8;
		}

		public void setInt(int i) {
			m_value += 16;
		}

		public void setLong(long l) {
			m_value += 32;
		}

		public void setFloat(float f) {
			m_value += 64;
		}

		public void setDouble(double d) {
			m_value += 128;
		}

		public void setStringValue(String stringValue) {
			m_stringValue = stringValue;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	@Named(type = I2.class, value = "five")
	public static class C25 implements I2 {
		@Inject(type = I1.class, value = { "F1", "F2" })
		private List<I1> m_a;

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}
	
	@Named(type = I2.class, value = "six")
	public static class C26 implements I2 {
		@Inject
		private PlexusContainer m_container;
		
		@Inject
		private LoggerManager m_loggerManager;
		
		@Inject
		private Logger m_logger;
		
		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	@Named(type = I1.class)
	public static enum E1 implements I1 {
		F1, F2;
	}

	public static interface I1 {
	}

	public static interface I2 {
	}
}
