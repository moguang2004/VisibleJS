package com.moguang.visiblejs.mixin.accessor;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mutable;

@Mixin(value = Slot.class)
public interface SlotAccessor {
    @Mutable
    @Accessor(value = "x")
    void visiblejs$setX(int x);

    @Mutable
    @Accessor(value = "y")
    void visiblejs$setY(int y);
}

