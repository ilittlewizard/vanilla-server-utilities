package com.github.ilittlewizard.vsu;

import com.github.ilittlewizard.vsu.backup.AutoBackupConfig;
import com.github.ilittlewizard.vsu.backup.AutoBackupManager;
import com.github.ilittlewizard.vsu.backup.callback.BackupCallback;
import com.github.ilittlewizard.vsu.backup.callback.TriggeredBackupCallback;
import com.github.ilittlewizard.vsu.command.AutoBackupCommand;
import com.github.ilittlewizard.vsu.command.VsuCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

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
			manager.setAutoBackupCallback(new BackupCallback() {
				@Override
				public void onError(String message) {
					if(AutoBackupConfig.getInstance().isAutoBackupNotifyError()) {
						Style style = Style.EMPTY
								.withColor(TextColor.fromRgb(Color.RED.getRGB()));
						Text error = Text.of(message).copyContentOnly().setStyle(style);
						for(ServerPlayerEntity player: server.getPlayerManager().getPlayerList()) {
							if(player.hasPermissionLevel(4)) {
								player.sendMessage(error);
							}
						}
					}
				}

				@Override
				public void onInfo(String message) {
					if(AutoBackupConfig.getInstance().isAutoBackupNotifyInfo()) {
						Style style = Style.EMPTY
								.withColor(TextColor.fromRgb(Color.LIGHT_GRAY.getRGB()))
								.withItalic(true);
						Text info = Text.of(message).copyContentOnly().setStyle(style);
						for(ServerPlayerEntity player: server.getPlayerManager().getPlayerList()) {
							if(player.hasPermissionLevel(4)) {
								player.sendMessage(info);
							}
						}
					}
				}
			});
			manager.startTickLoop();
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(getBackupCommand());
			dispatcher.register(VsuCommand.register());
			dispatcher.register(AutoBackupCommand.register());
		});
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
