package com.parallelblock.reporter.plugin.common;

import io.prometheus.client.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.parallelblock.reporter.plugin.common.MethodHandleHelper.getNamedField;

public final class CentralHandler {

    private final CollectorRegistry registry = new CollectorRegistry();
    private final Map<Object, Set<Collector>> registeredHandles = new HashMap<>();

    public Class[] getHandleTypes() {
        return new Class[]{Counter.class, Gauge.class, Histogram.class, Summary.class};
    }

    public Object register(Object handleOwner, Object handleBase, Object parentHandle, String[] labels) throws Throwable {
        Object handle = null;
        if (parentHandle != null) {
            handle = ((SimpleCollector) parentHandle).labels(labels);
        } else {
            Integer i = getNamedField(handleBase, "type", Integer.class);
            SimpleCollector.Builder collectorBuilder;
            switch (i) {
                case 1:
                    // counter
                    collectorBuilder = Counter.build();
                    break;
                case 2:
                    // gauge
                    collectorBuilder = Gauge.build();
                    break;
                case 3:
                    // histogram
                    collectorBuilder = Histogram.build();
                    break;
                case 4:
                    // summary
                    collectorBuilder = Summary.build()
                            .ageBuckets(getNamedField(handleBase, "ageBuckets", Integer.class))
                            .quantile(getNamedField(handleBase, "quantile", Double.class),
                                    getNamedField(handleBase, "error", Double.class))
                            .maxAgeSeconds(getNamedField(handleBase, "maxAgeSeconds", Long.class));
                    break;
                default:
                    throw new RuntimeException("Unrecognized type");
            }
            handle = collectorBuilder
                    .name(getNamedField(handleBase, "name", String.class))
                    .help(getNamedField(handleBase, "help", String.class))
                    .labelNames(getNamedField(handleBase, "labels", String[].class))
                    .namespace(getNamedField(handleBase, "namespace", String.class))
                    .namespace(getNamedField(handleBase, "subsystem", String.class)).register(registry);
        }
        registeredHandles.computeIfAbsent(handleOwner, whatever -> new HashSet<>())
                .add((Collector) handle);
        return handle;
    }

    public void unregisterHandlesFromOwner(Object owner) {
        Set<Collector> collectors = registeredHandles.remove(owner);
        if (collectors == null)
            return;

        for (Collector c : collectors)
            registry.unregister(c);
    }
}
