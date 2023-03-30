package io.odinjector.testclasses;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;

public class MyOtherAltCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(TestInterface1.class).to(TestImpl3.class);
	}
}
