package io.odinjector.testclasses;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;

public class MyCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(InterfaceForClassWithNonRecursiveHierarchialContext.class).to(ClassWithNonRecursiveHierarchialContext.class);
		binder.bind(Hierarchy.class).to(HierarchyImpl.class);
		binder.bind(TestInterface1.class).to(TestImpl1.class);
	}
}
