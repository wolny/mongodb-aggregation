package com.kmug.dao;

import com.kmug.aggregate.Aggregate;

import java.util.Date;
import java.util.List;

/**
 * @author Adrian Wolny
 */
public interface AggregatesRepository<T extends Aggregate> {

    List<T> getAggregates(String resource, Date start, Date end);

    void collect(T aggregate);
}
