package com.parallelblock.reporter.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MetricRoot implements MetricRegistrar {

    private Object generationHandle;
    protected Object handleOwner;
    private MethodHandle registrationHandle;

    public Counter.Builder counter() {
        return new Counter.Builder(this);
    }

    public Gauge.Builder gauge() {
        return new Gauge.Builder(this);
    }

    public Histogram.Builder histogram() {
        return new Histogram.Builder(this);
    }

    public Summary.Builder summary() {
        return new Summary.Builder(this);
    }

    @Override
    public Object register(MetricBase metric, Object parentHandle, String[] labels) {
        if (generationHandle == null)
            throw new IllegalStateException("attempted to register when generationHandle expired");
        try {
            return registrationHandle.invoke(generationHandle, handleOwner, (Object)metric, parentHandle, labels);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unexpected exception while trying to register metric");
        }
    }

    @Override
    public Object generationHandle() {
        return generationHandle;
    }

    protected void updateCentralAPI(Object handle, Object owner) throws Throwable {
        this.generationHandle = handle;
        this.handleOwner = owner;
        if (handle == null)
            return; // generation handles invalidated, will default all metrics to noop

        // first, update statics for each of the objects (this may happen multiple times if we are sharing namespaces)
        Class[] clazzes = (Class[])MethodHandles.lookup().findVirtual(handle.getClass(), "getHandleTypes", MethodType.methodType(Class[].class))
                .invoke(handle);

        Counter.attachToHandleType(clazzes[0]);
        Gauge.attachToHandleType(clazzes[1]);
        Histogram.attachToHandleType(clazzes[2]);
        Summary.attachToHandleType(clazzes[3]);

        // find registration handle
        registrationHandle = MethodHandles.lookup().findVirtual(handle.getClass(), "register", MethodType.methodType(Object.class, new Class[]{Object.class, Object.class, String[].class}));
    }
}
