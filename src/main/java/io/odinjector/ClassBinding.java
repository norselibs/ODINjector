package io.odinjector;

import io.odinjector.binding.Binding;
import io.odinjector.binding.BindingContext;
import io.odinjector.binding.BindingKey;
import io.odinjector.binding.BindingTarget;
import io.odinjector.injection.InjectionContext;
import io.odinjector.injection.InjectionException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes","unchecked"})
public class ClassBinding<T> implements Binding<T> {
	@SuppressWarnings("unchecked")
	private static final Class<? extends Annotation> JAVAX_POST_CONSTRUCT;
	static {
		Class<? extends Annotation> cls = null;
		try {
			cls = (Class<? extends Annotation>) Class.forName("javax.annotation.PostConstruct");
		} catch (ClassNotFoundException ignored) {}
		JAVAX_POST_CONSTRUCT = cls;
	}

	BindingKey<?> toClass;
	boolean setAsSingleton;

	private ClassBinding(BindingKey<? extends T> toClass, boolean setAsSingleton) {
		this.toClass = toClass;
		this.setAsSingleton = setAsSingleton;
	}

	public static <T> ClassBinding<T> of(BindingKey<? extends T> toClass) {
		return new ClassBinding<>(toClass, false);
	}

	public static <T> ClassBinding<T> of(BindingKey<? extends T> toClass, boolean setAsSingleton) {
		return new ClassBinding<>(toClass, setAsSingleton);
	}

	public Provider<T> getProvider(BindingContext context, InjectionContext<T> thisInjectionContext, OdinJector injector) {
		Constructor<?> constructor = Arrays.stream(toClass.getBoundClass().getDeclaredConstructors()).filter(const1 -> const1.isAnnotationPresent(Inject.class)).findFirst()
				.orElseGet(() -> Arrays.stream(toClass.getBoundClass().getDeclaredConstructors()).filter(c2 -> c2.getParameterTypes().length == 0).findFirst()
						.orElseThrow(() -> new InjectionException("Unable to find constructor which has the @Inject annotation or is parameterless on: "+toClass.getName())));


		List<Provider> args = new ArrayList<>();
		for(int i=0;i<constructor.getParameters().length;i++) {
			args.add(getInjection(thisInjectionContext, injector, constructor.getParameters()[i].getType(), constructor.getParameters()[i].getAnnotatedType(), new BindingTarget.ParameterTarget(constructor.getParameters()[i])));
		}
		List<Consumer<T>> additionalInjectors = new ArrayList<>();
		for(Method method : toClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Inject.class)) {
				method.setAccessible(true);
				List<Provider> methodArgs = new ArrayList<>();
				for(int x=0;x<method.getParameters().length;x++) {
					methodArgs.add(getInjection(thisInjectionContext, injector, method.getParameters()[x].getType(), method.getParameters()[x].getAnnotatedType(), new BindingTarget.ParameterTarget(method.getParameters()[x])));
				}
				additionalInjectors.add((t) -> {
					try {
						method.invoke(t,methodArgs.stream().map(Provider::get).toArray());
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				});
			}
		}

		for(Field field : toClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Inject.class)) {
				field.setAccessible(true);
				additionalInjectors.add((t) -> {
					try {
						field.set(t, getInjection(thisInjectionContext, injector, field.getType(), field.getAnnotatedType(), new BindingTarget.FieldTarget(field)).get());
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				});
			}
		}

		for (Method method : toClass.getDeclaredMethods()) {
			if (isPostConstruct(method)) {
				method.setAccessible(true);
				additionalInjectors.add((t) -> {
					try {
						method.invoke(t);
					} catch (IllegalAccessException | InvocationTargetException e) {
						throw new InjectionException("@PostConstruct failed on " + method.getName() + " in " + toClass.getName(), e);
					}
				});
			}
		}

		return new ClassBindingProvider(thisInjectionContext, constructor, args, toClass, additionalInjectors);
	}

	private boolean isPostConstruct(Method method) {
		return method.isAnnotationPresent(PostConstruct.class)
				|| (JAVAX_POST_CONSTRUCT != null && method.isAnnotationPresent(JAVAX_POST_CONSTRUCT));
	}

	private Provider<T> getInjection(InjectionContext<T> thisInjectionContext, OdinJector injector, Class type, AnnotatedType annotatedType, BindingTarget target) {
		return getInjectionProvider(thisInjectionContext, injector, type, annotatedType, target);
	}

	private Provider getInjectionProvider(InjectionContext<T> thisInjectionContext, OdinJector injector, Class type, AnnotatedType annotatedType, BindingTarget target) {
		if (type == List.class) {
			Class<?> listElementType = getClassFromType(annotatedType.getType());
			return () -> injector.getInstances(thisInjectionContext.nextContextFor(listElementType, target));
		} else if (type == Provider.class) {
			Class<?> providerElementType = getClassFromType(annotatedType.getType());
			return () -> (Provider)() -> injector.getInstance(thisInjectionContext.nextContextFor(providerElementType, target));
		} else {
			return () -> injector.getInstance(thisInjectionContext.nextContextFor(type, target));
		}
	}

	private static class ClassBindingProvider<C> implements Provider<C> {
		private InjectionContext<C> thisInjectionContext;
		private Constructor<?> constructor;
		private List<Provider> args;
		private BindingKey<C> toBindingKey;
		private List<Consumer<C>> additionalInjectors;

		private ClassBindingProvider(InjectionContext<C> thisInjectionContext, Constructor<?> constructor, List<Provider> args, BindingKey<C> toBindingKey, List<Consumer<C>> additionalInjectors) {
			this.thisInjectionContext = thisInjectionContext;
			this.constructor = constructor;
			this.args = args;
			this.toBindingKey = toBindingKey;
			this.additionalInjectors = additionalInjectors;
		}

		@Override
		public C get() {
			LinkedList<Class<?>> chain = Instantiator.RESOLUTION_CHAIN.get();
			Class<?> type = toBindingKey.getBoundClass();
			if (chain.contains(type)) {
				String chainStr = chain.stream().map(Class::getSimpleName).collect(Collectors.joining(" → "));
				throw new InjectionException("Circular dependency detected: " + chainStr + " → " + type.getSimpleName());
			}
			chain.addLast(type);
			try {
				constructor.setAccessible(true);
				C res = (C) constructor.newInstance(args.stream().map(Provider::get).toArray());
				additionalInjectors.forEach(c -> c.accept(res));
				return res;
			} catch (InjectionException e) {
				throw e;
			} catch (Exception e) {
				throw new InjectionException("Unable to construct " + toBindingKey.getName(), e);
			} finally {
				chain.removeLast();
			}
		}
	}

	@Override
	public Class<T> getElementClass() {
		return (Class<T>)toClass.getBoundClass();
	}

	@Override
	public boolean isSingleton() {
		return setAsSingleton || toClass.isAnnotationPresent(Singleton.class);
	}

	@Override
	public boolean isInterface() {
		return toClass.isInterface();
	}

	private Class<?> getClassFromType(Type type) {
		if (type instanceof ParameterizedType) {
			return (Class<?>)((ParameterizedType) type).getActualTypeArguments()[0];
		}
		throw new InjectionException("Could not find type parameter for generic class: "+type.getTypeName());
	}
}
