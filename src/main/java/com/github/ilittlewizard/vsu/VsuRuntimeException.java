package com.github.ilittlewizard.vsu;

public class VsuRuntimeException extends RuntimeException {
    public VsuRuntimeException() {
        super();
    }

    public VsuRuntimeException(String message) {
        super(message);
    }

    public VsuRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VsuRuntimeException(Throwable cause) {
        super(cause);
    }

    protected VsuRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
