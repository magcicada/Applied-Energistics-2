package appeng.helpers;

import appeng.util.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerHelper {
	@Nullable
	public static EntityPlayerMP getPlayerByUUID(UUID uuid) {
		final MinecraftServer server;
		if (Platform.isClientInstall()) {
			server = getIntegratedServer();
		} else {
			server = getDedicatedServer();
		}

		if (server == null) {
			return null;
		}

		return server.getPlayerList().getPlayerByUUID(uuid);
	}

	@SideOnly(Side.CLIENT)
	public static MinecraftServer getIntegratedServer() {
		return Minecraft.getMinecraft().getIntegratedServer();
	}

	@SideOnly(Side.SERVER)
	public static MinecraftServer getDedicatedServer() {
		return FMLServerHandler.instance().getServer();
	}
}
