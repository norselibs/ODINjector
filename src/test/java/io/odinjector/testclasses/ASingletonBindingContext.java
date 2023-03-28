package io.odinjector.testclasses;

import io.odinjector.Binder;
import io.odinjector.SingletonBindingContext;

public class ASingletonBindingContext extends SingletonBindingContext {
    @Override
    public void configure(Binder binder) {
        binder.bind(TestInterface1.class).to(SingletonImpl.class);
    }
}
