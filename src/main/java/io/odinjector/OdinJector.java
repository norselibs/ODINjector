package io.odinjector;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;
import io.odinjector.binding.BindingKey;
import io.odinjector.binding.IBindingContext;
import io.odinjector.injection.InjectionContext;
import io.odinjector.injection.InjectionContextImpl;
import io.odinjector.injection.InjectionOptions;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class OdinJector implements Injector {
	private final Instantiator instantiator;
	private final Yggdrasill yggdrasill;

	private OdinJector() {
		yggdrasill = new Yggdrasill();
		instantiator = new Instantiator(yggdrasill, this);
		yggdrasill.addAnnotation(ContextualInject.class, (ci, config) -> {
			config.recursive(ci.recursive()).addContext(ci.value());
		});
		Injector injector = this;
		addContext(new BindingContext() {
			@Override
			public void configure(Binder binder) {
				binder.bind(Injector.class).to(() -> injector);
			}
		});
	}

	public static OdinJector create() {
		return new OdinJector();
	}

	public OdinJector addContext(IBindingContext context) {
		yggdrasill.addContext(new BindingContext() {
			@Override
			public void configure(Binder binder) {
				context.configure(binder);
			}
		});
		return this;
	}

	public OdinJector addContext(BindingContext context) {
		yggdrasill.addContext(context);
		return this;
	}

	public OdinJector addDynamicContext(BindingContext context) {
		yggdrasill.addDynamicContext(context);
		return this;
	}

	public <T extends Annotation> OdinJector addAnnotation(Class<T> annotation, BiConsumer<T, ContextConfiguration> consumer) {
		yggdrasill.addAnnotation(annotation, consumer);
		return this;
	}

	public OdinJector setFallback(Function<Class<?>, Object> fallback) {
		instantiator.setFallback(fallback);
		return this;
	}

	public <T> T getInstance(BindingKey<T> type) {
		return getInstance(InjectionContextImpl.get(new ArrayList<>(), type));
	}

	public <T> T getInstance(Class<T> type) {
		return getInstance(InjectionContextImpl.get(new ArrayList<>(), BindingKey.get(type)));
	}

	public <T> Optional<T> getOptionalInstance(Class<T> type) {
		return Optional.ofNullable(getInstance(InjectionContextImpl.get(new ArrayList<>(), BindingKey.get(type), InjectionOptions.get().optional())));
	}

	public <T> T getInstance(Class<?> context, Class<T> type) {
		return getInstance(InjectionContextImpl.get(new ArrayList<>(yggdrasill.getDynamicContexts(Collections.singletonList(context))), BindingKey.get(type)));
	}

	public <T> Optional<T> getOptionalInstance(Class<?> context, Class<T> type) {
		return Optional.ofNullable(getInstance(InjectionContextImpl.get(new ArrayList<>(yggdrasill.getDynamicContexts(Collections.singletonList(context))), BindingKey.get(type), InjectionOptions.get().optional())));
	}

	public <T> List<T> getInstances(Class<T> type) {
		return getInstances(InjectionContextImpl.get(new ArrayList<>(), BindingKey.get(type), InjectionOptions.get().optional()));
	}

	public <T> List<T> getInstances(Class<?> context, Class<T> type) {
		return getInstances(InjectionContextImpl.get(new ArrayList<>(yggdrasill.getDynamicContexts(Collections.singletonList(context))), BindingKey.get(type), InjectionOptions.get().optional()));
	}

	@SuppressWarnings("unchecked")
	<T> T getInstance(InjectionContext<T> injectionContext) {
		return instantiator.get(injectionContext).get();
	}

	@SuppressWarnings("unchecked")
	<T> List<T> getInstances(InjectionContext<T> injectionContext) {
		return instantiator.getAll(injectionContext).get();
	}




}
