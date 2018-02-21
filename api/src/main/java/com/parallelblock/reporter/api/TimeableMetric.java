package com.parallelblock.reporter.api;

public abstract class TimeableMetric<T extends Metric> extends Metric<T> {
    protected TimeableMetric(T parent) {
        super(parent);
    }

    protected TimeableMetric(MetricBase base) {
        super(base);
    }

    public Timer time() {
        return new CloseableTimer(this, System.currentTimeMillis());
    }

    void time(Runnable r) {
        long start = System.currentTimeMillis();
        try {
            r.run();
        } finally {
            observe((System.currentTimeMillis() - start) / 1000);
        }
    }

    public interface Timer extends AutoCloseable {
        void close();

        default void observeDuration() {
            close();
        }
    }

    private static class CloseableTimer implements AutoCloseable, Timer {
        private final TimeableMetric parent;
        private final long startMs;

        private CloseableTimer(TimeableMetric parent, long startMs) {
            this.parent = parent;
            this.startMs = startMs;
        }

        @Override
        public void close() {
            parent.observe((System.currentTimeMillis() - startMs) / 1000);
        }
    }

    protected abstract void observe(double amt);
}
