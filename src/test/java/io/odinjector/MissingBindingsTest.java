package io.odinjector;

import io.odinjector.injection.InjectionException;
import io.odinjector.testclasses.MyAltCtx;
import io.odinjector.testclasses.MyCtx;
import io.odinjector.testclasses.MyOtherAltCtx;
import io.odinjector.testclasses.UnboundInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class MissingBindingsTest {

    OdinJector odinJector;

    @Before
    public void before() {
        odinJector = OdinJector.create().addContext(new MyCtx()).addDynamicContext(new MyAltCtx()).addDynamicContext(new MyOtherAltCtx());
    }

    @Test(expected = InjectionException.class)
    public void getInstance_withNoImplementations() {
        odinJector.getInstance(UnboundInterface.class);
    }

    @Test
    public void getOptionalInstance_withNoImplementations() {
        Optional<UnboundInterface> actual = odinJector.getOptionalInstance(UnboundInterface.class);

        assertFalse(actual.isPresent());
    }

    @Test
    public void getInstances_withNoImplementations() {
        List<UnboundInterface> actual = odinJector.getInstances(UnboundInterface.class);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }


    @Test(expected = InjectionException.class)
    public void getInstance_fromContext_withNoImplementations() {
        odinJector.getInstance(MyAltCtx.class, UnboundInterface.class);
    }

    @Test
    public void getOptionalInstance_fromContext_withNoImplementations() {
        Optional<UnboundInterface> actual = odinJector.getOptionalInstance(MyAltCtx.class, UnboundInterface.class);

        assertFalse(actual.isPresent());
    }

    @Test
    public void getInstances_fromContext_withNoImplementations() {
        List<UnboundInterface> actual = odinJector.getInstances(MyAltCtx.class, UnboundInterface.class);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }
}
