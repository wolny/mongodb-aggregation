package com.kmug.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author Adrian Wolny
 */
public class MonthlyUpdate extends UpdateObject {
    @Override
    public DBObject get(Date date, double value) {
        int minute = new DateTime(date).getDayOfMonth();
        DBObject obj = createUpdateObject(String.format(MongoAggregatesRepository.DAY_FIELD + ".%d.", minute), value);
        return new BasicDBObject("$inc", obj);
    }
}
