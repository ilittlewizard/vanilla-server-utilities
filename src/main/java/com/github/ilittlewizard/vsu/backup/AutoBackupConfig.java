package com.github.ilittlewizard.vsu.backup;

import com.github.ilittlewizard.vsu.VsuConfig;
import com.github.ilittlewizard.vsu.backup.AutoBackupQueue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoBackupConfig extends VsuConfig {
    private static final String NAME = "backup";

    public AutoBackupConfig() {
        super(NAME);
    }

    private static final AutoBackupConfig INSTANCE =
            load(NAME, AutoBackupConfig.class, new AutoBackupConfig());

    public static AutoBackupConfig getInstance() {
        return INSTANCE;
    }

    private int backupInterval = 60;
    private int backupQueueCapacity = 5;
    private String backupDateTimeFormat = "yyyy-MM-dd_HH-mm-ss-SSS";

    private transient DateFormat dateTimeFormat = new SimpleDateFormat(backupDateTimeFormat);

    /**
     * @return The interval of auto-backup, in second(s)
     */
    public int getBackupInterval() {
        return backupInterval;
    }

    /**
     * @param backupInterval The interval of auto-backup, in second(s)
     */
    public void setBackupInterval(int backupInterval) {
        this.backupInterval = backupInterval;
        save();
    }

    /**
     *
     * Return the length of the backup queue. <br>
     * @see AutoBackupQueue
     *
     * @return The length of backup queue
     */
    public int getBackupQueueCapacity() {
        return backupQueueCapacity;
    }

    public void setBackupQueueCapacity(int backupQueueCapacity) {
        this.backupQueueCapacity = backupQueueCapacity;
        save();
    }

    /**
     * Return the date time format of the backup's .zip file
      * @return The date time format of the backup file, without ".zip" extension
     */
    public String getBackupDateTimeFormat() {
        return backupDateTimeFormat;
    }

    public void setBackupDateTimeFormat(String backupDateTimeFormat) {
        this.backupDateTimeFormat = backupDateTimeFormat;
        this.dateTimeFormat = new SimpleDateFormat(backupDateTimeFormat);
        save();
    }

    public String formatCurrentTime() {
        return dateTimeFormat.format(new Date());
    }
}
