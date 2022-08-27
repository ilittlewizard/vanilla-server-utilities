package com.github.ilittlewizard.vsu.backup;

import com.github.ilittlewizard.vsu.VsuConfig;
import com.github.ilittlewizard.vsu.backup.callback.BackupCallback;
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
    private final AutoBackupQueue backupQueue = new AutoBackupQueue(
            AutoBackupConfig.getInstance().getAutoBackupQueueCapacity());

    public AutoBackupManager(MinecraftServer server) {
        this.server = server;
    }

    private BackupCallback autoBackupCallback;
    private int timeElapsedMilliseconds = 0;

    public void startTickLoop() {
        ticker.scheduleAtFixedRate(() -> {
            if(!VsuConfig.getInstance().isDoAutoBackup()) {
                /* Reset timer */
                timeElapsedMilliseconds = 0;
                return;
            }

            timeElapsedMilliseconds += TICKING_INTERVAL;
            if(timeElapsedMilliseconds < AutoBackupConfig.getInstance().getAutoBackupInterval() * 1000) {
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
            ioThread.submit(() -> backup(autoBackupCallback, false));
        }, TICKING_INTERVAL, TICKING_INTERVAL,
                TimeUnit.MILLISECONDS);
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
        ioThread.submit(() -> backup(callback, true));
    }
    private void backup(BackupCallback callback, boolean isTriggered) {
        ioThreadStatus.set(true);
;
        callback.onInfo("Saving Server ...");
        if(!server.saveAll(false, true, true)) {
            callback.onError("Failed to save world; Refer to server log for more details");
            ioThreadStatus.set(false);
            return;
        }

        callback.onInfo(isTriggered ? "Creating Auto-Backup ..." : "Creating Backup ...");

        String backupFileName = AutoBackupConfig.getInstance().formatCurrentTime() + ".zip";
        Path savePath = server.getSavePath(WorldSavePath.ROOT);
        Path backupPath = FabricLoader.getInstance().getGameDir()
                .resolve("backups/");
        if(isTriggered) {
            backupPath = backupPath.resolve("manual");
        }

        File backupFile = backupPath
                .resolve(backupFileName)
                .toFile();
        try {
            AutoBackupIO.makeParentDirectories(backupFile);
            AutoBackupIO.closeSessionLock(server);
            AutoBackupIO.createBackup(savePath, backupFile);
            AutoBackupIO.createSessionLock(server);

            if(!isTriggered) {
                File oldBackup = backupQueue.enqueue(backupFile);
                if(oldBackup != null && oldBackup.isFile()) {
                    // If it cannot be deleted, then we will just leave it alone
                    oldBackup.delete();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create backup", e);
            callback.onError("Failed to create backup due to an unexpected exception; " +
                    "Refer to server log for more detail");
            ioThreadStatus.set(false);
            return;
        }

        callback.onInfo("Backup created: /backups/" +
                (isTriggered ? "manual/" : "") +
                backupFileName);

        ioThreadStatus.set(false);
    }
}
