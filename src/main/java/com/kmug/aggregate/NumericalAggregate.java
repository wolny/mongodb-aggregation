package com.kmug.aggregate;

/**
 * @author Adrian Wolny
 */
public class NumericalAggregate extends Aggregate<NumericalAggregate> {
    private double sum;
    private double sqrSum;
    private long count;

    public double getSum() {
        return sum;
    }

    public double getSqrSum() {
        return sqrSum;
    }

    public long getCount() {
        return count;
    }

    public NumericalAggregate setSum(double sum) {
        this.sum = sum;
        return this;
    }

    public NumericalAggregate setSqrSum(double sqrSum) {
        this.sqrSum = sqrSum;
        return this;
    }

    public NumericalAggregate setCount(long count) {
        this.count = count;
        return this;
    }
}
