package com.neopixl.library.neorequest;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.neopixl.library.neorequest.model.NeoRequestData;
import com.neopixl.library.neorequest.listener.NeoRequestListener;
import com.neopixl.library.neorequest.model.NeoResponseEvent;
import com.neopixl.library.neorequest.model.PostJsonRequest;
import com.neopixl.library.neorequest.model.SatusMessageResponse;
import com.neopixl.library.neorequest.request.AbstractNeoRequest;
import com.neopixl.library.neorequest.request.FileStreamNeoRequest;
import com.neopixl.library.neorequest.request.MultipartNeoRequest;
import com.neopixl.library.neorequest.request.NeoRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private RequestQueue requestQueue;

    private TextView getWithoutParamsTextViewEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        linearLayout.removeAllViews();
        requestQueue = Volley.newRequestQueue(this);
        EventBus.getDefault().register(this);

        loadGetRequest();
        loadPostRequest();
        loadFileStreamRequest();
        loadMultipartRequest();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void didReceiveResponseForGetWithoutParams(NeoResponseEvent<SatusMessageResponse> event) {
        setSuccessForTextView(getWithoutParamsTextViewEventBus, event.isSuccess());
    }

    private void loadGetRequest() {
        Map<String, String> emtpyHeaders = new HashMap<>();

        getWithoutParamsTextViewEventBus = getTextViewForRequest("getWithoutParams");
        NeoRequest<SatusMessageResponse> getWithoutParamsRequest = new NeoRequest<>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/data", emtpyHeaders, SatusMessageResponse.class);
        addRequestWithRandom(getWithoutParamsRequest);


        final TextView getWithoutParamsAndEmptyReturnTextView = getTextViewForRequest("getWithoutParamsAndEmptyReturn");
        NeoRequest<Void> getWithoutParamsAndEmptyReturnRequest = new NeoRequest<>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/nodata", emtpyHeaders, new NeoRequestListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                setSuccessForTextView(getWithoutParamsAndEmptyReturnTextView, true);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(getWithoutParamsAndEmptyReturnTextView, false);

            }
        }, Void.class);
        addRequestWithRandom(getWithoutParamsAndEmptyReturnRequest);


        Map<String, String> params = new HashMap<>();
        params.put("query1", "test1");
        params.put("query2", "test2");
        final TextView getWithParamsTextView = getTextViewForRequest("getWithParams");
        NeoRequest<Void> getWithParamsRequest = new NeoRequest<>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/data/3", emtpyHeaders, params, new NeoRequestListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                setSuccessForTextView(getWithParamsTextView, true);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(getWithParamsTextView, false);

            }
        }, Void.class);
        addRequestWithRandom(getWithParamsRequest);


        final TextView getWithParamsErrorTextView = getTextViewForRequest("getWithParamsError");
        NeoRequest<Void> getWithParamsErrorRequest = new NeoRequest<>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/errordata/3", emtpyHeaders, new NeoRequestListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                setSuccessForTextView(getWithParamsErrorTextView, false);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(getWithParamsErrorTextView, true);

            }
        }, Void.class);
        addRequestWithRandom(getWithParamsErrorRequest);
    }

    private void loadPostRequest() {
        Map<String, String> emtpyHeaders = new HashMap<>();

        Map<String, String> params = new HashMap<>();
        params.put("query1", "test1");
        params.put("query2", "test2");
        final TextView postWithParamsTextView = getTextViewForRequest("postWithParams");
        NeoRequest<SatusMessageResponse> postWithParamsRequest = new NeoRequest<>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/data/3", emtpyHeaders, params, new NeoRequestListener<SatusMessageResponse>() {
            @Override
            public void onSuccess(SatusMessageResponse v) {
                setSuccessForTextView(postWithParamsTextView, true);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(postWithParamsTextView, false);

            }
        }, SatusMessageResponse.class);
        addRequestWithRandom(postWithParamsRequest);


        Map<String, String> fullHeaders = new HashMap<>();
        fullHeaders.put("Authorization", "Basic abcdefghijklmnopqrstuvwxyz");
        fullHeaders.put("Token", "abcdefghijkl");

        PostJsonRequest postJsonRequest = new PostJsonRequest();
        SatusMessageResponse satusMessageRequest = new SatusMessageResponse();
        satusMessageRequest.setStatus(200);
        satusMessageRequest.setMessage("abcdefghijkl");
        postJsonRequest.setTest1(1);
        postJsonRequest.setTest2("salut");
        postJsonRequest.setTestObject(satusMessageRequest);

        final TextView postWithJsonAndHeaderTextView = getTextViewForRequest("postWithJsonAndHeader");
        NeoRequest<SatusMessageResponse> postWithJsonAndHeaderRequest = new NeoRequest<>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/json", fullHeaders, postJsonRequest, new NeoRequestListener<SatusMessageResponse>() {
            @Override
            public void onSuccess(SatusMessageResponse v) {
                setSuccessForTextView(postWithJsonAndHeaderTextView, true);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(postWithJsonAndHeaderTextView, false);

            }
        }, SatusMessageResponse.class);
        addRequestWithRandom(postWithJsonAndHeaderRequest);
    }

    private void loadFileStreamRequest() {
        Map<String, String> emtpyHeaders = new HashMap<>();

        NeoRequestData data = new NeoRequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");

        final TextView putImageStreamTextView = getTextViewForRequest("putImageStream");
        FileStreamNeoRequest<Void> putImageStreamRequest = new FileStreamNeoRequest<>(Request.Method.PUT,
                "https://private-4b982e-neorequest.apiary-mock.com/put/image", emtpyHeaders, data, new NeoRequestListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                setSuccessForTextView(putImageStreamTextView, true);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(putImageStreamTextView, false);

            }
        }, Void.class);
        addRequestWithRandom(putImageStreamRequest);
    }

    private void loadMultipartRequest() {
        Map<String, String> emtpyHeaders = new HashMap<>();

        HashMap<String, NeoRequestData> multipardData = new HashMap<>();
        NeoRequestData data = new NeoRequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
        multipardData.put("image1", data);
        data = new NeoRequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
        multipardData.put("image1", data);

        final TextView putImageMultipartTextView = getTextViewForRequest("putImageMultipart");
        MultipartNeoRequest<Void> putImageMultipartRequest = new MultipartNeoRequest<>(Request.Method.PUT,
                "https://private-4b982e-neorequest.apiary-mock.com/put/images/3", emtpyHeaders, multipardData, new NeoRequestListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                setSuccessForTextView(putImageMultipartTextView, true);
            }

            @Override
            public void onFailure(VolleyError volleyError, int i) {
                setSuccessForTextView(putImageMultipartTextView, false);

            }
        }, Void.class);
        addRequestWithRandom(putImageMultipartRequest);
    }

    private TextView getTextViewForRequest(String requestName) {
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setGravity(Gravity.CENTER);

        TextView textViewRequest = new TextView(this);
        textViewRequest.setText(requestName +" :  ");

        TextView resultTextView = new TextView(this);
        resultTextView.setText("PENDING");
        resultTextView.setTextColor(Color.BLUE);

        horizontalLayout.addView(textViewRequest, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontalLayout.addView(resultTextView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(horizontalLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        return resultTextView;
    }

    private void setSuccessForTextView(TextView textView, boolean success) {
        String newText = "ERROR";
        int color = Color.RED;
        if (success) {
            newText = "OK";
            color = Color.GREEN;
        }
        textView.setText(newText);
        textView.setTextColor(color);
    }

    public void addRequestWithRandom(final AbstractNeoRequest request) {
        Random rand = new Random();
        int randomWait = rand.nextInt(3000) + 1;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestQueue.add(request);
            }
        }, randomWait);
    }
}
