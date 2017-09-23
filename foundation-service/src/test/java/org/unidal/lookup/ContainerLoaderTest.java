package org.unidal.lookup;

import org.junit.Assert;
import org.junit.Test;

public class ContainerLoaderTest {
	@Test
	public void test() {
		PlexusContainer c1 = ContainerLoader.getDefaultContainer();
		PlexusContainer c2 = ContainerLoader.getDefaultContainer(null);
		PlexusContainer c3 = ContainerLoader.getDefaultContainer();

		Assert.assertSame(c1, c2);
		Assert.assertSame(c1, c3);
	}
}
