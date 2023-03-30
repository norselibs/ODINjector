package io.odinjector;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;
import io.odinjector.binding.BindingResult;
import io.odinjector.injection.InjectionContext;
import io.odinjector.injection.InjectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

class Yggdrasill extends SingletonBindingContext {
	final Map<Class<?>, BindingContext> contexts = Collections.synchronizedMap(new LinkedHashMap<>());
	private final Map<Class<?>, BindingContext> dynamicContexts = Collections.synchronizedMap(new LinkedHashMap<>());
	private Map<Class<? extends Annotation>,BiConsumer<Object, ContextConfiguration>> annotations = Collections.synchronizedMap(new LinkedHashMap<>());

	public void addContext(BindingContext context) {
		context.init();
		contexts.put(context.getClass(), context);
	}

	public void addDynamicContext(BindingContext context) {
		context.init();
		dynamicContexts.put(context.getMarkedContext(), context);
	}

	@Override
	public void configure(Binder binder) {

	}

	public <T> List<BindingResult<T>> getBindings(InjectionContext<T> injectionContext) {
		if (injectionContext.getContext() != null) {
			List<BindingContext> list = new ArrayList<>(injectionContext.getContext());
			Collections.reverse(list);
			for (BindingContext context : list) {
				List<BindingResult<T>> bindings = context.getBindings(injectionContext);
				if (!bindings.isEmpty()) {
					return bindings;
				}
			}
		}
		List<BindingContext> list = new ArrayList<>(contexts.values());
		Collections.reverse(list);
		for(BindingContext context : list) {
			List<BindingResult<T>> bindings = context.getBindings(injectionContext);
			if (!bindings.isEmpty()) {
				return bindings;
			}
		}
		if (injectionContext.isOptional() && (injectionContext.getBindingKey().isInterface() || Modifier.isAbstract(injectionContext.getBindingKey().getModifiers()))) {
			return Collections.emptyList();
		}
		return Collections.singletonList(BindingResult.of(ClassBinding.of(injectionContext.getBindingKey()), this));
	}

	List<? extends BindingContext> getDynamicContexts(List<Class<?>> annotationContexts) {
		return annotationContexts.stream().map(ac -> {
			if (!dynamicContexts.containsKey(ac)) {
				throw new InjectionException("Unable to find a registered dynamic context for: "+ac.getName());
			}
			return dynamicContexts.get(ac);
		}).collect(Collectors.toList());
	}

	public <T extends Annotation> void addAnnotation(Class<T> annotation, BiConsumer<T, ContextConfiguration> consumer) {
		annotations.put(annotation, (BiConsumer) consumer);
	}

	ContextConfiguration getAnnotationConfiguration(Class<?> elementClass) {
		ContextConfiguration configuration = new ContextConfiguration();
		Class<?> workingClass = elementClass;
		while(workingClass != Object.class) {
			for (Map.Entry<Class<? extends Annotation>, BiConsumer<Object, ContextConfiguration>> entry : annotations.entrySet()) {
				Class<? extends Annotation> c = entry.getKey();
				BiConsumer<Object, ContextConfiguration> consumer = entry.getValue();
				if (workingClass.getAnnotation(c) != null) {
					Annotation instance = workingClass.getAnnotation(c);
					consumer.accept(instance, configuration);
				}
			}
			workingClass = workingClass.getSuperclass();
			if (workingClass == null) {
				break;
			}
		}
		return configuration;
	}
}
