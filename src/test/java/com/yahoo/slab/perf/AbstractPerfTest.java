package com.yahoo.slab.perf;

import com.sun.management.GcInfo;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractPerfTest {

    private final String name;
    private final int warmups;
    private final int repititions;
    private final boolean verbose;


    public AbstractPerfTest(final String[] args, final int warmups, final int defaultRepititions) {
        this.warmups = warmups;
        int rep = defaultRepititions;
        boolean v = false;
        name = args[0];
        for (int i = 1; i < args.length; i++) {
            final String arg = args[i];
            if (arg.matches("\\d+")) {
                rep = Integer.parseInt(arg);
            } else if (arg.startsWith("-v")) {
                v = true;
            }
        }
        this.verbose = v;
        this.repititions = rep;
    }

    protected void runPerfTest() throws Exception {
        ResultsCollector resultsCollector = new ResultsCollector(name);

        for (int i = 0; i < warmups; i++) {
            runTestCases(new ResultsCollector("warmup"));
        }
        for (int i = 0; i < repititions; i++) {
            runTestCases(resultsCollector);
        }
        System.out.println(resultsCollector);
    }

    protected abstract PerfTestCase initPerfTestCases(final String name);

    private void runTestCases(final ResultsCollector resultsCollector) throws Exception {
        final PerfTestCase testCase = initPerfTestCases(name);
        gc();

        println("Test " + name);

        try {
            testCase.before();
            testCase.test(resultsCollector);
        } finally {
            testCase.after();
        }

    }

    private void println(final Object value) {
        if (verbose) {
            System.out.println(value);
        }
    }

    protected interface PerfTestCase {

        void before();

        void test(ResultsCollector resultsCollector);

        void after();
    }

    protected static class ResultsCollector {

        private final String name;
        private final Map<String, List<Result>> resultsInMillisByActionMap;
        private long start, end;

        private ResultsCollector(final String name) {
            this.name = name;
            this.resultsInMillisByActionMap = new HashMap<>();
        }

        protected void start() {
            start = System.nanoTime();
        }

        protected void end(String action, final int invocations) {
            end = System.nanoTime();
            getResults(action).add(new Result((end - start), invocations));
            start();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("\n").append(name).append(":");
            for (Map.Entry<String, List<Result>> entry : resultsInMillisByActionMap.entrySet()) {
                sb.append("\n\t").append(entry.getKey()).append(":");
                for (Result result : entry.getValue()) {
                    sb.append("\n\t\t").append(result);
                }
            }
            return sb.toString();
        }

        private List<Result> getResults(final String action) {
            List<Result> results = resultsInMillisByActionMap.get(action);
            if (results == null) {
                results = new ArrayList<>();
                resultsInMillisByActionMap.put(action, results);
            }
            return results;
        }

        private static final class Result {

            private final long invocations;
            private final long duration;
            private final long totalMemory;
            private final long freeMemory;

            private Result(final long duration, final long invocations) {
                this.duration = duration;
                this.invocations = invocations;
                this.totalMemory = Runtime.getRuntime().totalMemory();
                this.freeMemory = Runtime.getRuntime().freeMemory();
            }

            @Override
            public String toString() {
                return String.format("Avg. duration %d ns. for %d invocations. Memory %d total, %d free",
                        duration / invocations, invocations, totalMemory, freeMemory);
            }
        }
    }

    // Best efforts to force the VM to actually do a GC.
    private void gc() throws Exception {
        final Map<String, Long> gcCountByName = new HashMap<>();
        ManagementFactory.getGarbageCollectorMXBeans().stream().forEach(
            gcBean -> gcCountByName.put(gcBean.getName(), gcBean.getCollectionCount()));
        while (!collectorsWereActiveSince(gcCountByName)) {
            System.gc();

            println("GC Current State:" + gcCountByName);

            for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                GcInfo gcInfo = ((com.sun.management.GarbageCollectorMXBean) gcBean).getLastGcInfo();
                Map<String, MemoryUsage> memUsages = gcInfo.getMemoryUsageBeforeGc();
                println("GC " + gcBean.getName() + ":");
                for (Map.Entry<String, MemoryUsage> memUsage : memUsages.entrySet()) {
                    println(memUsage.getKey() + ": " + memUsage.getValue());
                }
            }

        }
    }

    private static boolean collectorsWereActiveSince(final Map<String, Long> gcCountByName) {
        return ManagementFactory.getGarbageCollectorMXBeans().stream().anyMatch(
            gcBean -> gcCountByName.get(gcBean.getName()) < gcBean.getCollectionCount());
    }

}
