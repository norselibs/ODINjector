package io.odinjector.injection;

import io.odinjector.binding.BindingContext;
import io.odinjector.binding.BindingKey;
import io.odinjector.binding.BindingResultListener;
import io.odinjector.binding.BindingTarget;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface InjectionContext<T> extends InjectionModifier<T> {
    InjectionContextImpl.CurrentContext getCurrentKey();
    BindingKey<T> getBindingKey();
    List<BindingContext> getContext();
    void addNext(Collection<? extends BindingContext> dynamicContexts, boolean recursive);
    void addToNext(Collection<? extends BindingContext> annotationContexts, boolean recursive);

    InjectionContext<T> copy();
    BindingTarget getTarget();
    boolean isOptional();
    String logOutput();

    <C> InjectionContextImpl<C> nextContextFor(Class<C> parameterType, BindingTarget target);
    void wrap(Function<T, T> t);

    T wrap(T res);

    void setResultListeners(Map<Class<?>, BindingResultListener> bindingResultListeners);

    void applyBindingResultListeners(T res);
}
