package io.odinjector;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;
import io.odinjector.testclasses.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InstanceWrappingTest {
    OdinJector odinJector;

    @Before
    public void before() {
        odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
    }

    @Test
    public void wrapObjectUsingListener() {
        odinJector = OdinJector.create();
        odinJector.addContext(new BindingContext() {
            @Override
            public void configure(Binder binder) {
                binder.bindPackageToContext(TestImpl1.class.getPackage());
                binder.injectionListener((injectionContext) -> injectionContext.wrap(Mockito::spy));
            }
        });
        TestImpl1 actual = odinJector.getInstance(TestImpl1.class);

        actual.muh();
        verify(actual).muh();
    }

    @Test
    public void wrapObjectUsingListener_forCertainObjects() {
        odinJector = OdinJector.create();
        TestInterface1 mock = Mockito.mock(TestInterface1.class);
        odinJector.addContext(new BindingContext() {
            @Override
            public void configure(Binder binder) {
                binder.bindPackageToContext(TestImpl1.class.getPackage());
                binder.bind(TestInterface1.class).to(TestImpl1.class);
                binder.injectionListener((injectionContext) -> {
                    if(injectionContext.getTarget().hasAnnotation(Wrapped.class) && injectionContext.getBindingKey().getBoundClass().equals(TestInterface1.class)) {
                        injectionContext.wrap((o) -> mock);
                    }
                });
            }
        });
        AllInjectionTypes actual = odinJector.getInstance(AllInjectionTypes.class);

        actual.runAll();

        verify(mock, times(3)).muh();
    }

    @Test(expected = RuntimeException.class)
    public void wrapObjectUsingBindingResultListener_failOnMockInjected() {
        odinJector = OdinJector.create();
        TestInterface1 mock = Mockito.mock(TestInterface1.class);
        odinJector.addContext(new BindingContext() {
            @Override
            public void configure(Binder binder) {
                binder.bindPackageToContext(TestImpl1.class.getPackage());
                binder.bind(TestInterface1.class).to(() -> Mockito.mock(TestInterface1.class));
                binder.bindingResultListener(brl -> {
                    if (brl.getBound().getBoundClass().getSimpleName().contains("$MockitoMock$")) {
                        throw new RuntimeException("This is a mock");
                    }
                });
            }
        });
        AllInjectionTypes actual = odinJector.getInstance(AllInjectionTypes.class);
    }

}
