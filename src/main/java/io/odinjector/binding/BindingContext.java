package io.odinjector.binding;

import io.odinjector.ClassBinding;
import io.odinjector.ContextBinder;
import io.odinjector.injection.InjectionContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BindingContext {
	Map<BindingKey<?>, List<Binding<?>>> contextBindings = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	Map<Class<?>, BindingListener> bindingListeners = new ConcurrentHashMap<>();
	Set<Package> packageBindings = Collections.synchronizedSet(new HashSet<>());
	Map<Class<?>, BindingResultListener> bindingResultListeners = new ConcurrentHashMap<>();

	public abstract void configure(Binder binder);

	public void init() {
		configure(new ContextBinder(this));
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	public <T> List<BindingResult<T>> getBindings(InjectionContext<T> injectionContext) {
		bindingListeners.values().forEach(bl -> bl.listen(injectionContext));
		injectionContext.setResultListeners(bindingResultListeners);
		if (!contextBindings.containsKey(injectionContext.getBindingKey()) && packageBindings.contains(injectionContext.getBindingKey().getBoundClass().getPackage())) {
			contextBindings.put(injectionContext.getBindingKey(), Collections.singletonList(ClassBinding.of(injectionContext.getBindingKey(), false)));
		}
		return (contextBindings.containsKey(injectionContext.getBindingKey())
			? (List)contextBindings.get(injectionContext.getBindingKey()).stream().map(b -> BindingResult.of(b, this)).collect(Collectors.toList())
			: Collections.emptyList()
		);
	}

	public <T> BindingResult<T> getBinding(InjectionContext<T> injectionContext) {
		List<BindingResult<T>> bindings = getBindings(injectionContext);
		if (bindings.isEmpty()) {
			return BindingResult.empty();
		}
		return bindings.get(0);
	}

	public Class<?> getMarkedContext() {
		return getClass();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		return getClass().equals(o.getClass());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@SuppressWarnings("rawtypes")
	public void addListener(BindingListener listener) {
		bindingListeners.put(listener.getClass(), listener);
	}

	@SuppressWarnings("rawtypes")
	public Map<Class<?>, BindingListener> getBindingListeners() {
		return bindingListeners;
	}

	public void addPackageBinding(Package aPackage) {
		packageBindings.add(aPackage);
	}

	public void addBindingResultListener(BindingResultListener listener) {
		bindingResultListeners.put(listener.getClass(),listener);
	}

	public void add(BindingKey<?> key, List<Binding<?>> bindings) {
		contextBindings.put(key, bindings);
	}

	public void addIfAbsent(BindingKey<?> key, Supplier<Binding<?>> binding) {
		contextBindings.computeIfAbsent(key, l -> new ArrayList<>()).add(binding.get());
	}
}
