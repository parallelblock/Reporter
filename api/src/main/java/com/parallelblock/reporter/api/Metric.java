package com.parallelblock.reporter.api;

public abstract class Metric<T extends Metric> {

    protected Metric(T parent) {
        // WHAT THE ACTUAL FUCK JAVA
        // WHY ARE YOU LIKE THIS
        this.base = ((Metric)parent).base;
        nolabelChild = parent;
    }

    protected Metric(MetricBase base) {
        this.base = base;
        this.nolabelChild = (T)this;
        this.labels = new String[0];
    }

    private String[] labels;
    private T nolabelChild;

    private final MetricBase base;

    private Object handle;
    private Object generationHandle;

    public T labels(String... labels) {
        T t = newInstance(nolabelChild);
        ((Metric)t).labels = labels;
        return t;
    }

    protected Object getHandle() {
        // We either haven't registered or we need to re-register because we ded
        Object globalGenHandle = base.parent.generationHandle();
        if (globalGenHandle == null) return null; // we have no effective handle
        if (globalGenHandle != generationHandle) {

            if (nolabelChild != this)
                handle = base.parent.register(base, nolabelChild.getHandle(), labels);
            else
                handle = base.parent.register(base, null, labels);

            generationHandle = globalGenHandle;
        }
        return handle;
    }

    protected abstract T newInstance(T parent);
}
