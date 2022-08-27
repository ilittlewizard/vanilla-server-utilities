package com.github.ilittlewizard.vsu.command;

import com.github.ilittlewizard.vsu.VsuConfig;
import com.github.ilittlewizard.vsu.backup.AutoBackupConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sun.jdi.connect.Connector;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.*;

public class VsuCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("vsu")
                .requires(src -> src.hasPermissionLevel(4))
                .then(doAutoBackup());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> doAutoBackup() {
        return VsuCommandHelper.registerBooleanPropertyCommand(
                "doAutoBackup",
                VsuConfig.getInstance()::setDoAutoBackup,
                VsuConfig.getInstance()::isDoAutoBackup
        );
    }
}
