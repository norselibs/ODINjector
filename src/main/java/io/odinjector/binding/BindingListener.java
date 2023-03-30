package io.odinjector.binding;

import io.odinjector.injection.InjectionModifier;

public interface BindingListener<T> {
    void listen(InjectionModifier<T> injectionModifier);
}
