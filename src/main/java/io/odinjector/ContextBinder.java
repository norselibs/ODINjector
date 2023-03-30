package io.odinjector;

import io.odinjector.binding.*;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;

public class ContextBinder implements Binder {
	private final BindingContext context;

	public ContextBinder(BindingContext context) {
		this.context = context;
	}

	@Override
	public <T> BindingTo<T> bind(Class<T> fromClass) {
		return new BindingToImpl<T>(context, BindingKey.get(fromClass));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void injectionListener(BindingListener listener) {
		context.addListener(listener);
	}

	@Override
	public void bindPackageToContext(Package aPackage) {
		context.addPackageBinding(aPackage);
	}

	@Override
	public void bindingResultListener(BindingResultListener listener) {
		context.addBindingResultListener(listener);
	}

	@Override
	public <T> BindingTo<T> bind(BindingKey<T> bindingKey) {
		return new BindingToImpl<T>(context, bindingKey);
	}

	private static class BindingToImpl<T> implements BindingTo<T> {
		private final BindingContext context;
		private boolean setAsSingleton = false;
		private final BindingKey<T> fromClass;
		public BindingToImpl(BindingContext context, BindingKey<T> fromClass) {
			this.context = context;
			this.fromClass = fromClass;
		}

		@Override
		public void to(Class<? extends T> toClass) {
			context.add(fromClass, Collections.singletonList(ClassBinding.of(BindingKey.get(toClass), setAsSingleton)));
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public void to(Provider<? extends T> provider) {
			context.add(fromClass, Collections.singletonList(ProviderBinding.of((Provider)provider, fromClass, setAsSingleton)));
		}


		@Override
		public void to(Provides<? extends T> provides) {
			context.add(fromClass, Collections.singletonList(ProviderBinding.of(provides, fromClass, setAsSingleton)));
		}

		@Override
		public BindingTo<T> asSingleton() {
			this.setAsSingleton = true;
			return this;
		}

		@Override
		public void add(Class<? extends T> toClass) {
			context.addIfAbsent(fromClass, () -> ClassBinding.of(BindingKey.get(toClass), setAsSingleton));
		}

	}
}
