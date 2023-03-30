package io.odinjector;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;
import io.odinjector.binding.BindingKey;
import io.odinjector.testclasses.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class GenericDependenciesTest {
    OdinJector odinJector;

    @Before
    public void before() {
        odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
    }


    @Test
    public void dependsOnGeneric() {
        odinJector = OdinJector.create();
        TestImpl1 mock = Mockito.mock(TestImpl1.class);
        odinJector.addContext(new BindingContext() {
            @Override
            public void configure(Binder binder) {
                binder.bind(TestImpl1.class).to(() -> mock);
                binder.bind(TestInterface1.class).to(TestImpl1.class);
                binder.bind(BindingKey.get(MyGeneric.class, TestInterface1.class)).to(Provides.of(TestImpl1.class, MyGeneric::new));
            }
        });
        MyGeneric<TestInterface1> actual = odinJector.getInstance(new BindingKey<>(MyGeneric.class, TestInterface1.class));

        actual.doThis();
        verify(mock).muh();
    }

    @Test
    public void dependsOnSpecificGeneric() {
        odinJector = OdinJector.create();
        TestImpl1 mock = Mockito.mock(TestImpl1.class);
        TestImpl2 mock2 = Mockito.mock(TestImpl2.class);
        odinJector.addContext(new BindingContext() {
            @Override
            public void configure(Binder binder) {
                binder.bind(TestImpl1.class).to(() -> mock);
                binder.bind(TestImpl2.class).to(() -> mock2);
                binder.bind(BindingKey.get(MyGeneric.class, TestImpl1.class)).to(Provides.of(TestImpl1.class, MyGeneric::new));
                binder.bind(BindingKey.get(MyGeneric.class, TestImpl2.class)).to(Provides.of(TestImpl2.class, MyGeneric::new));
            }
        });
        MyGeneric<TestInterface1> actual = odinJector.getInstance(new BindingKey<>(MyGeneric.class, TestImpl2.class));

        actual.doThis();
        verify(mock2).muh();
        verifyNoInteractions(mock);
    }
}
