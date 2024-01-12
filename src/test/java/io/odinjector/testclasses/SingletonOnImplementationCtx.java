package io.odinjector.testclasses;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;

public class SingletonOnImplementationCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(Hierarchy.class).asSingleton().to(HierarchyImpl.class);
		binder.bind(HierarchyImpl.class).asSingleton().to(HierarchyImpl.class);
		binder.bind(TestInterface1.class).to(SingletonImpl.class);
	}
}
