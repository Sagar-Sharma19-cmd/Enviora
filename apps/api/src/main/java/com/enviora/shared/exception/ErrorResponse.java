package com.enviora.shared.exception;

import java.time.Instant;

public class ErrorResponse {
    private boolean success;
    private String message;
    private String error;
    private int status;
    private Instant timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(boolean success, String message, String error, int status, Instant timestamp) {
        this.success = success;
        this.message = message;
        this.error = error;
        this.status = status;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private boolean success;
        private String message;
        private String error;
        private int status;
        private Instant timestamp;

        public ErrorResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(success, message, error, status, timestamp);
        }
    }
}
