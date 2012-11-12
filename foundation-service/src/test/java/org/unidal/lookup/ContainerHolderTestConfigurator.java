package org.unidal.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.unidal.lookup.ContainerHolderTest.BadCollectionHolder;
import org.unidal.lookup.ContainerHolderTest.BadObject;
import org.unidal.lookup.ContainerHolderTest.BadObjectHolder;
import org.unidal.lookup.ContainerHolderTest.MockContainer;
import org.unidal.lookup.ContainerHolderTest.MockInterface;
import org.unidal.lookup.ContainerHolderTest.MockObject;
import org.unidal.lookup.ContainerHolderTest.MockObject2;
import org.unidal.lookup.ContainerHolderTest.MockObject3;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public class ContainerHolderTestConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ContainerHolderTestConfigurator());
	}

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(MockContainer.class));

		// for normal cases
		all.add(C(MockInterface.class, MockObject.class));
		all.add(C(MockInterface.class, "secondary", MockObject2.class));
		all.add(C(MockInterface.class, "third", MockObject3.class));

		// for exception cases
		all.add(C(BadObject.class));
		all.add(C(BadObjectHolder.class).req(BadObject.class));

		all.add(C(Queue.class, "non-blocking", LinkedList.class));
		all.add(C(Queue.class, "blocking", LinkedBlockingQueue.class));
		all.add(C(List.class, "array", ArrayList.class));
		all.add(C(Map.class, "hash", HashMap.class));

		all.addAll(defineComponent(BadCollectionHolder.class));

		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return ContainerHolderTest.class;
	}
}
