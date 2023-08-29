package appeng.server.tracker;

import appeng.me.Grid;

import java.util.LinkedList;

public class Tracker {
    private final Grid grid;
    private final LinkedList<Integer> timeUsages = new LinkedList<>();
    private int timeUsageCache = 0;

    private long startTime = 0;

    public Tracker(final Grid grid) {
        this.grid = grid;
    }

    public void startTracking() {
        startTime = System.nanoTime() / 1000;
    }

    public void endTracking() {
        updateTimeUsage((System.nanoTime() / 1000) - startTime);
    }

    public Grid getGrid() {
        return grid;
    }

    public int getTimeUsageAvg() {
        if (timeUsages.isEmpty()) {
            return 0;
        }

        return timeUsageCache / timeUsages.size();
    }

    public int getMaxTimeUsage() {
        if (timeUsages.isEmpty()) {
            return 0;
        }

        return timeUsages.stream()
                .mapToInt(usage -> usage)
                .max().getAsInt();
    }

    private void updateTimeUsage(final long time) {
        int t = (int) time;
        timeUsages.addFirst(t);
        timeUsageCache += t;

        if (timeUsages.size() > 1200) {
            timeUsageCache -= timeUsages.pollLast();
        }
    }

}
