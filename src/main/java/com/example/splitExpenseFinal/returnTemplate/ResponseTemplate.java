package com.example.splitExpenseFinal.returnTemplate;

public class ResponseTemplate<T> {
    private String status;
    private int code;
    private T value;

    public ResponseTemplate(String status, int code, T value) {
        this.status = status;
        this.code = code;
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}


