package io.odinjector.binding;

import io.odinjector.Provides;

import javax.inject.Provider;

public interface BindingTo<T> {
	/**
	 * Binds toClass to the binding key supplied
	 * @param toClass
	 */
	void to(Class<? extends T> toClass);
	/**
	 * Binds the provider toProvider to the binding key supplied
	 * @param toProvider
	 */
	void to(Provider<? extends T> toProvider);
	/**
	 * Binds the provides configuration to the binding key supplied
	 * @param provides
	 */
	void to(Provides<? extends T> provides);

	/**
	 * register this binding as a singleton
	 * @return this
	 */
	BindingTo<T> asSingleton();

	/**
	 * Adds this claiss to the list of bindings for the binding key supplied.
	 * This allows for injecting multiple implementations of the same interface
	 * as a list into a class
	 * @param toClass
	 */
	void add(Class<? extends T> toClass);
}
