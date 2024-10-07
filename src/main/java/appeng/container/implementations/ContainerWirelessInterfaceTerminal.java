package appeng.container.implementations;

import appeng.api.networking.security.IActionHost;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.helpers.WirelessTerminalGuiObject;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerWirelessInterfaceTerminal extends ContainerInterfaceTerminal implements IInventorySlotAware {
    private final WirelessTerminalGuiObject wirelessTerminalGUIObject;
    private final int slot;

    public ContainerWirelessInterfaceTerminal(InventoryPlayer ip, WirelessTerminalGuiObject guiObject) {
        super(ip, guiObject);

        if (guiObject != null) {
            final int slotIndex = guiObject.getInventorySlot();
            if (!guiObject.isBaubleSlot()) {
                this.lockPlayerInventorySlot(slotIndex);
            }
            this.slot = slotIndex;
        } else {
            this.slot = -1;
            this.lockPlayerInventorySlot(ip.currentItem);
        }

        this.bindPlayerInventory(ip, 0, 0);

        this.wirelessTerminalGUIObject = guiObject;
    }

    @Override
    protected IActionHost getActionHost() {
        return wirelessTerminalGUIObject;
    }

    @Override
    public int getInventorySlot() {
        return wirelessTerminalGUIObject.getInventorySlot();
    }

    @Override
    public boolean isBaubleSlot() {
        return wirelessTerminalGUIObject.isBaubleSlot();
    }

}
