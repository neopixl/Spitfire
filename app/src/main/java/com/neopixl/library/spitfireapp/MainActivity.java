package com.neopixl.library.spitfireapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.neopixl.library.spitfire.listener.RequestListener;
import com.neopixl.library.spitfire.model.RequestData;
import com.neopixl.library.spitfire.model.ResponseEvent;
import com.neopixl.library.spitfire.request.BaseRequest;
import com.neopixl.library.spitfire.request.MultipartRequest;
import com.neopixl.library.spitfire.request.UploadFileRequest;
import com.neopixl.library.spitfireapp.model.PostJsonRequest;
import com.neopixl.library.spitfireapp.model.StatusMessageResponse;

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
    public void didReceiveResponseForGetWithoutParams(ResponseEvent<StatusMessageResponse> event) {
        setSuccessForTextView(getWithoutParamsTextViewEventBus, event.isSuccess());
    }

    private void loadGetRequest() {
        getWithoutParamsTextViewEventBus = getTextViewForRequest("getWithoutParams");
        BaseRequest<StatusMessageResponse> getWithoutParamsRequest = new BaseRequest.Builder<StatusMessageResponse>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/data", StatusMessageResponse.class)
                .build();

        addRequestWithRandom(getWithoutParamsRequest);

        final TextView getWithoutParamsAndEmptyReturnTextView = getTextViewForRequest("getWithoutParamsAndEmptyReturn");

        BaseRequest<Void> getWithoutParamsAndEmptyReturnRequest = new BaseRequest.Builder<Void>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/nodata", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        setSuccessForTextView(getWithoutParamsAndEmptyReturnTextView, true);
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(getWithoutParamsAndEmptyReturnTextView, false);

                    }
                })
                .build();

        addRequestWithRandom(getWithoutParamsAndEmptyReturnRequest);

        Map<String, String> params = new HashMap<>();
        params.put("query1", "test1");
        params.put("query2", "test2");
        final TextView getWithParamsTextView = getTextViewForRequest("getWithParams");
        BaseRequest<Void> getWithParamsRequest = new BaseRequest.Builder<Void>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/data/3", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        setSuccessForTextView(getWithParamsTextView, true);
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(getWithParamsTextView, false);

                    }
                }).build();

        addRequestWithRandom(getWithParamsRequest);

        final TextView getWithParamsErrorTextView = getTextViewForRequest("getWithParamsError");
        BaseRequest<Void> getWithParamsErrorRequest = new BaseRequest.Builder<Void>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/errordata/3", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        setSuccessForTextView(getWithParamsErrorTextView, false);
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(getWithParamsErrorTextView, true);

                    }
                }).build();

        addRequestWithRandom(getWithParamsErrorRequest);
    }

    private void loadPostRequest() {

        Map<String, String> params = new HashMap<>();
        params.put("query1", "test1");
        params.put("query2", "test2");
        final TextView postWithParamsTextView = getTextViewForRequest("postWithParams");
        BaseRequest<StatusMessageResponse> postWithParamsRequest = new BaseRequest.Builder<StatusMessageResponse>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/data/3", StatusMessageResponse.class)
                .listener(new RequestListener<StatusMessageResponse>() {
            @Override
            public void onSuccess(Request request, NetworkResponse response, StatusMessageResponse v) {
                setSuccessForTextView(postWithParamsTextView, true);
            }

            @Override
            public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                setSuccessForTextView(postWithParamsTextView, false);

            }
        }).parameters(params).build();
        addRequestWithRandom(postWithParamsRequest);

        Map<String, String> fullHeaders = new HashMap<>();
        fullHeaders.put("Authorization", "Basic abcdefghijklmnopqrstuvwxyz");
        fullHeaders.put("Token", "abcdefghijkl");

        PostJsonRequest postJsonRequest = new PostJsonRequest();
        StatusMessageResponse satusMessageRequest = new StatusMessageResponse();
        satusMessageRequest.setStatus(200);
        satusMessageRequest.setMessage("abcdefghijkl");
        postJsonRequest.setTest1(1);
        postJsonRequest.setTest2("salut");
        postJsonRequest.setTestObject(satusMessageRequest);

        final TextView postWithJsonAndHeaderTextView = getTextViewForRequest("postWithJsonAndHeader");
        BaseRequest<StatusMessageResponse> postWithJsonAndHeaderRequest = new BaseRequest.Builder<StatusMessageResponse>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/json", StatusMessageResponse.class).listener(new RequestListener<StatusMessageResponse>() {
            @Override
            public void onSuccess(Request request, NetworkResponse response, StatusMessageResponse v) {
                setSuccessForTextView(postWithJsonAndHeaderTextView, true);
            }

            @Override
            public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                setSuccessForTextView(postWithJsonAndHeaderTextView, false);

            }
        })
                .headers(fullHeaders)
                .object(postJsonRequest)
                .build();

        addRequestWithRandom(postWithJsonAndHeaderRequest);



        final TextView postWithStatusErrorTextView = getTextViewForRequest("postWithStatusError");
        BaseRequest<Void> postWithStatusErrorTextViewRequest = new BaseRequest.Builder<Void>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/errordata-json", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        setSuccessForTextView(postWithStatusErrorTextView, false);
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(postWithStatusErrorTextView, (response != null && response.statusCode == 400));

                    }
                }).build();

        addRequestWithRandom(postWithStatusErrorTextViewRequest);
    }

    private void loadFileStreamRequest() {
        RequestData data = new RequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");

        final TextView putImageStreamTextView = getTextViewForRequest("putImageStream");

        Map<String, String> fullHeaders = new HashMap<>();
        fullHeaders.put("Authorization", "Basic abcdefghijklmnopqrstuvwxyz");
        fullHeaders.put("Token", "abcdefghijkl");

        UploadFileRequest<Void> putImageStreamRequest = new UploadFileRequest.Builder<Void>(Request.Method.PUT, "https://private-4b982e-neorequest.apiary-mock.com/put/image", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        setSuccessForTextView(putImageStreamTextView, true);
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(putImageStreamTextView, false);

                    }
                })
                .headers(fullHeaders)
                .partData(data)
                .build();

        addRequestWithRandom(putImageStreamRequest);
    }

    private void loadMultipartRequest() {
        HashMap<String, RequestData> multiPartData = new HashMap<>();
        RequestData data = new RequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
        multiPartData.put("image1", data);
        data = new RequestData("image2", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
        multiPartData.put("image2", data);

        final TextView putImageMultipartTextView = getTextViewForRequest("putImageMultipart");


        Map<String, String> fullHeaders = new HashMap<>();
        fullHeaders.put("Authorization", "Basic abcdefghijklmnopqrstuvwxyz");
        fullHeaders.put("Token", "abcdefghijkl");

        MultipartRequest<Void> putImageMultipartRequest = new MultipartRequest.Builder<Void>(Request.Method.PUT,
                "https://private-4b982e-neorequest.apiary-mock.com/put/images/3", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        setSuccessForTextView(putImageMultipartTextView, true);
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(putImageMultipartTextView, false);

                    }
                })
                .headers(fullHeaders)
                .multiPartData(multiPartData)
                .build();
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
        String newText = success ? "OK" : "ERROR";
        int color = success ? Color.GREEN : Color.RED;

        textView.setText(newText);
        textView.setTextColor(color);
    }

    public void addRequestWithRandom(final Request request) {
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
