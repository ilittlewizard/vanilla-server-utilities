package com.github.ilittlewizard.vsu;

import com.github.ilittlewizard.vsu.backup.AutoBackupConfig;

public class VsuConfig extends AbstractVsuConfig{
    private static final String NAME = "vsu";
    private static final VsuConfig INSTANCE =
            load(NAME, VsuConfig.class, new VsuConfig());

    public static VsuConfig getInstance() {
        return INSTANCE;
    }

    public VsuConfig() {
        super("vsu");
    }

    private boolean doAutoBackup = false;

    /**
     * Return if auto-backup is enabled
     *
     * @return Is auto-backup is enabled
     */
    public boolean isDoAutoBackup() {
        return doAutoBackup;
    }

    public void setDoAutoBackup(boolean doAutoBackup) {
        this.doAutoBackup = doAutoBackup;
        save();
    }
}
