package io.odinjector.testclasses;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;

public class MyAltCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(Hierarchy.class).to(AltHierarchyImpl.class);
		binder.bind(TestInterface1.class).to(TestImpl2.class);
	}
}
