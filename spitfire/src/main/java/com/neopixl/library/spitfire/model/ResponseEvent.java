package com.neopixl.library.spitfire.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class ResponseEvent<T> {

    @NonNull
    private NetworkResponse response;
    @Nullable
    private VolleyError error;
    @Nullable
    private T data;
    @NonNull
    private Request request;

    /**
     * Constructor for a ResponseEvent (sent using EventBus)
     * @param data Response received for a request
     * @param error error received for a request
     * @param response current http request
     * @param request current request
     */
    public ResponseEvent(@Nullable T data, @Nullable VolleyError error, @NonNull NetworkResponse response, @NonNull Request request) {
        this.response = response;
        this.error = error;
        this.data = data;
        this.request = request;
    }

    /**
     * Get the status code
     * @return <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">status code</a>
     */
    @NonNull
    public NetworkResponse getResponse() {
        return response;
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
     * Returns the request which initialized this event
     * @return The request
     */
    @Nullable
    public Object getRequest() {
        return request;
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
