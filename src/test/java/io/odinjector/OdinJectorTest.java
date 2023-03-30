package io.odinjector;

import io.odinjector.binding.Binder;
import io.odinjector.binding.BindingContext;
import io.odinjector.testclasses.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OdinJectorTest {
	OdinJector odinJector;

	@Before
	public void before() {
		odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
	}

	@Test
	public void getInterfaceBinding() {
		TestInterface1 actual = odinJector.getInstance(TestInterface1.class);

		assertSame(TestImpl1.class, actual.getClass());
	}

	@Test
	public void getActualInstance() {
		TestImpl1 actual = odinJector.getInstance(TestImpl1.class);
		assertSame(TestImpl1.class, actual.getClass());
	}

	@Test
	public void getClassWithInterfaceBindingDependency() {
		ClassWithInterfaceInjection actual = odinJector.getInstance(ClassWithInterfaceInjection.class);

		assertSame(TestImpl1.class, actual.get().getClass());
	}

	@Test
	public void getClassUsingMethodInjection() {
		ClassWithMethodInjection actual = odinJector.getInstance(ClassWithMethodInjection.class);

		assertSame(TestImpl1.class, actual.get().getClass());
	}

	@Test
	public void getClassWithProviderDependency() {
		ClassWithProviderInjection actual = odinJector.getInstance(ClassWithProviderInjection.class);

		assertSame(TestImpl1.class, actual.get().getClass());
	}

	@Test
	public void getClassWithProviderDependency_asSecondParameter() {
		ProviderAsSecondParameter actual = odinJector.getInstance(ProviderAsSecondParameter.class);

		assertSame(TestImpl1.class, actual.get().getClass());
	}

	@Test
	public void getClassWithListDependency() {
		ClassWithListInjection actual = odinJector.getInstance(ClassWithListInjection.class);

		assertEquals(1, actual.get().size());
		assertSame(TestImpl1.class, actual.get().get(0).getClass());
	}

	@Test
	public void getClassWithMultipleElementsInListDependency() {
		odinJector.addContext(new MyMultipleBindingsCtx());
		ClassWithListInjection actual = odinJector.getInstance(ClassWithListInjection.class);

		assertEquals(3, actual.get().size());
		assertSame(TestImpl1.class, actual.get().get(0).getClass());
		assertSame(TestImpl2.class, actual.get().get(1).getClass());
		assertSame(TestImpl3.class, actual.get().get(2).getClass());
	}

	@Test
	public void additionalContextsOverrideExisting() {
		odinJector.addContext(new MyAltCtx());
		TestInterface1 actual = odinJector.getInstance(TestInterface1.class);

		assertSame(TestImpl2.class, actual.getClass());
	}

	@Test
	public void getWithInjectionFromAdditinalContext() {
		odinJector.addContext(new MyAltCtx());
		ClassWithInterfaceInjection actual = odinJector.getInstance(ClassWithInterfaceInjection.class);

		assertSame(TestImpl2.class, actual.get().getClass());
	}



	@Test
	public void getInstanceFromProvider() {
		odinJector.addContext(new ProviderCtx());
		TestInterface1 actual = odinJector.getInstance(TestInterface1.class);

		assertSame(TestImpl2.class, actual.getClass());
	}



	@Test
	public void fallbackProvider() {
		odinJector.setFallback((c) -> {
			if (c == UnboundInterface.class) {
				return new UnboundInterfaceImplementation();
			}
			throw new RuntimeException("Unexpected class fallback");
		});

		UnboundInterface actual = odinJector.getInstance(UnboundInterface.class);

		assertSame(UnboundInterfaceImplementation.class, actual.getClass());
	}

	@Test
	public void injectorBinding() {
		ClassWithInjectorInjected actual = odinJector.getInstance(ClassWithInjectorInjected.class);

		assertSame(TestImpl1.class, actual.getImplementation().getClass());
	}


	@Test
	public void multipleInjectionsFromProvides() {
		odinJector = OdinJector.create();
		TestImpl1 mock = Mockito.mock(TestImpl1.class);
		TestImpl2 mock2 = Mockito.mock(TestImpl2.class);
		TestImpl3 mock3 = Mockito.mock(TestImpl3.class);
		odinJector.addContext(new BindingContext() {
			@Override
			public void configure(Binder binder) {
			binder.bind(TestImpl1.class).to(() -> mock);
			binder.bind(TestImpl2.class).to(() -> mock2);
			binder.bind(TestImpl3.class).to(() -> mock3);
			binder.bind(MultipleSameDependencies.class).to(Provides.of(TestImpl1.class, TestImpl2.class, TestImpl3.class, MultipleSameDependencies::new));
			}
		});
		MultipleSameDependencies actual = odinJector.getInstance(MultipleSameDependencies.class);

		actual.run();
		verify(mock).muh();
		verify(mock2).muh();
		verify(mock3).muh();
	}

	@Test
	public void withRecursiveInstantiation() {
		odinJector = OdinJector.create();

		RecursiveInstantiationSingletonParent actual = odinJector.getInstance(RecursiveInstantiationSingletonParent.class);

		actual.getParent().getChild();
		// This should not fail
	}

	@Test
	public void withLoopingProviderInstantiation() {
		odinJector = OdinJector.create();

		LoopingProviderDependency1 actual = odinJector.getInstance(LoopingProviderDependency1.class);

		actual.getDep().getDep().getDep().getDep();
		// This should not fail
	}


	@Test
	public void perfTestBasic() throws Throwable {
		perfTestBase(Assert::assertNotSame);
	}

	@Test
	public void perfTestSingleton() throws Throwable {
		odinJector = OdinJector.create().addContext(new SingletonCtx());
		perfTestBase(Assert::assertSame);
	}

	private void perfTestBase(BiConsumer<TestInterface1, TestInterface1> assertDelegate) {
		long s = System.currentTimeMillis();

		List<Thread> threads = new ArrayList<>();
		for(int i=0;i<10;i++) {
			Thread t = new Thread(() -> {
				TestInterface1 previous = null;
				for(int x =0;x<100_000;x++) {
					TestInterface1 actual1 = odinJector.getInstance(TestInterface1.class);
					if (previous == null) {
						previous = actual1;
					} else {
						assertDelegate.accept(previous, actual1);
					}
				}
			});
			threads.add(t);
		}
		threads.forEach(Thread::run);
		threads.forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		assertTrue(3000 > System.currentTimeMillis()-s);
	}
}
