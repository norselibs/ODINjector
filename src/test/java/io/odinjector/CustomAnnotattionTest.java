package io.odinjector;

import io.odinjector.testclasses.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class CustomAnnotattionTest {
    OdinJector odinJector;

    @Before
    public void before() {
        odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
    }

    @Test
    public void customAnnotation() {
        odinJector.addAnnotation(CustomAnnotation.class, (customAnnotation, conf) -> conf.addContext(MyAltCtx.class));

        ClassWithCustomAnntation actaul = odinJector.getInstance(ClassWithCustomAnntation.class);

        assertSame(TestImpl2.class, actaul.getInterface().getClass());
    }

    @Test
    public void customAnnotation_onParentClass() {
        odinJector.addAnnotation(CustomAnnotation.class, (customAnnotation, conf) -> conf.addContext(MyAltCtx.class));

        ExtendingClassWithCustomAnnotation actaul = odinJector.getInstance(ExtendingClassWithCustomAnnotation.class);

        assertSame(TestImpl2.class, actaul.getInterface().getClass());
    }
}
