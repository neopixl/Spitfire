package com.neopixl.library.neorequest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Main class used to store the object mapper {@link ObjectMapper} and the default retry policy {@link RetryPolicy} used by all requests. It acts as a singleton.
 * <p>Created by Florian ALONSO on 12/30/16.
 * For Neopixl</p>
 */

public final class NeoRequestManager {

    private static ObjectMapper objectMapper;
    private static RetryPolicy defaultRetryPolicy = generateRetryPolicy();
    private static int requestTimeout = 30000;// 30 seconds

    /**
     * Sets the timeout
     * @param timeout
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
     * @param newRetryPolicy {@link RetryPolicy}
     */
    public static void setDefaultRetryPolicy(RetryPolicy newRetryPolicy) {
        defaultRetryPolicy = newRetryPolicy;
    }

    /**
     * Get the default retry policy
     * @return the default retry policy {@link RetryPolicy}
     */
    public static RetryPolicy getDefaultRetryPolicy() {
        return defaultRetryPolicy;
    }

    /**
     * Get the default object mapper (with SerializationFeature.INDENT_OUTPUT set to false and SerializationInclusion set to JsonInclude.Include.NON_NULL)
     * @return the current object mapper {@link ObjectMapper}
     */
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
     * @return a default retry policy
     */
    private static RetryPolicy generateRetryPolicy() {
        return new DefaultRetryPolicy(requestTimeout, 1, 1);
    }

    /**
     * Converts a base URL, endpoint, and parameters into a full URL
     *
     * @param method The {@link com.android.volley.Request.Method} of the URL
     * @param url    The URL
     * @param params The parameters to be appended to the URL if a GET method is used
     * @return The full URL
     */
    public static String parseGetUrl(int method, String url, Map<String, String> params, String encoding) {
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
