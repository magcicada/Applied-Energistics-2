package appeng.server.subcommands;

import appeng.api.AEApi;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;
import appeng.me.Grid;
import appeng.server.ISubCommand;
import appeng.server.tracker.PerformanceTracker;
import appeng.server.tracker.Tracker;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;

public class PerfTracker implements ISubCommand {

    @Override
    public String getHelp(final MinecraftServer srv) {
        return null;
    }

    @Override
    public void call(final MinecraftServer srv, final String[] args, final ICommandSender sender) {
        if (args.length == 2) {
            if ("reset".equals(args[1])) {
                PerformanceTracker.INSTANCE.resetTracker();
                sender.sendMessage(new TextComponentString("PerformanceTracker has been reset."));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /ae2 PerfTracker [reset]"));
            }
            return;
        }
        Map<Grid, Tracker> trackerMap = PerformanceTracker.INSTANCE.getTrackerMap();

        sender.sendMessage(new TextComponentString(String.format(
                "Grid tracker result in 1 minute data is being organized...(Total Grids: %s)", trackerMap.size())
        ));

        trackerMap.values().stream()
                .filter(t -> !t.getGrid().isEmpty())
                .sorted((o1, o2) -> Integer.compare(o2.getTimeUsageAvg(), o1.getTimeUsageAvg()))
                .limit(10)
                .forEach(t -> sendTrackerTimeUsage(t, sender));
    }

    public static void sendTrackerTimeUsage(final Tracker tracker, final ICommandSender sender) {
        Grid grid = tracker.getGrid();

        IGridNode pivot = grid.getPivot();
        DimensionalCoord location = pivot.getGridBlock().getLocation();
        EntityPlayer player = AEApi.instance().registries().players().findPlayer(pivot.getPlayerID());
        int gridSize = grid.getNodes().size();

        sender.sendMessage(new TextComponentString(String.format("Grid location: %s, Size: %s", location, gridSize)));

        if (player == null) {
            sender.sendMessage(new TextComponentString("Owner: Unknown"));
        } else {
            sender.sendMessage(new TextComponentString(String.format("Owner: %s (UUID: %s)", player.getDisplayNameString(), player.getUniqueID())));
        }

        sender.sendMessage(new TextComponentString(String.format("Tick time usage: %sms", (float) tracker.getTimeUsageAvg() / 1000F)));
        sender.sendMessage(new TextComponentString(String.format("Max tick time usage: %sms", (float) tracker.getMaxTimeUsage() / 1000F)));
        sender.sendMessage(new TextComponentString(""));
    }

}
