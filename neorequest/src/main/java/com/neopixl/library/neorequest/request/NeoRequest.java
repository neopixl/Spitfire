package com.neopixl.library.neorequest.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neopixl.library.neorequest.NeoRequestManager;
import com.neopixl.library.neorequest.listener.NeoRequestListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class NeoRequest<T> extends AbstractNeoRequest<T> {

    private Object jsonObjectBody;
    private Map<String, String> standardParams;

    public NeoRequest(int method, String url, Map<String, String> headers, NeoRequestListener<T> listener, Class<T> classResponse) {
        this(method, url, headers, null, null, listener, classResponse);
    }

    public NeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, NeoRequestListener<T> listener, Class<T> classResponse) {
        this(method, url, headers, params, null, listener, classResponse);
    }

    public NeoRequest(int method, String url, Map<String, String> headers, Object jsonObject, NeoRequestListener<T> listener, Class<T> classResponse) {
        this(method, url, headers, null, jsonObject, listener, classResponse);
    }

    public NeoRequest(int method, String url, Map<String, String> headers, Class<T> classResponse) {
        this(method, url, headers, null, null, null, classResponse);
    }

    public NeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Class<T> classResponse) {
        this(method, url, headers, params, null, null, classResponse);
    }

    public NeoRequest(int method, String url, Map<String, String> headers, Object jsonObject, Class<T> classResponse) {
        this(method, url, headers, null, jsonObject, null, classResponse);
    }

    protected NeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Object jsonObject, NeoRequestListener<T> listener, Class<T> classResponse) {
        super(method, url, headers, listener, classResponse);
        standardParams = params;
        jsonObjectBody = jsonObject;

        if (method == Method.GET && jsonObjectBody != null) {
            throw new IllegalArgumentException("Cannot use json body request with GET");
        }
    }

    protected String getJsonContentType() {
        return "application/json; charset=" + getParamsEncoding();
    }

    @Override
    public String getBodyContentType() {
        if ((getMethod() == Request.Method.POST || getMethod() == Request.Method.PUT) && getJsonObjectBody() != null) {
            return getJsonContentType();
        }
        return super.getBodyContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        String bodyContentType = getBodyContentType();
        int method = getMethod();
        if (method != Method.GET && bodyContentType != null && bodyContentType.equals(getJsonContentType())) {
            byte ptext[];
            try {
                ptext = NeoRequestManager.getObjectMapper().writeValueAsBytes(getJsonObjectBody());
                VolleyLog.d("Sending JSON BODY : " + new String(ptext, getParamsEncoding()));
            } catch (JsonProcessingException e) {
                JSONObject object = new JSONObject(getParams());
                VolleyLog.d("Sending JSON FROM PARAMS : " + object.toString());
                ptext = object.toString().getBytes();
            } catch (UnsupportedEncodingException e) {
                ptext = null;
                e.printStackTrace();
            }
            return ptext;
        }
        return super.getBody();
    }

    @Override
    public String getUrl() {
        int method = getMethod();
        String topUrl = super.getUrl();
        if (method == Method.GET) {
            return NeoRequestManager.parseGetUrl(method, topUrl, getParams(), getParamsEncoding());
        }
        return topUrl;
    }

    public Object getJsonObjectBody() {
        return jsonObjectBody;
    }

    @Override
    public Map<String, String> getParams() {
        return standardParams;
    }
}
