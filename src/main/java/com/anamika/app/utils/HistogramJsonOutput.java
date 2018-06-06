package com.anamika.app.utils;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * POJO for Json Output
 */
@Data
@Builder
public class HistogramJsonOutput {
    private final double pinMinimum, pinMaximum;

    /**
     * Ratio of values in a bucket, e.g. 1.1 means that the first bucket counts
     * values in the range [pinMinimum, pinMinimum * 1.1).
     */
    private final double bucketRatio;

    /**
     * Math.log(bucketRatio)
     */
    private final double logBucketRatio;

    /**
     * Number of samples in each histogram bucket.
     */
    @Singular
    private final List<Long> counts;

    /**
     * Sum of all sample values, *before* pinning to pinMinimum / pinMaximum. Can be divided by
     * totalCount to yield the mean sample value.
     */
    private final double totalValue;

    /**
     * Total number of samples (equal to the sum of the counts array).
     */
    private final long count;

    /**
     * Number of samples which reflected an error outcome. (A subset of totalCount.)
     */
    private final long errorCount;

    /**
     * Minimum and maximum sample values, *before* pinning to pinMinimum / pinMaximum. If
     * no samples have yet been provided, these fields hold 0.
     */
    private final double minValue, maxValue;

    private final double firstBucketStart;
}
