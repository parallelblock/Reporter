package com.parallelblock.reporter.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Histogram extends TimeableMetric<Histogram> {

    private Histogram(Histogram parent) {
        super(parent);
    }

    private Histogram(MetricBase base) {
        super(base);
    }

    public static void attachToHandleType(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        observeHandle = MethodHandles.lookup().findVirtual(clazz, "observe", MethodType.methodType(Void.class, Double.class));
    }

    private static MethodHandle observeHandle;

    public void observe(double amt) {
        Object handle = getHandle();
        if (handle != null)
            MethodHandleHelper.help(observeHandle, handle, amt);
    }

    @Override
    protected Histogram newInstance(Histogram parent) {
        return new Histogram(parent);
    }

    public static final class Builder extends MetricBase<Builder> {
        public Builder(MetricRegistrar parent) {
            super(parent, 3);
        }

        public Histogram create() {
            return new Histogram(this);
        }
    }
}
