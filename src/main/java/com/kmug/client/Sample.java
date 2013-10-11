package com.kmug.client;

/**
 * @author Adrian Wolny
 */
public class Sample {
    private final String resource;
    private final double value;
    private final long timestamp;

    // for Jackson de-serialization
    Sample() {
        this(null, 0.0, 0L);
    }

    public Sample(String resource, double value, long timestamp) {
        this.resource = resource;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getResource() {
        return resource;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
