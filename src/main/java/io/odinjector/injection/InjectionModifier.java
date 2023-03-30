package io.odinjector.injection;

import io.odinjector.binding.BindingKey;
import io.odinjector.binding.BindingTarget;

import java.util.function.Function;

public interface InjectionModifier<T> {
    void wrap(Function<T, T> t);
    BindingTarget getTarget();
    BindingKey<T> getBindingKey();
}
