package com.neopixl.spitfire.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public interface RequestListener<T> {

    /**
     * Called when the request has succeeded
     * @param request the current request
     * @param response the network response
     * @param result the parsed response
     */
    void onSuccess(@NonNull Request<T> request, @NonNull NetworkResponse response, @Nullable T result);

    /**
     * Called when the request has failed
     * @param request the current request
     * @param response the network response
     * @param error <b>VolleyError</b> error for the request
     */
    void onFailure(@NonNull Request<T> request, @Nullable NetworkResponse response, @Nullable VolleyError error);
}