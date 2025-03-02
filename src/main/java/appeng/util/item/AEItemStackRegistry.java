/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 AlgorithmX2
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package appeng.util.item;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class AEItemStackRegistry {

    private static final ObjectOpenHashSet<AESharedItemStack> REGISTRY = new ObjectOpenHashSet<>();

    private AEItemStackRegistry() {
    }

    static synchronized AESharedItemStack getRegisteredStack(@Nonnull final ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("stack cannot be empty");
        }

        final int stackSize = stack.getCount();
        stack.setCount(1);

        AESharedItemStack ret = REGISTRY.get(new AESharedItemStack(stack));

        if (ret != null) {
            stack.setCount(stackSize);
            return ret.incrementReferenceCount();
        }

        ret = new AESharedItemStack(stack.copy());
        stack.setCount(stackSize);

        REGISTRY.add(ret);
        return ret.incrementReferenceCount();
    }

    static synchronized void unregisterStack(@Nonnull final AESharedItemStack stack) {
        REGISTRY.remove(stack);
    }

}
