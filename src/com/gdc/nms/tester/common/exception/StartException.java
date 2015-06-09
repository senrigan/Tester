package com.gdc.nms.tester.common.exception;

public class StartException extends Exception {
    public StartException() {
        super();
    }

    public StartException(String message) {
        super(message);
    }

    public StartException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartException(Throwable cause) {
        super(cause);
    }
}
