package io.odinjector.testclasses;

import io.odinjector.Binder;
import io.odinjector.BindingContext;

public class MyOtherAltCtx extends BindingContext {
	@Override
	public void configure(Binder binder) {
		binder.bind(TestInterface1.class).to(TestImpl3.class);
	}
}
