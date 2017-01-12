package com.neopixl.library.neorequest.listener;

import com.android.volley.VolleyError;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public interface NeoRequestListener<T> {
    void onSuccess(T result);

    void onFailure(VolleyError error, int statusCode);
}