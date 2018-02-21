package com.parallelblock.reporter.api;

public abstract class MetricBase <T extends MetricBase> {

    public final MetricRegistrar parent;
    public final int type;
    public String name;
    public String help;
    public String[] labels;
    public String namespace;
    public String subsystem;

    protected MetricBase(MetricRegistrar parent, int type) {
        if (parent == null)
            throw new NullPointerException("MetricBase created without a parent (parent is null)");
        this.parent = parent;
        this.type = type;
    }

    public T name(String name) {
        this.name = name;
        return (T)this;
    }

    public T help(String help) {
        this.help = help;
        return (T)this;
    }

    public T labels(String... labels) {
        this.labels = labels;
        return (T)this;
    }

    public T namespace(String namespace) {
        this.namespace = namespace;
        return (T)this;
    }

    public T subsystem(String subsystem) {
        this.subsystem = subsystem;
        return (T)this;
    }
}
