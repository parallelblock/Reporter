package com.parallelblock.reporter.api;

public interface MetricRegistrar {
    Object register(MetricBase metric, Object parentHandle, String[] labels);
    Object generationHandle();
}
