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

    private final int warmups;
    private final int repititions;
    private final boolean verbose;


    public AbstractPerfTest(final String[] args, final int warmups, final int defaultRepititions) {
        this.warmups = warmups;
        int rep = defaultRepititions;
        boolean v = false;
        for (String arg : args) {
            if (arg.matches("\\d+")) {
                rep = Integer.parseInt(arg);
            }
            else if (arg.startsWith("-v")) {
                v = true;
            }
        }
        this.verbose = v;
        this.repititions = rep;
    }

    protected void runPerfTest() throws Exception {
        Map<String, ResultsCollector> testCaseResults = new HashMap<>();

        for (int i = 0; i < warmups; i++) {
            runTestCases(null);
        }
        for (int i = 0; i < repititions; i++) {
            runTestCases(testCaseResults);
        }
        printResults(testCaseResults);
    }

    private void printResults(final Map<String, ResultsCollector> testCaseResults) {
        for (ResultsCollector resultsCollector : testCaseResults.values()) {
            System.out.println(resultsCollector);
        }
    }

    protected abstract Map<String, PerfTestCase> initPerfTestCases();

    private void runTestCases(final Map<String, ResultsCollector> testCaseResults) throws Exception {
        final Map<String, PerfTestCase> testCases = initPerfTestCases();
        for (Map.Entry<String, PerfTestCase> testCase : testCases.entrySet()) {
            gc();

            final String name = testCase.getKey();
            final PerfTestCase test = testCase.getValue();
            final ResultsCollector result = (testCaseResults == null) ? new ResultsCollector(name) : getResultsCollector(testCaseResults, name);

            println("Test " + name);

            try {
                test.before();
                test.test(result);
            } finally {
                test.after();
            }
        }
    }

    private void println(final Object value) {
        if (verbose) {
            System.out.println(value);
        }
    }

    private ResultsCollector getResultsCollector(final Map<String, ResultsCollector> testCaseResults, final String name) {
        ResultsCollector collector = testCaseResults.putIfAbsent(name, new ResultsCollector(name));
        if (collector == null) {
            collector = testCaseResults.get(name);
        }
        return collector;
    }

    protected interface PerfTestCase {

        void before();

        void test(ResultsCollector resultsCollector);

        void after();
    }

    protected static class ResultsCollector {

        private final String name;
        private final Map<String, List<Double>> resultsInMillisByActionMap;
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
            getResults(action).add((double) ((end - start) / invocations));
            start();
        }

        @Override
        public String toString() {
            return name + " " + resultsInMillisByActionMap + " nano sec. per action";
        }

        private List<Double> getResults(final String action) {
            List<Double> results = resultsInMillisByActionMap.get(action);
            if (results == null) {
                results = new ArrayList<>();
                resultsInMillisByActionMap.put(action, results);
            }
            return results;
        }
    }

    // Best efforts to force the VM to actually do a GC.
    private void gc() throws Exception {
        final Map<String, Long> gcCountByName = new HashMap<>();
        ManagementFactory.getGarbageCollectorMXBeans().stream().forEach(
            gcBean -> gcCountByName.put(gcBean.getName(), gcBean.getCollectionCount()));
        while (!collectorsWereActiveSince(gcCountByName)) {
            System.gc();

            println("GC Current State:"+ gcCountByName);

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
