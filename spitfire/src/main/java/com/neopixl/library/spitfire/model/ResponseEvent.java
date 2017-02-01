package com.neopixl.library.spitfire.model;

import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class ResponseEvent<T> {

    private int statusCode;
    private VolleyError error;
    private T data;

    /**
     * Constructor for a NeoResponseEvent (sent using EventBus)
     * @param data Response received for a request
     * @param error error received for a request
     * @param statusCode current http status code <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">status code</a>
     */
    public ResponseEvent(T data, VolleyError error, int statusCode) {
        this.statusCode = statusCode;
        this.error = error;
        this.data = data;
    }

    /**
     * Get the status code
     * @return <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">status code</a>
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Return the volleyError
     * @return <b>VolleyError</b>
     */
    public VolleyError getError() {
        return error;
    }

    /**
     * Returns the data
     * @return The response object
     */
    public T getData() {
        return data;
    }

    /**
     * Check if the request is successful (It means no error found for the current event).
     * @return boolean value
     */
    public boolean isSuccess() {
        return error == null;
    }
}
