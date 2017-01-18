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
     * @param <T> Type used for the request
     */
    public static class Builder<T> {

        protected int method = -10;
        private String url;

        private Class<T> classResponse;
        private NeoRequestListener<T> mListener;
        private Map<String, String> headers;
        private boolean eventBusIsSticky;
        private Map<String, String> parameters;
        private Object jsonObject;


        /**
         * Default
         * @param method used to send the request
         * @param url given url to access the resource
         * @param classResponse class used to parse the response
         */
        public Builder(int method, String url, Class classResponse) {
            this.method = method;
            this.url = url;
            this.classResponse = classResponse;
        }

        /**
         * Sets the listener for the request
         * @param listener {@link NeoRequestListener}
         * @return the builder
         */
        public Builder listener(NeoRequestListener<T> listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * Sets the headers for the request
         * @param headers used to send the request
         * @return
         */
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Specifies if the request will post <a href="http://greenrobot.org/eventbus/documentation/configuration/sticky-events/" target="_blank">sticky events</a>
         * @param isStickyEvent flag used to specify if all events will be sent as sticky events
         * @return boolean
         */
        public Builder stickyEvent(boolean isStickyEvent) {
            this.eventBusIsSticky = isStickyEvent;
            return this;
        }

        /**
         * Specifies the object
         * @param jsonObject
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

        /**
         * Create a request based on the current request
         * @return The request
         */
        public NeoRequest<T> build() {
            return new NeoRequest<T>(this);
        }
    }

    /**
     * Constructor using the builder
     * @param builder {@link Builder}
     */
    protected NeoRequest(Builder builder) {
        this(builder.method, builder.url, builder.headers, builder.parameters, builder.jsonObject, builder.mListener, builder.classResponse, builder.eventBusIsSticky);
    }

    private NeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Object jsonObject, NeoRequestListener<T> listener, Class<T> classResponse, boolean isStickyEvent) {
        super(method, url, headers, listener, classResponse , isStickyEvent);
        standardParams = params;
        jsonObjectBody = jsonObject;

        if (method == Method.GET && jsonObjectBody != null) {
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
