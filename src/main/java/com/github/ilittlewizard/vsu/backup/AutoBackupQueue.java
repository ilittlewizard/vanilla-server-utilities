package com.github.ilittlewizard.vsu.backup;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Since auto-backup can take up disk space really fast, a "backup queue"
 * is needed to delete backup(s) and free up disk space. <br><br>
 *
 * The backup queue is a queue-like structure that has a FIFO manner.
 * Newly-created backups are inserted into the queue and once the amount
 * of backups had exceed the capacity, the oldest backup will be deleted. <br><br>
 *
 * That way, the amount of backup(s) on disk will be kept to a
 * constant amount, which is same as the length of the backup queue. <br><br>
 *
 * This class is not thread-safe since it is only intended to be
 * accessed in the I/O thread.
 */
public class AutoBackupQueue {
    private LinkedList<File> backups = new LinkedList<>();
    private int capacity;

    public AutoBackupQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Record a new backup
     * @param backup The new backup file
     * @return The oldest backup that have to be deleted, {@code null} otherwise
     */
    public @Nullable File enqueue(File backup) {
        backups.offer(backup);
        if(backups.size() > capacity) {
            // The oldest backup have to be deleted
            return backups.poll();
        }

        return null;
    }

    /**
     * Resize the queue. <br>
     * If the new capacity is smaller than the size of the queue, part of
     * the queue have to be "trimmed" and the trimmed part will be returned.
     *
     * @param capacity The new capacity of the queue
     * @return The trimmed part of the old queue, empty if the queue is not trimmed
     */
    public List<File> resize(int capacity) {
        this.capacity = capacity;
        if(capacity >= backups.size()) {
            // None of the queue have to be trimmed
            return new ArrayList<>();
        }

        int trimmedLength = backups.size() - capacity;
        List<File> trimmed = backups.subList(0, trimmedLength);
        backups = new LinkedList<>(backups.subList(trimmedLength, backups.size()));
        return trimmed;
    }
}
