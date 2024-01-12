package io.odinjector;

import io.odinjector.binding.BindingContext;

import javax.inject.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SingletonBindingContext extends BindingContext {
    Map<Class, Object> singletons = Collections.synchronizedMap(new HashMap<>());

    public <T> T singleton(Class clazz, Provider<T> provider) {
        return (T)singletons.computeIfAbsent(clazz, c2 -> provider.get());
    }
}
