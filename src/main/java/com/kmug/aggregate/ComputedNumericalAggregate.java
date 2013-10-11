package com.kmug.aggregate;

import java.util.Date;

/**
 * @author Adrian Wolny
 */
public class ComputedNumericalAggregate {
    public static ComputedNumericalAggregate fromNumericalAggregate(NumericalAggregate na) {
        double avg = 0.0;
        double stdDev = 0.0;
        if (na.getCount() != 0L) {
            avg = na.getSum() / na.getCount();
            stdDev = Math.sqrt(na.getCount() * na.getSqrSum() - na.getSum() * na.getSum()) / na.getCount();
        }
        return new ComputedNumericalAggregate(na.getDate(), avg, stdDev);
    }

    public static double round(double value) {
        return Math.round(value * 1000) / 1000;
    }

    private final Date date;
    private final double avg;
    private final double stdDev;

    public ComputedNumericalAggregate(Date date, double avg, double stdDev) {
        this.date = date;
        this.avg = round(avg);
        this.stdDev = round(stdDev);
    }

    public Date getDate() {
        return date;
    }

    public double getAvg() {
        return avg;
    }

    public double getStdDev() {
        return stdDev;
    }
}
