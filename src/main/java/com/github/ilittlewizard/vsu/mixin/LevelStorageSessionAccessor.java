package com.github.ilittlewizard.vsu.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.SessionLock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelStorage.Session.class)
public interface LevelStorageSessionAccessor {
    @Accessor
    LevelStorage.LevelSave getDirectory();

    @Accessor
    SessionLock getLock();

    @Mutable
    @Accessor
    void setLock(SessionLock lock);
}
