package com.neopixl.library.neorequest.listener;

import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public interface NeoRequestListener<T> {

    /**
     * Called when the request has succeeded
     * @param result the parsed response
     */
    void onSuccess(T result);

    /**
     * Called when the request has failed
     * @param error <b>VolleyError</b> error for the request
     * @param statusCode <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">status code</a>
     */
    void onFailure(VolleyError error, int statusCode);
}