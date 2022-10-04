package com.xqb.xqbutils.http;

public class HttpResult<T> {
    private boolean status;
    private String statusCode;
    private String message;
    private T data;

    public HttpResult() {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return this.data;
    }

    public void setResult(T data) {
        this.data = data;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return "0".equals(this.statusCode) && this.status;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "status=" + status +
                ", statusCode='" + statusCode + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
