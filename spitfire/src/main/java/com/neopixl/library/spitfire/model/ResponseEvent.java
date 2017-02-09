package com.neopixl.library.spitfire.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class ResponseEvent<T> {

    @NonNull
    private int statusCode;
    @Nullable
    private VolleyError error;
    @Nullable
    private T data;
    @Nullable
    private Object requestTag;

    /**
     * Constructor for a ResponseEvent (sent using EventBus)
     * @param data Response received for a request
     * @param error error received for a request
     * @param statusCode current http status code <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">status code</a>
     * @param requestTag current request tag attribute
     */
    public ResponseEvent(@Nullable T data, @Nullable VolleyError error, @NonNull int statusCode, @Nullable Object requestTag) {
        this.statusCode = statusCode;
        this.error = error;
        this.data = data;
        this.requestTag = requestTag;
    }

    /**
     * Get the status code
     * @return <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">status code</a>
     */
    @NonNull
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Return the volleyError
     * @return <b>VolleyError</b>
     */
    @Nullable
    public VolleyError getError() {
        return error;
    }

    /**
     * Returns the data
     * @return The response object
     */
    @Nullable
    public T getData() {
        return data;
    }

    /**
     * Returns the request tag which initialized this event
     * @return The request tag
     */
    @Nullable
    public Object getRequestTag() {
        return requestTag;
    }

    /**
     * Check if the request is successful (It means no error found for the current event).
     * @return boolean value
     */
    @NonNull
    public boolean isSuccess() {
        return error == null;
    }
}
