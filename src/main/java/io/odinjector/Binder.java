package io.odinjector;

public interface Binder {
	<T> BindingTo<T> bind(Class<T> tClass);
	<T> void injectionListener(BindingListener<T> listener);
	void bindPackageToContext(Package aPackage);
	void bindingResultListener(BindingResultListener listener);
	<T> BindingTo<T> bind(BindingKey<T> bindingKey);
}
