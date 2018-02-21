package com.parallelblock.reporter.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Summary extends TimeableMetric<Summary> {

    private Summary(Summary parent) {
        super(parent);
    }

    private Summary(MetricBase base) {
        super(base);
    }

    public static void attachToHandleType(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        observeHandle = MethodHandles.lookup().findVirtual(clazz, "observe", MethodType.methodType(Void.class, Double.class));
    }

    private static MethodHandle observeHandle;

    public void observe(double amt) {
        Object handle =  getHandle();
        if (handle != null)
            MethodHandleHelper.help(observeHandle, handle, amt);
    }

    @Override
    protected Summary newInstance(Summary parent) {
        return new Summary(parent);
    }

    public static final class Builder extends MetricBase<Builder> {
        public int ageBuckets;
        public double quantile;
        public double error;
        public long maxAgeSeconds;

        public Builder(MetricRegistrar parent) {
            super(parent, 4);
        }

        public Builder ageBuckets(int ageBuckets) {
            this.ageBuckets = ageBuckets;
            return this;
        }

        public Builder quantile(double quantile, double error) {
            this.quantile = quantile;
            this.error = error;
            return this;
        }

        public Builder maxAgeSeconds(long maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
            return this;
        }

        public Summary create() {
            return new Summary(this);
        }
    }
}
