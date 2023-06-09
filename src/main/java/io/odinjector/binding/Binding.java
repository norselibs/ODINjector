package io.odinjector.binding;

import io.odinjector.OdinJector;
import io.odinjector.injection.InjectionContext;

import javax.inject.Provider;

public interface Binding<T> {
	Provider<T> getProvider(BindingContext context, InjectionContext<T> thisInjectionContext, OdinJector injector);
	Class<T> getElementClass();
	boolean isSingleton();
	boolean isInterface();
}
