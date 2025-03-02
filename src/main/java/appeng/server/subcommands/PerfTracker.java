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
                sender.sendMessage(new TextComponentString("性能跟踪器已经重置。"));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "用法: /ae2 PerfTracker [reset]"));
            }
            return;
        }
        Map<Grid, Tracker> trackerMap = PerformanceTracker.INSTANCE.getTrackers();

        sender.sendMessage(new TextComponentString(String.format(
                "正在整理所有 AE 网络中 1 分钟内的数据 ...(总网络数量: %s)", trackerMap.size())
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
        int playerID = pivot.getPlayerID();
        EntityPlayer player = AEApi.instance().registries().players().findPlayer(playerID);
        int gridSize = grid.getNodes().size();

        sender.sendMessage(new TextComponentString(String.format("网络位置（中心）：%s，网络节点数量：%s", location, gridSize)));

        if (player == null) {
            sender.sendMessage(new TextComponentString(String.format("所有者：未知 (玩家 ID: %s)", playerID)));
        } else {
            sender.sendMessage(new TextComponentString(String.format("所有者：%s (UUID: %s)", player.getDisplayNameString(), player.getUniqueID())));
        }

        sender.sendMessage(new TextComponentString(String.format("平均 Tick 时间：%sms", (float) tracker.getTimeUsageAvg() / 1000F)));
        sender.sendMessage(new TextComponentString(String.format("最大 Tick 时间：%sms", (float) tracker.getMaxTimeUsage() / 1000F)));
        sender.sendMessage(new TextComponentString(""));
    }

}
