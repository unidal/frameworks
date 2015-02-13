package org.unidal.web.mvc.model;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.Module;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.ErrorActionMeta;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.TransitionMeta;
import org.unidal.web.mvc.model.entity.ModuleModel;

public class ModuleManagerTest extends ComponentTestCase {
	@Test
	public void testBuild() throws Exception {
		ModelManager manager = lookup(ModelManager.class);
		ModuleModel module = manager.build(TestModule2.class.newInstance());

		Assert.assertEquals("test2", module.getModuleName());
		Assert.assertEquals("action1", module.getDefaultInboundActionName());
		Assert.assertEquals("default", module.getDefaultErrorActionName());
	}

	@Test
	public void testRegister() throws Exception {
		ModelManager registry = lookup(ModelManager.class);

		registry.register(TestModule1.class);
		registry.register(TestModule2.class);

		Assert.assertEquals("test1", registry.getFirstModule("test1").getModuleName());
		Assert.assertEquals("test2", registry.getFirstModule("test2").getModuleName());
		Assert.assertNull(registry.getFirstModule("test"));

		try {
			registry.register(TestModule3.class);
			Assert.fail("No transition and error methods defined");
		} catch (RuntimeException e) {
			// expected
		}

		try {
			registry.register(TestModule4.class);
			Assert.fail("Require transition and error methods defined");
		} catch (RuntimeException e) {
			// expected
		}

		registry.register(TestModule2Copy.class);

		ModuleModel m1 = registry.getModule("test2", "action1");
		ModuleModel m2 = registry.getModule("test2", "action2");
		ModuleModel m3 = registry.getModule("test2", "action3");

		Assert.assertSame(m1, m2);
		Assert.assertNotSame(m1, m3);
	}

	@ModuleMeta(name = "test1")
	public static final class TestModule1 extends AbstractModule {
	}

	@ModuleMeta(name = "test2", defaultInboundAction = "action1", defaultTransition = "default", defaultErrorAction = "default")
	public static final class TestModule2 extends AbstractModule {
		@TransitionMeta(name = "secondary")
		public void doSecondaryTransition(ActionContext<?> ctx) {
		}

		@TransitionMeta(name = "default")
		public void doTransition(ActionContext<?> ctx) {
		}

		@InboundActionMeta(name = "action1")
		public void inboundAction1(ActionContext<?> ctx) {
		}

		@InboundActionMeta(name = "action2", transition = "secondary", errorAction = "secondary")
		public void inboundAction2(ActionContext<?> ctx) {
		}

		@ErrorActionMeta(name = "default")
		public void onError(ActionContext<?> ctx) {
		}

		@ErrorActionMeta(name = "secondary")
		public void onSecondaryError(ActionContext<?> ctx) {
		}
	}

	@ModuleMeta(name = "test2")
	public static final class TestModule2Copy extends AbstractModule {
		@InboundActionMeta(name = "action3")
		public void inboundAction1(ActionContext<?> ctx) {
		}
	}

	@ModuleMeta(name = "test3", defaultTransition = "missing", defaultErrorAction = "missing")
	public static final class TestModule3 extends AbstractModule implements Module {
	}

	@ModuleMeta(name = "test4")
	public static final class TestModule4 implements Module {
		@InboundActionMeta(name = "action1")
		public void inboundAction1(ActionContext<?> ctx) {
		}

		@Override
		public Class<? extends PageHandler<?>>[] getPageHandlers() {
			return null;
		}
	}
}
