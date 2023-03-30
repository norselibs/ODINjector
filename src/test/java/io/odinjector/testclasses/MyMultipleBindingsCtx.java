package io.odinjector.testclasses;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;

public class MyMultipleBindingsCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(TestInterface1.class).add(TestImpl1.class);
		binder.bind(TestInterface1.class).add(TestImpl2.class);
		binder.bind(TestInterface1.class).add(TestImpl3.class);
	}
}
