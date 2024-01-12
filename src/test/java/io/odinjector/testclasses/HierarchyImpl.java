package io.odinjector.testclasses;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

public class HierarchyImpl implements Hierarchy {
	private TestInterface1 i;
	public static AtomicInteger invocations = new AtomicInteger();
	@Inject
	public HierarchyImpl(TestInterface1 i) {
		this.i = i;
		invocations.incrementAndGet();
	}

	@Override
	public TestInterface1 getTestInterface() {
		return i;
	}
}
