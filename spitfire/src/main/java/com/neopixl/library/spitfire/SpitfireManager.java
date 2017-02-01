package com.neopixl.library.spitfire;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import java.net.URLEncoder;
import java.util.Map;

/**
 * Main class used to store the object mapper <b>ObjectMapper</b> and the default retry policy <b>RetryPolicy</b> used by all requests. It acts as a singleton.
 * <p>Created by Florian ALONSO on 12/30/16.
 * For Neopixl</p>
 */

public final class SpitfireManager {

    private static ObjectMapper objectMapper;

    @NonNull
    private static RetryPolicy defaultRetryPolicy = generateRetryPolicy();
    private static int requestTimeout = 30000;// 30 seconds

    /**
     * Sets the timeout
     * @param timeout default timeout for all requests
     * @param regenerateRetryPolicy if set to true, it will generate a new retry policy (30 seconds for the timeout, 1 retry maximum, 1 backoff multiplier)
     */
    public static void setRequestTimeout(int timeout, boolean regenerateRetryPolicy) {
        requestTimeout = timeout;
        if (regenerateRetryPolicy) {
            defaultRetryPolicy = generateRetryPolicy();
        }
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
        return new DefaultRetryPolicy(requestTimeout, 1, 1);
    }

    /**
     * Converts a base URL, endpoint, and parameters into a full URL
     *
     * @param method The <b>com.android.volley.Request.Method</b> of the URL
     * @param url    The URL, not null
     * @param params The parameters to be appended to the URL if a GET method is used, can be null
     * @param encoding The encoding used to parse parameters set in the url (GET method), can be null
     * @return The full URL
     */
    public static String parseGetUrl(int method, @NonNull String url, @Nullable Map<String, String> params, @NonNull String encoding) {
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() == null || entry.getValue().equals("null")) {
                    entry.setValue("");
                }
            }
        }

        if (method == Request.Method.GET && params != null && !params.isEmpty()) {
            final StringBuilder result = new StringBuilder(url);
            final int startLength = result.length();
            for (String key : params.keySet()) {
                try {
                    final String encodedKey = URLEncoder.encode(key, encoding);
                    final String encodedValue = URLEncoder.encode(params.get(key), encoding);
                    if (result.length() > startLength) {
                        result.append("&");
                    } else {
                        result.append("?");
                    }
                    result.append(encodedKey);
                    result.append("=");
                    result.append(encodedValue);
                } catch (Exception e) {
                }
            }
            return result.toString();
        } else {
            return url;
        }
    }

}
