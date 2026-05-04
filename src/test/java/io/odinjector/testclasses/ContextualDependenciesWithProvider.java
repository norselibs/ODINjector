package io.odinjector.testclasses;

import io.odinjector.ContextualInject;

import javax.inject.Inject;
import javax.inject.Provider;

@ContextualInject(MyAltCtx.class)
public class ContextualDependenciesWithProvider {
	private Provider<TestInterface1> dependency;

	@Inject
	public ContextualDependenciesWithProvider(Provider<TestInterface1> dependency) {
		this.dependency = dependency;
	}

	public TestInterface1 get() {
		return dependency.get();
	}
}
