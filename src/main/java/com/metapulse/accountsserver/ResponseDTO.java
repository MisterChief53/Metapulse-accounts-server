package com.metapulse.accountsserver;


/*An auxiliar class to give a json format to a given response*/
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