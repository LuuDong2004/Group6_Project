package group6.cinema_project.exception;

public class PastDateTime extends RuntimeException {
    private final String errorCode;
    private final Object[] args;

    public PastDateTime(String message) {
        super(message);
        this.errorCode = "PAST_DATETIME_ERROR";
        this.args = null;
    }

    public PastDateTime(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public PastDateTime(String message, String errorCode, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public PastDateTime(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PAST_DATETIME_ERROR";
        this.args = null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}