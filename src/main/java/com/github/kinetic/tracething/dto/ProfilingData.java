package com.github.kinetic.tracething.dto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Class to hold profiling data
 */
public final class ProfilingData {

    /**
     * The method invocation statistics
     */
    private static final ConcurrentHashMap<String, MethodStats> methodStats = new ConcurrentHashMap<>();

    /**
     * Add a method invocation to the profile data
     *
     * @param method   the method name
     * @param duration the duration of the method invocation in nanoseconds
     */
    public static void add(final String method, final long duration) {
        methodStats.computeIfAbsent(method, _ -> new MethodStats()).record(duration);
    }

    /**
     * Get the method invocation statistics
     *
     * @return the method invocation statistics
     */
    public static ConcurrentHashMap<String, MethodStats> getMethodStats() {
        return methodStats;
    }

    /**
     * Class to hold method invocation statistics
     */
    public static class MethodStats {

        /**
         * The number of method invocations and the total duration of all invocations
         */
        private final LongAdder count = new LongAdder();

        /**
         * The total duration of all invocations
         */
        private final LongAdder totalDuration = new LongAdder();

        /**
         * Record a method invocation
         *
         * @param duration the duration of the method invocation in nanoseconds
         */
        public void record(final long duration) {
            count.increment();
            totalDuration.add(duration);
        }

        /**
         * Get the number of method invocations
         *
         * @return the number of method invocations
         */
        public long getCount() {
            return count.sum();
        }

        /**
         * Get the total duration of all invocations
         *
         * @return the total duration of all invocations
         */
        public long getTotalDuration() {
            return totalDuration.sum();
        }
    }
}
