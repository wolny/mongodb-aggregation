package com.kmug.service;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kmug.aggregate.ComputedNumericalAggregate;
import com.kmug.aggregate.NumericalAggregate;
import com.kmug.dao.AggregatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Adrian Wolny
 */
@Service
public class AggregatesService {
    private static final Function<NumericalAggregate, ComputedNumericalAggregate> AGGREGATE_TRANSFORMER =
            new Function<NumericalAggregate, ComputedNumericalAggregate>() {
                @Override
                public ComputedNumericalAggregate apply(NumericalAggregate input) {
                    return ComputedNumericalAggregate.fromNumericalAggregate(input);
                }
            };

    private final AggregatesRepository<NumericalAggregate> aggregatesRepository;

    @Autowired
    public AggregatesService(AggregatesRepository<NumericalAggregate> aggregatesRepository) {
        this.aggregatesRepository = aggregatesRepository;
    }

    public AggregatesRepository<NumericalAggregate> getAggregatesRepository() {
        return aggregatesRepository;
    }

    public List<ComputedNumericalAggregate> getNumericalSeries(String resource, Date start, Date end) {
        return Lists.newArrayList(Iterables.transform(
                getAggregatesRepository().getAggregates(resource, start, end),
                AGGREGATE_TRANSFORMER));
    }

    public void collectNumerical(String resource, double value, Date date) {
        NumericalAggregate numericalAggregate = new NumericalAggregate()
                .setSum(value)
                .setSqrSum(value * value)
                .setCount(1)
                .setResource(resource)
                .setDate(date);
        getAggregatesRepository().collect(numericalAggregate);
    }
}
