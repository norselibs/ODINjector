package io.odinjector;

import io.odinjector.testclasses.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class ContextualInjectionTest {
    OdinJector odinJector;

    @Before
    public void before() {
        odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
    }

    @Test
    public void getDependency_fromContextualInject() {
        ContextualDependencies actual1 = odinJector.getInstance(ContextualDependencies.class);

        assertSame(TestImpl2.class, actual1.getDependency().getClass());
    }

    @Test
    public void getDependency_fromContextualMarkerInject() {
        odinJector.addDynamicContext(new MyAltCtxWithMarker());
        ContextualDependenciesWithMarker actual1 = odinJector.getInstance(ContextualDependenciesWithMarker.class);

        assertSame(TestImpl2.class, actual1.getDependency().getClass());
    }

    @Test
    public void getDependency_fromMarkerInject() {
        odinJector.addContext(new MyAltCtxWithMarker());
        ContextualDependencies actual1 = odinJector.getInstance(ContextualDependencies.class);

        assertSame(TestImpl2.class, actual1.getDependency().getClass());
    }

    @Test
    public void getDependency_fromNonRecurisveContextualInject() {
        ClassWithNonRecursiveHierarchialContext actual = odinJector.getInstance(ClassWithNonRecursiveHierarchialContext.class);

        assertSame(AltHierarchyImpl.class, actual.getHierarchy().getClass());
        assertSame(TestImpl1.class, actual.getHierarchy().getTestInterface().getClass());
    }

    @Test
    public void getDependency_fromRecurisveContextualInject() {
        ClassWithRecursiveHierarchialContext actual = odinJector.getInstance(ClassWithRecursiveHierarchialContext.class);

        assertSame(AltHierarchyImpl.class, actual.getHierarchy().getClass());
        assertSame(TestImpl2.class, actual.getHierarchy().getTestInterface().getClass());
    }

    @Test
    public void getDependency_fromNonRecurisveContextualInject_setOnResolvedClass() {
        InterfaceForClassWithNonRecursiveHierarchialContext actual = odinJector.getInstance(InterfaceForClassWithNonRecursiveHierarchialContext.class);

        assertSame(AltHierarchyImpl.class, actual.getHierarchy().getClass());
        assertSame(TestImpl1.class, actual.getHierarchy().getTestInterface().getClass());
    }

    @Test
    public void getDependency_withMultipleContextualClasses() {
        ClassWithMultipleContexts actual = odinJector.getInstance(ClassWithMultipleContexts.class);

        assertSame(AltHierarchyImpl.class, actual.getHierarchy().getClass());
        assertSame(TestImpl3.class, actual.getTestInterface1().getClass());
    }


}
