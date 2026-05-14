package com.moguang.visiblejs.mixin.accessor;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Slot.class)
public interface SlotAccessor {
    @Accessor(value = "x")
    void visiblejs$setX(int x);

    @Accessor(value = "y")
    void visiblejs$setY(int y);
}

