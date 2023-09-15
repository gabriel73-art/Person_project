package com.bqpro.project.Response;

import java.util.List;

public class MessageResponse {
    private String message;
    private List<String> m;

    public MessageResponse(String message) {
        this.message = message;
    }
    public MessageResponse(List<String> m) {
        this.m = m;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getM() {
        return m;
    }

    public void setM(List<String> m) {
        this.m = m;
    }
}
