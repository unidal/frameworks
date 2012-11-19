package org.unidal.dal.jdbc.entity;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.test.user.dal.UserEntity;
import org.unidal.test.user.dal.invalid.DuplicatedEntity;
import org.unidal.test.user.dal.invalid.NoAnnotatedEntity;

public class EntityManagerTest extends ComponentTestCase {
	@Test
	public void testRegisterWithAnnotation() throws Exception {
		EntityInfoManager manager = lookup(EntityInfoManager.class);

		manager.register(UserEntity.class);
		Assert.assertNotNull(manager.getEntityInfo(UserEntity.class));
		Assert.assertNotNull(manager.getEntityInfo("user"));

		try {
			manager.register(DuplicatedEntity.class);

			Assert.fail("Logical name should be unique");
		} catch (RuntimeException e) {
			// expected
		}

		try {
			manager.getEntityInfo("unknown");

			Assert.fail("Entity should be registered before be used");
		} catch (RuntimeException e) {
			// expected
		}
	}

	@Test
	public void testRegisterWithoutAnnotation() throws Exception {
		EntityInfoManager manager = lookup(EntityInfoManager.class);

		try {
			manager.register(NoAnnotatedEntity.class);

			Assert.fail("Entity should be annotated");
		} catch (RuntimeException e) {
			// expected
		}

	}
}
