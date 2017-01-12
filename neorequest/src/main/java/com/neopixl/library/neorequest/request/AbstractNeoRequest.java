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
 */

public abstract class AbstractNeoRequest<T> extends Request<T> {

    private List<Integer> mAcceptedStatusCodes;
    private Class<T> classResponse;
    private final NeoRequestListener<T> mListener;
    private Map<String, String> headers;
    private boolean eventBusIsSticky;

    AbstractNeoRequest(int method, String url, Map<String, String> headers, NeoRequestListener<T> listener, Class<T> classResponse) {
        super(method, url, null);

        this.headers = headers;

        this.classResponse = classResponse;

        setShouldCache(method == Method.GET);

        mListener = listener;
        eventBusIsSticky = false;

        mAcceptedStatusCodes = new ArrayList<>();
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_OK);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_NO_CONTENT);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_ACCEPTED);
        mAcceptedStatusCodes.add(HttpURLConnection.HTTP_CREATED);

        setRetryPolicy(NeoRequestManager.getDefaultRetryPolicy());

    }

    public void addAcceptedStatusCodes(int[] statusCodes) {
        for (int statusCode : statusCodes) {
            mAcceptedStatusCodes.add(statusCode);
        }
    }

    public List<Integer> getAcceptedStatusCodes() {
        return mAcceptedStatusCodes;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onSuccess(response);
        } else {
            NeoResponseEvent<T> event = new NeoResponseEvent<>(response, null, -1);
            EventBus eventBus = EventBus.getDefault();
            if (isEventBusIsSticky()) {
                eventBus.postSticky(event);
            } else {
                eventBus.post(event);
            }
        }
    }

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
            if (isEventBusIsSticky()) {
                eventBus.postSticky(event);
            } else {
                eventBus.post(event);
            }
        }
    }

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

    public byte[] getBody() throws AuthFailureError {
        if (getMethod() == Method.GET) {
            return null;
        }
        return super.getBody();
    }

    public boolean isEventBusIsSticky() {
        return eventBusIsSticky;
    }

    public void setEventBusIsSticky(boolean eventBusIsSticky) {
        this.eventBusIsSticky = eventBusIsSticky;
    }
}