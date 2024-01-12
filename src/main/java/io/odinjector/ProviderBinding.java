package io.odinjector;

import io.odinjector.binding.Binding;
import io.odinjector.binding.BindingContext;
import io.odinjector.binding.BindingKey;
import io.odinjector.injection.InjectionContext;

import javax.inject.Provider;

public class ProviderBinding<T> implements Binding<T> {
	private Provider<T> provider;
	private Provides<? extends T> provides;
	private final boolean setAsSingleton;
	private final BindingKey<T> clazz;

	private ProviderBinding(Provider<T> provider, boolean setAsSingleton, BindingKey<T> clazz) {
		this.provider = provider;
		this.setAsSingleton = setAsSingleton;
		this.clazz = clazz;
	}

	private ProviderBinding(Provides<? extends T> provides, boolean setAsSingleton, BindingKey<T> clazz) {
		this.provides = provides;
		this.setAsSingleton = setAsSingleton;
		this.clazz = clazz;
	}

	public static <C> ProviderBinding<C> of(Provider<C> provider, BindingKey<C> clazz) {
		return new ProviderBinding<>(provider, false, clazz);
	}

	public static <C> ProviderBinding<C> of(Provider<C> provider, BindingKey<C> clazz, boolean setAsSingleton) {
		return new ProviderBinding<>(provider, setAsSingleton, clazz);
	}

	public static <C> ProviderBinding<C> of(Provides<? extends C> provider, BindingKey<C> clazz, boolean setAsSingleton) {
		return new ProviderBinding<>(provider, setAsSingleton, clazz);
	}

	@Override
	public Provider<T> getProvider(BindingContext context, InjectionContext<T> thisInjectionContext, OdinJector injector) {
		if (provider != null) {
			return provider;
		}
		if (provides != null) {
			return () -> provides.call(injector);
		}
		throw new RuntimeException();
	}

	@Override
	public Class<T> getElementClass() {
		return clazz.getBoundClass();
	}

	@Override
	public boolean isSingleton() {
		return setAsSingleton;
	}

	@Override
	public boolean isInterface() {
		return false;
	}
}

