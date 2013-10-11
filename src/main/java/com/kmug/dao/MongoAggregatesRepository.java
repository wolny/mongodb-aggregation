package com.kmug.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.kmug.aggregate.Aggregate;
import com.kmug.aggregate.NumericalAggregate;
import com.kmug.aggregate.TimeSpan;
import com.mongodb.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Adrian Wolny
 */
public class MongoAggregatesRepository implements AggregatesRepository<NumericalAggregate> {
    public static final String AGGREGATES_DB = "aggregates";
    public static final String ID_SEPARATOR = "/";

    public static final TimeSpan[] SPANS = {TimeSpan.HOURLY, TimeSpan.DAILY, TimeSpan.MONTHLY};
    public static final String ID_FIELD = "_id";
    public static final String RESOURCE_FIELD = "resource";
    public static final String DATE_FIELD = "date";
    public static final String COUNT_FIELD = "count";
    public static final String SUM_FIELD = "sum";
    public static final String SQR_SUM_FIELD = "sqrSum";
    public static final String MINUTE_FIELD = "minute";
    public static final String HOUR_FIELD = "hour";
    public static final String DAY_FIELD = "day";

    private static final Map<TimeSpan, UpdateObject> UPDATE_OBJECT_MAP =
            ImmutableMap.of(TimeSpan.HOURLY, new HourlyUpdate(),
                    TimeSpan.DAILY, new DailyUpdate(),
                    TimeSpan.MONTHLY, new MonthlyUpdate());

    private final DB aggregatesDb;

    @Autowired
    public MongoAggregatesRepository(Mongo mongo) {
        aggregatesDb = mongo.getDB(AGGREGATES_DB);
        ensureIndexes();
    }

    public DB getAggregatesDb() {
        return aggregatesDb;
    }

    @Override
    public List<NumericalAggregate> getAggregates(String resource, final Date start, final Date end) {
        TimeSpan daySpan = TimeSpan.DAILY;
        List<NumericalAggregate> result = Lists.newLinkedList();
        Date periodStart = daySpan.getPeriodStart(start);
        Date periodEnd = daySpan.getPeriodStart(end);
        if (!periodEnd.equals(end)) {
            periodEnd = daySpan.addToDate(periodEnd);
        }

        for (DBObject dbObj : getCollection(daySpan).find(getFindQuery(resource, periodStart, periodEnd))) {
            result.addAll(extractAggregates(dbObj));
        }

        // filter aggregates
        Iterators.removeIf(result.iterator(), new Predicate<NumericalAggregate>() {
            @Override
            public boolean apply(NumericalAggregate input) {
                Date date = input.getDate();
                Date periodStart = TimeSpan.HOURLY.getPeriodStart(start);
                return date.before(periodStart) || date.after(end);
            }
        });

        Collections.sort(result, Aggregate.COMPARATOR);
        return result;
    }

    private DBObject getFindQuery(String resource, Date start, Date end) {
        return QueryBuilder.start(RESOURCE_FIELD).is(resource)
                .and(DATE_FIELD).greaterThanEquals(start)
                .and(DATE_FIELD).lessThanEquals(TimeSpan.DAILY.addToDate(end))
                .get();
    }

    private List<NumericalAggregate> extractAggregates(DBObject dbObj) {
        List<NumericalAggregate> result = Lists.newArrayList();
        Date startDate = (Date) dbObj.get(DATE_FIELD);
        DBObject dayByHour = (DBObject) dbObj.get(HOUR_FIELD);
        for (String hour : dayByHour.keySet()) {
            DBObject hourObj = (DBObject) dayByHour.get(hour);
            NumericalAggregate agg = createNumericalAggregate(hourObj);
            DateTime dt = new DateTime(startDate).plusHours(Integer.valueOf(hour));
            agg.setDate(dt.toDate());
            result.add(agg);
        }
        return result;
    }

    private NumericalAggregate createNumericalAggregate(DBObject hourObj) {
        return new NumericalAggregate()
                .setCount((Long) hourObj.get(COUNT_FIELD))
                .setSum((Double) hourObj.get(SUM_FIELD))
                .setSqrSum((Double) hourObj.get(SQR_SUM_FIELD));
    }


    @Override
    public void collect(NumericalAggregate aggregate) {
        System.out.println("Collecting aggregate: " + aggregate.getResource() + " " + aggregate.getSum() + " " + aggregate.getDate());
        for (TimeSpan span : SPANS) {
            update(aggregate, span);
        }
    }

    private void update(NumericalAggregate aggregate, TimeSpan span) {
        DBObject query = getQuery(aggregate, span);
        DBObject update = updateObject(span).get(aggregate.getDate(), aggregate.getSum());
        // create aggregate if does not exist -> upsert:true
        getCollection(span).update(query, update, true, false);
    }

    private UpdateObject updateObject(TimeSpan span) {
        return UPDATE_OBJECT_MAP.get(span);
    }

    private DBObject getQuery(NumericalAggregate aggregate, TimeSpan span) {
        DBObject result = new BasicDBObject(ID_FIELD, idFor(aggregate, span));
        // remaining attributes are needed for upsert !
        result.put(RESOURCE_FIELD, aggregate.getResource());
        result.put(DATE_FIELD, span.getPeriodStart(aggregate.getDate()));
        return result;
    }

    private void ensureIndexes() {
        // ensure indexes
        for (TimeSpan span : SPANS) {
            DBObject obj = new BasicDBObject(RESOURCE_FIELD, 1);
            obj.put(DATE_FIELD, 1);
            getCollection(span).ensureIndex(obj);
        }
    }

    private DBCollection getCollection(TimeSpan span) {
        return getAggregatesDb().getCollection(span.toString().toLowerCase());
    }

    private String idFor(Aggregate aggregate, TimeSpan span) {
        return aggregate.getResource() + ID_SEPARATOR + span.getPeriodStart(aggregate.getDate()).getTime();
    }
}
