package com.github.ilittlewizard.vsu.mixin;

import net.minecraft.world.level.storage.SessionLock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

@Mixin(SessionLock.class)
public interface SessionLockInvoker {
    @Invoker("create")
    static SessionLock create(Path path) {
        throw new AssertionError();
    }
}
