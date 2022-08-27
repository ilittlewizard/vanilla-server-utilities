package com.github.ilittlewizard.vsu;

import com.github.ilittlewizard.vsu.backup.AutoBackupManager;
import com.github.ilittlewizard.vsu.backup.BackupCallback;
import com.github.ilittlewizard.vsu.backup.TriggeredBackupCallback;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class VsuMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("vanilla-server-utilities");
	private static AutoBackupManager manager;
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			manager = new AutoBackupManager(server);
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(getBackupCommand())
		);
	}

	private LiteralArgumentBuilder<ServerCommandSource> getBackupCommand() {
		return literal("backup")
				.requires(src -> src.hasPermissionLevel(4))
				.executes(context -> {
					manager.triggerBackup(new TriggeredBackupCallback(context.getSource()));
					return 0;
				});
	}
}
