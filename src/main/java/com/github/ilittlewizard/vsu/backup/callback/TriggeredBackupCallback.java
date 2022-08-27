package com.github.ilittlewizard.vsu.backup.callback;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TriggeredBackupCallback implements BackupCallback {
    private final ServerCommandSource source;

    public TriggeredBackupCallback(ServerCommandSource source) {
        this.source = source;
    }

    @Override
    public void onError(String message)  {
        source.sendError(Text.of(message));
    }

    @Override
    public void onInfo(String message)  {
        source.sendFeedback(Text.of(message), false);
    }
}
