package com.parallelblock.reporter.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Gauge extends TimeableMetric<Gauge> {
    private Gauge(Gauge parent) {
        super(parent);
    }

    private Gauge(MetricBase base) {
        super(base);
    }

    public static void attachToHandleType(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        incHandle = MethodHandles.lookup().findVirtual(clazz, "inc", MethodType.methodType(Void.class, Double.class));
        setHandle = MethodHandles.lookup().findVirtual(clazz, "set", MethodType.methodType(Void.class, Double.class));
    }

     private static MethodHandle incHandle;
     private static MethodHandle setHandle;

     public void dec() {
         inc(-1);
     }

     public void dec(double amount) {
         inc(-amount);
     }

     public void inc() {
         inc(1);
     }

     void inc(double amount) {
         Object handle = getHandle();
         if (handle != null)
            MethodHandleHelper.help(incHandle, handle, amount);
     }

     void set(double value) {
         Object handle = getHandle();
         if (handle != null)
             MethodHandleHelper.help(setHandle, handle, value);

     }

     void setToCurrentTime() {
         set(System.currentTimeMillis() / 1000);
     }

    @Override
    protected void observe(double amt) {
        set(amt);
    }

    @Override
    protected Gauge newInstance(Gauge parent) {
        return new Gauge(parent);
    }

    public static final class Builder extends MetricBase<Builder> {
        public Builder(MetricRegistrar group) {
            super(group, 2);
        }

        public Gauge create() {
            return new Gauge(this);
        }
    }
}
