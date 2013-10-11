package com.kmug.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

/**
 * @author Adrian Wolny
 */
public abstract class UpdateObject {
    public abstract DBObject get(Date date, double value);

    protected DBObject createUpdateObject(String prefix, double value) {
        DBObject result = new BasicDBObject();
        result.put(prefix + MongoAggregatesRepository.COUNT_FIELD, 1L);
        result.put(prefix + MongoAggregatesRepository.SUM_FIELD, value);
        result.put(prefix + MongoAggregatesRepository.SQR_SUM_FIELD, value * value);
        return result;
    }
}
