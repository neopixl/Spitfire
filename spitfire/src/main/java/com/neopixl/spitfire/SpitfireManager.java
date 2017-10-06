package com.neopixl.spitfire;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Main class used to store the object mapper <b>ObjectMapper</b> and the default retry policy <b>RetryPolicy</b> used by all requests. It acts as a singleton.
 * <p>Created by Florian ALONSO on 12/30/16.
 * For Neopixl</p>
 */

public final class SpitfireManager {

    @Nullable
    private static ObjectMapper objectMapper;

    @NonNull
    private static RetryPolicy defaultRetryPolicy = generateRetryPolicy();
    private static int requestTimeout = 30000;// 30 seconds

    /**
     * Sets the timeout
     * @param timeout default timeout for all requests
     */
    public static void setRequestTimeout(int timeout) {
        requestTimeout = timeout;
        defaultRetryPolicy = generateRetryPolicy();
    }

    /**
     * Store a retry policy
     * @param newRetryPolicy <b>RetryPolicy</b>, not null
     */
    public static void setDefaultRetryPolicy(@NonNull RetryPolicy newRetryPolicy) {
        defaultRetryPolicy = newRetryPolicy;
    }

    /**
     * Get the default retry policy
     * @return the default retry policy <b>RetryPolicy</b>, not null
     */
    @NonNull
    public static RetryPolicy getDefaultRetryPolicy() {
        return defaultRetryPolicy;
    }

    /**
     * Get the default object mapper (with SerializationFeature.INDENT_OUTPUT set to false and SerializationInclusion set to JsonInclude.Include.NON_NULL)
     * @return the current object mapper <b>ObjectMapper</b>, not null
     */
    @NonNull
    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return objectMapper;
    }

    /**
     * Generate a default retry policy (30 seconds for the timeout, 1 retry maximum, 1 backoff multiplier)
     * @return a default retry policy, not null
     */
    @NonNull
    private static RetryPolicy generateRetryPolicy() {
        return new DefaultRetryPolicy(requestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

}
