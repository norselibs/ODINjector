package io.odinjector.testclasses;

import io.odinjector.Binder;
import io.odinjector.BindingContext;

import javax.inject.Provider;

public class ProviderCtx extends BindingContext {

	@Override
	public void configure(Binder binder) {
		binder.bind(TestInterface1.class).to(TestImpl2::new);
	}
}
