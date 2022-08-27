package com.github.ilittlewizard.vsu.backup;

import com.github.ilittlewizard.vsu.mixin.LevelStorageSessionAccessor;
import com.github.ilittlewizard.vsu.mixin.MinecraftServerAccessor;
import com.github.ilittlewizard.vsu.mixin.SessionLockInvoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.SessionLock;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Contains helper methods for backup I/O logics.
 * Caller must handle the IOException that might be thrown.
 */
public class AutoBackupIO {

    /**
     * Release the session lock of a server
     * @param server The minecraft server to process
     * @throws IOException If the session lock failed to close
     */
    public static void closeSessionLock(MinecraftServer server) throws IOException {
        LevelStorage.Session session = ((MinecraftServerAccessor) server).getSession();
        SessionLock sessionLock = ((LevelStorageSessionAccessor) session).getLock();
        sessionLock.close();
    }


    /**
     * Create a session lock for a server
     * @param server The minecraft server to process
     */
    public static void createSessionLock(MinecraftServer server) {
        LevelStorageSessionAccessor sessionAccessor = (LevelStorageSessionAccessor)
                ((MinecraftServerAccessor) server).getSession();
        SessionLock lock = SessionLockInvoker.create(sessionAccessor.getDirectory().path());
        sessionAccessor.setLock(lock);
    }


    /**
     * Create a file's parent directory
     * @param file The file to make its parent directory
     * @return true if its parent directory is successfully created
     */
    public static boolean makeParentDirectories(File file) {
        return file.getParentFile().mkdirs();
    }
    /**
     * Create a backup of a world
     * @param savePath The path to the world folder;
     *                 for single-player/ LAN servers, it is often located in /%game-instance%/saves/%world-name%/;
     *                 for dedicated server, it is located in /%server-root%/%level-name%/.
     * @param backup  The path to the .zip backup file;
     *                This method will create the backup file if it doesn't exists,
     *                otherwise, it will be overwritten without any warning
     * @throws IOException If encountered any {@link IOException}
     */
    public static void createBackup(final Path savePath, final File backup) throws IOException {
        try (
                OutputStream backupStream = new FileOutputStream(backup, false);
                ZipOutputStream zipBackupStream = new ZipOutputStream(backupStream)) {
            zipFolderRecursive(savePath.toFile(), zipBackupStream, savePath.toString().length() + 1);
        }
    }

    /**
     * Zip a folder and write to a {@link ZipOutputStream}
     * @param folder The folder to zip
     * @param out The {@link ZipOutputStream} to write to
     * @param prefixLength The length of the folder's name, including the ending dash;
     *                     This is used to obtain the files' name
     * @throws IOException If encountered any {@link IOException}
     */
    private static void zipFolderRecursive(final File folder, final ZipOutputStream out, final int prefixLength)
            throws IOException {
        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipFolderRecursive(file, out, prefixLength);
                continue;
            }

            final ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
            out.putNextEntry(zipEntry);
            try (FileInputStream inputStream = new FileInputStream(file)) {
                IOUtils.copy(inputStream, out);
            }
            out.closeEntry();
        }
    }
}
