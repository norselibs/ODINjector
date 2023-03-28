package io.odinjector.testclasses;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OtherSingleton {
    private final SingletonImpl innerSingleton;

    @Inject
    public OtherSingleton(SingletonImpl innerSingleton) {
        this.innerSingleton = innerSingleton;
    }

    public void muh() {
        innerSingleton.muh();
    }

    public SingletonImpl inner() {
        return innerSingleton;
    }
}
