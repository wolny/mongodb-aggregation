package com.kmug.aggregate;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Adrian Wolny
 */
public abstract class Aggregate<T extends Aggregate<T>> {
    public static final Comparator<Aggregate> COMPARATOR = new Comparator<Aggregate>() {
        @Override
        public int compare(Aggregate o1, Aggregate o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    };
    private Date date;
    private String resource;

    public Date getDate() {
        return date;
    }

    public String getResource() {
        return resource;
    }

    public T setDate(Date date) {
        this.date = date;
        return cast(this);
    }

    public T setResource(String resource) {
        this.resource = resource;
        return cast(this);
    }

    @SuppressWarnings("unchecked")
    private T cast(Aggregate<T> aggregate) {
        return (T) aggregate;
    }
}
