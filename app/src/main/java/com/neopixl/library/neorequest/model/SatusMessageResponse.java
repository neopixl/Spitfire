package com.neopixl.library.neorequest.model;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class SatusMessageResponse {

    private String message;
    private int status;

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
