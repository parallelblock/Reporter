package com.parallelblock.reporter.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Counter extends Metric<Counter> {
    private Counter(Counter parent) {
        super(parent);
    }

    private Counter(MetricBase base) {
        super(base);
    }

    public static void attachToHandleType(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        incHandle = MethodHandles.lookup().findVirtual(clazz, "inc", MethodType.methodType(Void.class, Double.class));
    }

    private static MethodHandle incHandle;

    public void inc() {
        inc(1);
    }

    public void inc(double amount) {
        Object handle = getHandle();
        if (handle != null)
            MethodHandleHelper.help(incHandle, handle, amount);
    }

    @Override
    protected Counter newInstance(Counter parent) {
        return new Counter(parent);
    }

    public static final class Builder extends MetricBase<Builder> {
        public Builder(MetricRegistrar group) {
            super(group, 1);
        }

        public Counter create() {
            return new Counter(this);
        }
    }
}
