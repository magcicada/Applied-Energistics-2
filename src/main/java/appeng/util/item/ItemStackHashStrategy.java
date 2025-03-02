package appeng.util.item;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Original code by Eutro, butchered by Prototypetrousers.
 * A configurable generator of hashing strategies, allowing for consideration of select properties of ItemStacks when
 * considering equality.
 */
public interface ItemStackHashStrategy extends Hash.Strategy<ItemStack> {
    /**
     * @return a builder object for producing a custom ItemStackHashStrategy.
     */
    static ItemStackHashStrategyBuilder builder() {
        return new ItemStackHashStrategyBuilder();
    }

    /**
     * Generates an ItemStackHash configured to compare every aspect of ItemStacks except the number
     * of items in the stack.
     *
     * @return the ItemStackHashStrategy as described above.
     */
    static ItemStackHashStrategy comparingAllButCount() {
        return builder().compareItem(true)
                .compareDamage(true)
                .compareTag(true)
                .build();
    }

    /**
     * Builder pattern class for generating customized ItemStackHashStrategy
     */
    class ItemStackHashStrategyBuilder {
        private boolean item, damage, tag;

        /**
         * Defines whether the Item type should be considered for equality.
         *
         * @param choice {@code true} to consider this property, {@code false} to ignore it.
         * @return {@code this}
         */
        public ItemStackHashStrategyBuilder compareItem(boolean choice) {
            item = choice;
            return this;
        }

        /**
         * Defines whether damage values should be considered for equality.
         *
         * @param choice {@code true} to consider this property, {@code false} to ignore it.
         * @return {@code this}
         */
        public ItemStackHashStrategyBuilder compareDamage(boolean choice) {
            damage = choice;
            return this;
        }

        /**
         * Defines whether NBT Tags should be considered for equality.
         *
         * @param choice {@code true} to consider this property, {@code false} to ignore it.
         * @return {@code this}
         */
        public ItemStackHashStrategyBuilder compareTag(boolean choice) {
            tag = choice;
            return this;
        }

        /**
         * @return the ItemStackHashStrategy as configured by "compare" methods.
         */
        public ItemStackHashStrategy build() {
            return new ItemStackHashStrategy() {
                @Override
                public int hashCode(@Nullable ItemStack o) {
                    return o == null || o.isEmpty() ? 0 : Objects.hash(
                            item ? o.getItem() : null,
                            damage ? o.getItemDamage() : null,
                            tag ? o.getTagCompound() != null && !o.getTagCompound().isEmpty() ? o.getTagCompound() : null : null
                    );
                }

                @Override
                public boolean equals(@Nullable ItemStack a, @Nullable ItemStack b) {
                    if (a == null || a.isEmpty()) return b == null || b.isEmpty();
                    if (b == null || b.isEmpty()) return false;
                    return stackEquals(a, b);
                }
            };
        }

        private boolean stackEquals(ItemStack stackA, ItemStack stackB) {
            if (stackA.isEmpty() && stackB.isEmpty()) {
                return true;
            } else if (stackA.isEmpty() && !stackB.isEmpty()) {
                return false;
            } else if (item && stackA.getItem() != stackB.getItem()) {
                return false;
            } else if (damage && stackA.getItemDamage() != stackB.getItemDamage()) {
                return false;
            } else if (!tag) {
                return true;
            }
            NBTTagCompound stackATag = stackA.getTagCompound();
            NBTTagCompound stackBTag = stackB.getTagCompound();
            if ((stackATag == null || stackATag.isEmpty()) && (stackBTag != null && !stackBTag.isEmpty())) {
                // stackA has no tag but stackB tag is not empty, is invalid.
                return false;
            }
            if ((stackATag != null && !stackATag.isEmpty())) {
                if (stackBTag == null || stackBTag.isEmpty()) {
                    // stackA has tag but stackB has no tag, is invalid.
                    return false;
                }
                if (!stackATag.equals(stackBTag)) {
                    // Different tag, is invalid.
                    return false;
                }
            }
            return stackA.areCapsCompatible(stackB);
        }
    }
}

