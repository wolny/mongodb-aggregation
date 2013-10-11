package com.kmug.aggregate;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author Adrian Wolny
 */
public enum TimeSpan {
    MINUTELY {
        @Override
        public Date getPeriodStart(Date date) {
            DateTime dt = new DateTime(date);
            return new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dt.getHourOfDay(), dt.getMinuteOfHour(), 0, 0).toDate();
        }

        @Override
        public Date addToDate(Date date) {
            DateTime dt = new DateTime(date);
            return dt.plusMinutes(1).toDate();
        }
    },
    HOURLY {
        @Override
        public Date getPeriodStart(Date date) {
            DateTime dt = new DateTime(date);
            return new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), dt.getHourOfDay(), 0, 0, 0).toDate();
        }

        @Override
        public Date addToDate(Date date) {
            DateTime dt = new DateTime(date);
            return dt.plusHours(1).toDate();
        }
    },
    DAILY {
        @Override
        public Date getPeriodStart(Date date) {
            DateTime dt = new DateTime(date);
            return new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 0, 0, 0, 0).toDate();
        }

        @Override
        public Date addToDate(Date date) {
            DateTime dt = new DateTime(date);
            return dt.plusDays(1).toDate();
        }
    },
    MONTHLY {
        @Override
        public Date getPeriodStart(Date date) {
            DateTime dt = new DateTime(date);
            return new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0, 0).toDate();
        }

        @Override
        public Date addToDate(Date date) {
            DateTime dt = new DateTime(date);
            return dt.plusMonths(1).toDate();
        }
    };

    public abstract Date getPeriodStart(Date date);

    public abstract Date addToDate(Date date);
}
