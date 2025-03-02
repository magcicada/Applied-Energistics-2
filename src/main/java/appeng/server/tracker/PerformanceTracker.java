package appeng.server.tracker;

import appeng.me.Grid;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class PerformanceTracker {
    public static final PerformanceTracker INSTANCE = new PerformanceTracker();

    private Map<Grid, Tracker> trackers = new Object2ObjectOpenHashMap<>();
    private Map<Grid, Tracker> tracking = new Object2ObjectOpenHashMap<>();

    private PerformanceTracker() {

    }

    public void resetTracker() {
        trackers.clear();
        tracking.clear();
    }

    public Map<Grid, Tracker> getTrackers() {
        return trackers;
    }

    public void startTracking() {
        this.trackers.clear();
        this.trackers = tracking;
        this.tracking = new Object2ObjectOpenHashMap<>();
    }

    public void startTracking(Grid g) {
        Tracker tracker = trackers.computeIfAbsent(g, v -> new Tracker(g));
        tracker.startTracking();
        tracking.put(g, tracker);
    }

    public void endTracking(Grid g) {
        Tracker tracker = tracking.computeIfAbsent(g, v -> new Tracker(g));
        tracker.endTracking();
    }
}
