package com.neopixl.library.neorequest.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.neopixl.library.neorequest.NeoRequestManager;
import com.neopixl.library.neorequest.listener.NeoRequestListener;
import com.neopixl.library.neorequest.model.NeoResponseEvent;

import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 * @param <T> The type used as the response for the request
 */

public abstract class AbstractNeoRequest<T> extends Request<T> {

    private List<Integer> mAcceptedStatusCodes;
    private Class<T> classResponse;
    private final NeoRequestListener<T> mListener;
    private Map<String, String> headers;
    private boolean isStickyEvent;

    /**
     * Abstract class Builder used to create a new request
     * @param <T> Type used for the response of the request.
     */
    public static abstract class AbstractBuilder<T, RequestType extends AbstractNeoRequest<T>> {

        protected int method = -10;
        private String url;
        private Class<T> classResponse;

        private NeoRequestListener<T> mListener;
        private Map<String, String> headers;
        private boolean eventBusIsSticky;

        /**
         * Default
         * @param method used to send the request
         * @param url given url to access the resource
         * @param classResponse class used to parse the response
         */
        public AbstractBuilder(int method, String url, Class classResponse) {

            this.method = method;
            this.url = url;
            this.classResponse = classResponse;
        }

        /**
         * Sets the listener for the request
         * @param listener {@link NeoRequestListener}
         * @return the builder
         */

        public AbstractBuilder listener(NeoRequestListener<T> listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * Sets the headers for the request
         * @param headers used to send the request
         * @return Map&lt;String, String&gt;
         */
        public AbstractBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Specifies if the request will post <a href="http://greenrobot.org/eventbus/documentation/configuration/sticky-events/" target="_blank">sticky events</a>
         * @param isStickyEvent flag used to specify if all events will be sent as sticky events
         * @return boolean
         */

        public AbstractBuilder stickyEvent(boolean isStickyEvent) {
            this.eventBusIsSticky = isStickyEvent;
            return this;
        }

        /**
         * You must implement this method in your subclass.
         * @return AbstractNeoRequest
         */
        abstract public RequestType build() ;
    }


    AbstractNeoRequest(AbstractBuilder builder) {
        super(builder.method, builder.url, null);

        Map<String, String> builderHeaders = builder.headers;

        this.headers = builderHeaders!=null ? builderHeaders : new HashMap<String, String>();
        this.classResponse = builder.classResponse;

        setShouldCache(builder.method == Method.GET);

        this.mListener = builder.mListener;
        this.isStickyEvent = builder.eventBusIsSticky;

        mAcceptedStatusCodes = new ArrayList<>();
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_OK);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_NO_CONTENT);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_ACCEPTED);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_CREATED);

        setRetryPolicy(NeoRequestManager.getDefaultRetryPolicy());
    }


    /**
     * Constructor to create the request
     * @param method http method (GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, PATCH) {@link com.android.volley.Request.Method}
     * @param url url for the request
     * @param headers headers for the request (can be null)
     * @param listener request listener (can be null, in this case the response will be sent using events with EventBus)
     * @param classResponse the class used to parse the response associated to the request.
     */
    AbstractNeoRequest(int method, String url, Map<String, String> headers, NeoRequestListener<T> listener, Class<T> classResponse, boolean isStickyEvent) {
        super(method, url, null);

        this.headers = headers!=null ? headers : new HashMap<String, String>();
        this.classResponse = classResponse;

        setShouldCache(method == Method.GET);

        this.mListener = listener;
        this.isStickyEvent = isStickyEvent;

        mAcceptedStatusCodes = new ArrayList<>();
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_OK);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_NO_CONTENT);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_ACCEPTED);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_CREATED);

        setRetryPolicy(NeoRequestManager.getDefaultRetryPolicy());
    }

    /**
     * Add accepted status codes (<a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">Http status codes</a>)
     * @param statusCodes int[]
     */

    public void addAcceptedStatusCodes(int[] statusCodes) {
        for (int statusCode : statusCodes) {
            mAcceptedStatusCodes.add(statusCode);
        }
    }

    /**
     * Get the list of all accepted status codes
     * @return list of accepted status codes
     */
    public List<Integer> getAcceptedStatusCodes() {
        return mAcceptedStatusCodes;
    }

    /**
     * Delivers the response using the listener (if one is set) or post an event using EventBus.
     * Note: This method is called internally, you should never call it directly. But you override it.
     * @param response The response
     */
    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onSuccess(response);
        } else {
            NeoResponseEvent<T> event = new NeoResponseEvent<>(response, null, -1);
            EventBus eventBus = EventBus.getDefault();
            if (isStickyEvent) {
                eventBus.postSticky(event);
            } else {
                eventBus.post(event);
            }
        }
    }

    /**
     * Delivers the error using the listener (if one is set) or post an event using EventBus.
     * Note: This method is called internally, you should never call it directly. But you override it.
     * @param error {@link VolleyError}
     */
    @Override
    public void deliverError(VolleyError error) {
        int statusCode = -1;
        if (error != null && error.networkResponse != null) {
            statusCode = error.networkResponse.statusCode;
        }


        if (mListener != null) {
            mListener.onFailure(error, statusCode);
        } else {
            NeoResponseEvent<T> event = new NeoResponseEvent<>(null, error, statusCode);
            EventBus eventBus = EventBus.getDefault();
            if (isStickyEvent) {
                eventBus.postSticky(event);
            } else {
                eventBus.post(event);
            }
        }
    }

    /**
     * Parses the network response {@link NetworkResponse} and returns the expected Type for the request.
     * @param response {@link NetworkResponse} The response for the request (Success or error).
     * @return Response object linked to a specific type
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        JavaType returnType = getReturnType();
        T returnData = null;
        if (returnType != null) {
            try {
                if (response.data != null) {
                    returnData = NeoRequestManager.getObjectMapper().readValue(response.data, returnType);
                }
            } catch (Exception e) {
                VolleyLog.e(e, "An error occurred while parsing network response:");
                returnData = null;
            }
        }


        if (returnData == null && classResponse != Void.class) {
            ParseError parseError = new ParseError(response);
            String content = "";
            if (response.data != null) {
                content = new String(response.data);
            }
            VolleyLog.e(parseError, "Return data is null. API returned : "+ content);
            return Response.error(parseError);
        }

        return Response.success(returnData, HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * Returns the type for the response.
     * @return null, Array, List, Map or Object.
     */
    private JavaType getReturnType() {
        if (classResponse == Void.class) {
            return null;
        } else if (classResponse.isArray()) {
            return TypeFactory.defaultInstance().constructArrayType(classResponse.getComponentType());
        } else if (classResponse == List.class) {
            return TypeFactory.defaultInstance().constructCollectionType(List.class, classResponse.getComponentType());
        } else if (classResponse == Map.class) {
            return TypeFactory.defaultInstance().constructMapType(Map.class, String.class, classResponse.getComponentType());
        }
        return TypeFactory.defaultInstance().constructType(classResponse);
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link AuthFailureError} as authentication may be required to
     * provide these values.
     * @throws AuthFailureError In the event of auth failure
     * @return Map&lt;String, String&gt;
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> currentHeader = new HashMap<>(super.getHeaders());
        currentHeader.putAll(this.headers);

        String bodyContentType = getBodyContentType();
        if (bodyContentType != null) {

            currentHeader.put("Content-Type", bodyContentType);
        } else if (currentHeader.containsKey("Content-Type")) {
            currentHeader.remove("Content-Type");
        }
        return currentHeader;
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     * @return byte[] or null if the
     */
    public byte[] getBody() throws AuthFailureError {
        if (getMethod() == Method.GET) {
            return null;
        }
        return super.getBody();
    }

    /**
     * Returns the sticky's state for the event signal sent using EventBus.
     * see <a href="http://greenrobot.org/eventbus/documentation/configuration/sticky-events/" target="_blank">Sticky Events</a>
     * @return boolean
     */
    public boolean isStickyEvent() {
        return isStickyEvent;
    }
}