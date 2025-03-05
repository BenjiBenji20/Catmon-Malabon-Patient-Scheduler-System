package com.azathoth.CatmonMalabonHealthCenterSystem.exception;

import java.time.LocalDate;

public class ErrorResponse {
    private LocalDate date;
    private String error;
    private String message;
    private int status;

    public ErrorResponse(LocalDate date, String error, String message, int status) {
        this.date = date;
        this.error = error;
        this.message = message;
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
