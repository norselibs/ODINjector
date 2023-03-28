package io.odinjector;

public interface BindingListener<T> {
    void listen(InjectionModifier<T> injectionModifier);
}
