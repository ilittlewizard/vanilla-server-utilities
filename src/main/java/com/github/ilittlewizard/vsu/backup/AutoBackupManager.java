package com.github.ilittlewizard.vsu.backup;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoBackupManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TICKING_INTERVAL = 1000 / 5;
    private final ScheduledExecutorService ticker = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService ioThread = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean ioThreadStatus = new AtomicBoolean(false);
    private final MinecraftServer server;

    public AutoBackupManager(MinecraftServer server) {
        this.server = server;
    }

    private BackupCallback autoBackupCallback;
    private int backupInterval = 60;
    private int timeElapsedMilliseconds = 0;

    public void startTickLoop() {
        ticker.scheduleAtFixedRate(() -> {
            timeElapsedMilliseconds += TICKING_INTERVAL;
            if(timeElapsedMilliseconds < backupInterval * 1000) {
                /* Continue ticking */
                return;
            }

            if(ioThreadStatus.get()) {
                /* IO Thread busy, continue waiting */
                autoBackupCallback.onError("Auto-backup triggered while IO thread is busy. " +
                        "Consider lengthen the auto-backup interval");
                return;
            }

            timeElapsedMilliseconds = 0;
            ioThread.submit(() -> backup(autoBackupCallback));
        }, TICKING_INTERVAL, TICKING_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public void stopTickLoop() {
        ticker.shutdown();
    }

    public int getBackupInterval() {
        return backupInterval;
    }

    public void setBackupInterval(int backupInterval) {
        this.backupInterval = backupInterval;
    }

    public void setAutoBackupCallback(BackupCallback autoBackupCallback) {
        this.autoBackupCallback = autoBackupCallback;
    }

    public void triggerBackup(BackupCallback callback) {
        if(ioThreadStatus.get()) {
            /* IO Thread busy, warn user */
            callback.onError("IO thread is busy; please retry later");
            return;
        }

        timeElapsedMilliseconds = 0;
        ioThread.submit(() -> backup(callback));
    }
    private void backup(BackupCallback callback) {
        ioThreadStatus.set(true);

        setServerAutoSaving(false);
        callback.onInfo("Saving Server ...");
        if(!server.saveAll(false, true, true)) {
            callback.onError("Failed to save world; Refer to server log for more details");
            ioThreadStatus.set(false);
            return;
        }

        callback.onInfo("Creating Backup ...");
        String backupFileName = AutoBackupConfig.getInstance().formatCurrentTime() + ".zip";
        try {
            Path savePath = server.getSavePath(WorldSavePath.ROOT);
            File backupFile = FabricLoader.getInstance().getGameDir()
                    .resolve("backups/")
                    .resolve(backupFileName)
                    .toFile();
            AutoBackupIO.makeParentDirectory(backupFile);

            AutoBackupIO.closeSessionLock(server);
            AutoBackupIO.createBackup(savePath, backupFile);
            AutoBackupIO.createSessionLock(server);
        } catch (Exception e) {
            LOGGER.error("Failed to create backup", e);
            callback.onError("Failed to create backup due to an unexpected IOException; " +
                    "Refer to server log for more detail");
            ioThreadStatus.set(false);
            return;
        }

        setServerAutoSaving(true);
        callback.onInfo("Backup created: " + backupFileName);
        ioThreadStatus.set(false);
    }

    private void setServerAutoSaving(boolean enabled) {
        for (ServerWorld serverWorld : server.getWorlds()) {
            if (serverWorld != null)
                serverWorld.savingDisabled = !enabled;
        }
    }
}
