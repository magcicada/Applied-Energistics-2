package appeng.server.tracker;

import appeng.me.Grid;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class PerformanceTracker {
    public static final PerformanceTracker INSTANCE = new PerformanceTracker();

    private final Map<Grid, Tracker> trackerMap = new Object2ObjectOpenHashMap<>();

    private PerformanceTracker() {

    }

    public void resetTracker() {
        trackerMap.clear();
    }

    public Map<Grid, Tracker> getTrackerMap() {
        return trackerMap;
    }

    public void startTracking(Grid g) {
        trackerMap.computeIfAbsent(g, v -> new Tracker(g)).startTracking();
    }

    public void endTracking(Grid g) {
        trackerMap.computeIfAbsent(g, v -> new Tracker(g)).endTracking();
    }
}
