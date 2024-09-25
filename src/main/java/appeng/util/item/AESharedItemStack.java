/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.util.item;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


final class AESharedItemStack {

    private final ItemStack itemStack;
    private final int itemDamage;
    private final int hashCode;

    private final AtomicInteger referenceCount = new AtomicInteger();

    public AESharedItemStack(final ItemStack itemStack) {
        this(itemStack, itemStack.getItemDamage());
    }

    /**
     * A constructor to explicitly set the damage value and not fetch it from the {@link ItemStack}
     *
     * @param itemStack The {@link ItemStack} to filter
     * @param damage    The damage of the item
     */
    private AESharedItemStack(ItemStack itemStack, int damage) {
        this.itemStack = itemStack;
        this.itemDamage = damage;

        // Ensure this is always called last.
        this.hashCode = this.makeHashCode();
    }

    AESharedItemStack incrementReferenceCount() {
        this.referenceCount.decrementAndGet();
        return this;
    }

    void decrementReferenceCount() {
        if (this.referenceCount.decrementAndGet() <= 0) {
            AEItemStackRegistry.unregisterStack(this);
        }
    }

    ItemStack getDefinition() {
        return this.itemStack;
    }

    int getItemDamage() {
        return this.itemDamage;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final AESharedItemStack other)) {
            return false;
        }

        Preconditions.checkState(this.itemStack.getCount() == 1, "ItemStack#getCount() has to be 1");
        Preconditions.checkArgument(other.itemStack.getCount() == 1, "ItemStack#getCount() has to be 1");

        if (this.itemStack == other.itemStack) {
            return true;
        }
        return stackEquals(this.itemStack, other.itemStack);
    }

    private static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else if (stackA.isEmpty() && !stackB.isEmpty()) {
            return false;
        } else if (stackA.getItem() != stackB.getItem()) {
            return false;
        } else if (stackA.getItemDamage() != stackB.getItemDamage()) {
            return false;
        }

        NBTTagCompound stackATag = stackA.getTagCompound();
        NBTTagCompound stackBTag = stackB.getTagCompound();

        if (stackATag == null || stackATag.isEmpty()) {
            if (stackBTag != null && !stackBTag.isEmpty()) {
                return false; // stackA has no tag but stackB tag is not empty, is invalid.
            }
        } else {
            if (stackBTag == null || stackBTag.isEmpty()) {
                return false; // stackA has tag but stackB has no tag, is invalid.
            }
            if (!stackATag.equals(stackBTag)) {
                return false; // Different tag, is invalid.
            }
        }
        return stackA.areCapsCompatible(stackB);
    }

    private int makeHashCode() {
        final NBTTagCompound tag = this.itemStack.getTagCompound();
        final int combinedHashCode =  Long.hashCode(System.identityHashCode(this.itemStack.getItem()) & 0xFFFFFFFFL | (this.itemDamage & 0xFFFFFFFFL) << 32);
        final int tagHashCode = tag != null && !tag.isEmpty() ? tag.hashCode() : 0;
        return tagHashCode != 0 ? combinedHashCode ^ tagHashCode : combinedHashCode;
    }

}
