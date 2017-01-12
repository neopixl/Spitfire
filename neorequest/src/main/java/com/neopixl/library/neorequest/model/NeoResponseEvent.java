package com.neopixl.library.neorequest.model;

import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class NeoResponseEvent<T> {
    private int statusCode;
    private VolleyError error;
    private T data;

    public NeoResponseEvent(T data, VolleyError error, int statusCode) {
        this.statusCode = statusCode;
        this.error = error;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public VolleyError getError() {
        return error;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return error == null;
    }
}
