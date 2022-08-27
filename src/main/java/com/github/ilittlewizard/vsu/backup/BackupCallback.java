package com.github.ilittlewizard.vsu.backup;

public interface BackupCallback {
    void onError(String message);
    void onInfo(String message);
}

