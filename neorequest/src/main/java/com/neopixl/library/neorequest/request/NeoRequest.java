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


    /**
     * Class Builder used to create a new request
     */
    public static class Builder<T> extends AbstractBuilder {
        private Map<String, String> parameters;
        private Object jsonObject;

        /**
         * Default
         *
         * @param method        used to send the request
         * @param url           given url to access the resource
         * @param classResponse class used to parse the response
         */
        public Builder(int method, String url, Class classResponse) {
            super(method, url, classResponse);
        }

        /**
         * Specifies the object
         * @param jsonObject The object to be embedded in the body
         * @return Builder {@link Builder}
         */
        public Builder object(Object jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        /**
         * Set the parameters for the request
         * @param parameters Map&lt;String, String&gt;
         * @return Builder {@link Builder}
         */
        public Builder parameters(Map<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        @Override
        public Builder listener(NeoRequestListener listener) {
            super.listener(listener);
            return this;
        }

        @Override
        public Builder headers(Map headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public Builder stickyEvent(boolean isStickyEvent) {
            super.stickyEvent(isStickyEvent);
            return this;
        }

        /**
         * Create a request based on the current request
         * @return The request
         */

        public NeoRequest build() {
            return new NeoRequest(this);
        }
    }

    /**
     * Constructor using the builder
     * @param builder {@link Builder}
     */
    protected NeoRequest(Builder builder) {
        super(builder);

        this.standardParams = builder.parameters;
        this.jsonObjectBody = builder.jsonObject;

        if (builder.method == Method.GET && jsonObjectBody != null) {
            throw new IllegalArgumentException("Cannot use json body request with GET");
        }
    }

    /**
     * Get the json content type  (default : "application/json; charset=UTF-8")
     * @return the current content type
     */
    protected String getJsonContentType() {
        return "application/json; charset=" + getParamsEncoding();
    }

    /**
     *
     * @return the current body content type
     */

    @Override
    public String getBodyContentType() {
        if ((getMethod() == Request.Method.POST || getMethod() == Request.Method.PUT) && getJsonObjectBody() != null) {
            return getJsonContentType();
        }
        return super.getBodyContentType();
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
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

    /**
     * Get the url for the request
     * @return the url used to access the ressource
     */
    @Override
    public String getUrl() {
        int method = getMethod();
        String topUrl = super.getUrl();
        if (method == Method.GET) {
            return NeoRequestManager.parseGetUrl(method, topUrl, getParams(), getParamsEncoding());
        }
        return topUrl;
    }

    /**
     * Get the json object body for the request
     * @return the json body
     */
    public Object getJsonObjectBody() {
        return jsonObjectBody;
    }

    /**
     * Get the parameters for the request
     * @return the current parameters associated to the request
     */
    @Override
    public Map<String, String> getParams() {
        return standardParams;
    }
}
