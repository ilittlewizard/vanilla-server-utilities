package com.github.ilittlewizard.vsu.command;

import com.github.ilittlewizard.vsu.VsuConfig;
import com.github.ilittlewizard.vsu.backup.AutoBackupConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AutoBackupCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("autosave")
                .requires(src -> src.hasPermissionLevel(4))
                .then(notifyError())
                .then(notifyInfo())
                .then(interval())
                .then(queueCapacity());
    }

    private static LiteralArgumentBuilder<ServerCommandSource> notifyError() {
        return VsuCommandHelper.registerBooleanPropertyCommand(
                "notifyError",
                AutoBackupConfig.getInstance()::setAutoBackupNotifyError,
                AutoBackupConfig.getInstance()::isAutoBackupNotifyError
        );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> notifyInfo() {
        return VsuCommandHelper.registerBooleanPropertyCommand(
                "notifyInfo",
                AutoBackupConfig.getInstance()::setAutoBackupNotifyInfo,
                AutoBackupConfig.getInstance()::isAutoBackupNotifyInfo
        );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> interval() {
        return VsuCommandHelper.registerIntegerPropertyCommand(
                "interval",
                AutoBackupConfig.getInstance()::setAutoBackupInterval,
                interval -> interval > 0,
                "interval must be a positive integer",
                AutoBackupConfig.getInstance()::getAutoBackupInterval
        );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> queueCapacity() {
        return VsuCommandHelper.registerIntegerPropertyCommand(
                "queueCapacity",
                AutoBackupConfig.getInstance()::setAutoBackupQueueCapacity,
                interval -> interval > 0,
                "queueCapacity must be a positive integer",
                AutoBackupConfig.getInstance()::getAutoBackupQueueCapacity
        );
    }
}
