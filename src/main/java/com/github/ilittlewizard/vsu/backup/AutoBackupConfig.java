package com.github.ilittlewizard.vsu.backup;

import com.github.ilittlewizard.vsu.AbstractVsuConfig;
import com.github.ilittlewizard.vsu.VsuConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoBackupConfig extends AbstractVsuConfig {
    private static final String NAME = "autobackup";
    private static AutoBackupConfig INSTANCE;


    public static AutoBackupConfig getInstance() {
        if(INSTANCE == null) {
            INSTANCE = load(NAME, AutoBackupConfig.class, new AutoBackupConfig());
        }
        return INSTANCE;
    }
    private boolean autoBackupNotifyError = true;
    private boolean autoBackupNotifyInfo = false;
    private int autoBackupInterval = 60;
    private int autoBackupQueueCapacity = 5;
    private static final DateFormat BACKUP_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
    public AutoBackupConfig() {
        super(NAME);
    }

    /**
     * Return whenever the OPs should receive errors from auto-backup
     * @return whenever the OPs should receive errors from auto-backup
     */
    public boolean isAutoBackupNotifyError() {
        return autoBackupNotifyError;
    }

    public void setAutoBackupNotifyError(boolean autoBackupNotifyError) {
        this.autoBackupNotifyError = autoBackupNotifyError;
        save();
    }

    /**
     * Return whenever the OPs should receive infos from auto-backup
     * @return whenever the OPs should receive infos from auto-backup
     */
    public boolean isAutoBackupNotifyInfo() {
        return autoBackupNotifyInfo;
    }

    public void setAutoBackupNotifyInfo(boolean autoBackupNotifyInfo) {
        this.autoBackupNotifyInfo = autoBackupNotifyInfo;
        save();
    }

    /**
     * @return The interval of auto-backup, in second(s)
     */
    public int getAutoBackupInterval() {
        return autoBackupInterval;
    }

    /**
     * @param autoBackupInterval The interval of auto-backup, in second(s)
     */
    public void setAutoBackupInterval(int autoBackupInterval) {
        this.autoBackupInterval = autoBackupInterval;
        save();
    }

    /**
     * Return the length of the backup queue. <br>
     *
     * @return The length of backup queue
     * @see AutoBackupQueue
     */
    public int getAutoBackupQueueCapacity() {
        return autoBackupQueueCapacity;
    }

    public void setAutoBackupQueueCapacity(int autoBackupQueueCapacity) {
        this.autoBackupQueueCapacity = autoBackupQueueCapacity;
        save();
    }

    public String formatCurrentTime() {
        return BACKUP_DATE_FORMAT.format(new Date());
    }
}
