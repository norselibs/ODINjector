package io.odinjector.testclasses;

import io.odinjector.Binder;
import io.odinjector.BindingContext;

import javax.inject.Provider;

public class ProviderCtx extends BindingContext {

	@Override
	public void configure(Binder binder) {
		binder.bind(TestInterface1.class).to(new Provider<TestInterface1>() {
			@Override
			public TestInterface1 get() {
				return new TestImpl2();
			}
		});
	}
}
