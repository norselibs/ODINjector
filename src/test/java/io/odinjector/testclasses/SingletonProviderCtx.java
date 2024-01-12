package io.odinjector.testclasses;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;

public class SingletonProviderCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(Hierarchy.class).asSingleton().to(this::hierarchy);
	}

	private Hierarchy hierarchy() {
		return new HierarchyImpl(new SingletonImpl());
	}
}
