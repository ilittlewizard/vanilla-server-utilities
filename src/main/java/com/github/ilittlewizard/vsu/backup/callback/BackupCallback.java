package com.github.ilittlewizard.vsu.backup.callback;

public interface BackupCallback {
    void onError(String message);
    void onInfo(String message);
}

