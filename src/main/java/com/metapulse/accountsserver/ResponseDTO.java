package com.metapulse.accountsserver;

public class ResponseDTO {
    private boolean response;

    public ResponseDTO(boolean response) {
        this.response = response;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }
}