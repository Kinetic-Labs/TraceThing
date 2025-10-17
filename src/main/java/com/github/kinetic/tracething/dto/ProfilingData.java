package com.github.kinetic.tracething.dto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class ProfilingData {

    private static final ConcurrentHashMap<String, MethodStats> methodStats = new ConcurrentHashMap<>();

    public static void add(String method, long duration) {
        methodStats.computeIfAbsent(method, k -> new MethodStats()).record(duration);
    }

    public static ConcurrentHashMap<String, MethodStats> getMethodStats() {
        return methodStats;
    }

    public static class MethodStats {
        private final LongAdder count = new LongAdder();
        private final LongAdder totalDuration = new LongAdder();

        public void record(long duration) {
            count.increment();
            totalDuration.add(duration);
        }

        public long getCount() {
            return count.sum();
        }

        public long getTotalDuration() {
            return totalDuration.sum();
        }
    }
}
