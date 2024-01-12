package io.odinjector;

import io.odinjector.testclasses.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SingletonTest {
    OdinJector odinJector;

    @Before
    public void before() {
        odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
    }

    @Test
    public void getNotSingleton() {
        TestInterface1 actual1 = odinJector.getInstance(TestImpl1.class);
        TestInterface1 actual2 = odinJector.getInstance(TestImpl2.class);

        assertNotSame(actual1, actual2);
    }

    @Test
    public void getSingleton() {
        SingletonImpl actual1 = odinJector.getInstance(SingletonImpl.class);
        SingletonImpl actual2 = odinJector.getInstance(SingletonImpl.class);

        assertSame(actual1, actual2);
    }

    @Test
    public void getSingleton_setInContext() {
        odinJector.addContext(new SingletonCtx());
        Hierarchy actual1 = odinJector.getInstance(Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertSame(actual1, actual2);
    }

    @Test
    public void getSingleton_isDifferentDependingOnSingletonContext() {
        odinJector.addDynamicContext(new SingletonCtx());
        Hierarchy actual1 = odinJector.getInstance(SingletonCtx.class, Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertNotSame(actual1, actual2);
    }

    @Test
    public void getSingletonNumberOfConstructorInvocations_setInContext() {
        odinJector.addContext(new SingletonCtx());
        HierarchyImpl.invocations.set(0);
        Hierarchy actual1 = odinJector.getInstance(Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertEquals(1, HierarchyImpl.invocations.get());
    }

    @Test
    public void getSingletonNumberOfConstructorInvocations_fromInterfaceAndImplementation() {
        odinJector.addContext(new SingletonOnImplementationCtx());
        HierarchyImpl.invocations.set(0);
        Hierarchy actual1 = odinJector.getInstance(HierarchyImpl.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertSame(actual1, actual2);
        assertEquals(1, HierarchyImpl.invocations.get());
    }

    @Test
    public void getSingletonNumberOfConstructorInvocations_isDifferentDependingOnSingletonContext() {
        HierarchyImpl.invocations.set(0);
        odinJector.addDynamicContext(new SingletonCtx());
        Hierarchy actual1 = odinJector.getInstance(SingletonCtx.class, Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertEquals(2, HierarchyImpl.invocations.get());
    }


    @Test
    public void getSingleton_fromProvider_setInContext() {
        odinJector.addContext(new SingletonProviderCtx());
        Hierarchy actual1 = odinJector.getInstance(Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertSame(actual1, actual2);
    }

    @Test
    public void getSingleton_fromProvider_isDifferentDependingOnSingletonContext() {
        odinJector.addDynamicContext(new SingletonProviderCtx());
        Hierarchy actual1 = odinJector.getInstance(SingletonProviderCtx.class, Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertNotSame(actual1, actual2);
    }

    @Test
    public void getSingletonNumberOfConstructorInvocations_fromProvider_setInContext() {
        odinJector.addContext(binder -> {
            binder.bind(Hierarchy.class).asSingleton().to(() -> new HierarchyImpl(new SingletonImpl()));
        });
        HierarchyImpl.invocations.set(0);
        Hierarchy actual1 = odinJector.getInstance(Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertEquals(1, HierarchyImpl.invocations.get());
    }

    @Test
    public void getSingletonNumberOfConstructorInvocations_fromProvider_isDifferentDependingOnSingletonContext() {
        HierarchyImpl.invocations.set(0);
        odinJector.addDynamicContext(new SingletonProviderCtx());
        Hierarchy actual1 = odinJector.getInstance(SingletonProviderCtx.class, Hierarchy.class);
        Hierarchy actual2 = odinJector.getInstance(Hierarchy.class);

        assertEquals(2, HierarchyImpl.invocations.get());
    }

    @Test
    public void getNotSingleton_fromDependency() {
        ClassWithInterfaceInjection actual1 = odinJector.getInstance(ClassWithInterfaceInjection.class);
        ClassWithInterfaceInjection actual2 = odinJector.getInstance(ClassWithInterfaceInjection.class);

        assertNotSame(actual1.get(), actual2.get());
    }

    @Test
    public void getSingleton_fromDependency() {
        odinJector.addContext(new SingletonCtx());
        TestInterface1 actual1 = odinJector.getInstance(ClassWithInterfaceInjection.class).get();
        TestInterface1 actual2 = odinJector.getInstance(ClassWithInterfaceInjection.class).get();

        assertSame(actual1, actual2);
    }


    @Test
    public void singletonIsSetInContextOfInterfaceBindingToo_instantiateFromInterfaceFirst() {
        odinJector = OdinJector.create().addContext(new SingletonCtx());

        TestInterface1 fromInterface = odinJector.getInstance(TestInterface1.class);
        TestInterface1 fromImplementationWithSingletonAnnotation = odinJector.getInstance(SingletonImpl.class);

        assertSame(fromInterface, fromImplementationWithSingletonAnnotation);
    }

    @Test
    public void singletonIsSetInContextOfInterfaceBindingToo_instantiateFromImplemeentationFirst() {
        odinJector = OdinJector.create().addContext(new SingletonCtx());

        TestInterface1 fromImplementationWithSingletonAnnotation = odinJector.getInstance(SingletonImpl.class);
        TestInterface1 fromInterface = odinJector.getInstance(TestInterface1.class);

        assertSame(fromInterface, fromImplementationWithSingletonAnnotation);
    }

    @Test
    public void differentSingletonsInDifferentContexts() {
        odinJector = OdinJector.create().addContext(new SingletonCtx()).addDynamicContext(new ASingletonBindingContext());

        TestInterface1 withASingletonScopedContext = odinJector.getInstance(ASingletonBindingContext.class, TestInterface1.class);
        TestInterface1 withoutAnyContext = odinJector.getInstance(TestInterface1.class);

        assertNotSame(withASingletonScopedContext, withoutAnyContext);
    }

    @Test
    public void nonSingletonScopedDynamicContext_doesNotDefineSingletonScope() {
        odinJector = OdinJector.create().addContext(new SingletonCtx()).addDynamicContext(new SingletonCtx());

        TestInterface1 withASingletonScopedContext = odinJector.getInstance(SingletonCtx.class, TestInterface1.class);
        TestInterface1 withoutAnyContext = odinJector.getInstance(TestInterface1.class);

        assertSame(withASingletonScopedContext, withoutAnyContext);
    }

    @Test
    public void singletonWithinSingleton() {
        odinJector = OdinJector.create().addContext(new SingletonCtx());

        OtherSingleton otherSingleton = odinJector.getInstance(OtherSingleton.class);
        OtherSingleton otherSingleton2 = odinJector.getInstance(OtherSingleton.class);

        assertSame(otherSingleton, otherSingleton2);
        assertSame(otherSingleton.inner(), otherSingleton2.inner());
    }

}
