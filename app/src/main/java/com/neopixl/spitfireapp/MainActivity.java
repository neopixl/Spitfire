package com.neopixl.spitfireapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.neopixl.spitfire.listener.RequestListener;
import com.neopixl.spitfire.model.RequestData;
import com.neopixl.spitfire.request.BaseRequest;
import com.neopixl.spitfire.request.MultipartRequest;
import com.neopixl.spitfire.request.UploadFileRequest;
import com.neopixl.spitfireapp.model.PostJsonRequest;
import com.neopixl.spitfireapp.model.StatusMessageResponse;
import com.neopixl.spitfireapp.model.WrapperStatusMessageResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private RequestQueue requestQueue;

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

        loadGetRequest();
        loadPostRequest();
        loadPatchRequest();
        loadFileStreamRequest();
        loadMultipartRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadGetRequest() {
        final TextView getWithoutParamsAndNoContentReturnTextView = getTextViewForRequest("getWithoutParamsAndNoContentReturnTextView");
        BaseRequest<StatusMessageResponse> getWithParamsAndNoContentReturnRequest = new BaseRequest.Builder<StatusMessageResponse>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/datanocontent", StatusMessageResponse.class)
                .listener(new RequestListener<StatusMessageResponse>() {
                    @Override
                    public void onSuccess(@NonNull Request<StatusMessageResponse> request, @NonNull NetworkResponse response, StatusMessageResponse v) {
                        setSuccessForTextView(getWithoutParamsAndNoContentReturnTextView, true);
                    }

                    @Override
                    public void onFailure(@NonNull Request<StatusMessageResponse> request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(getWithoutParamsAndNoContentReturnTextView, false);

                    }
                }).build();
        addRequestWithRandom(getWithParamsAndNoContentReturnRequest);

        final TextView getWithoutParamsAndPartialContentReturnTextView = getTextViewForRequest("getWithoutParamsAndPartialContentReturnTextView");
        BaseRequest<WrapperStatusMessageResponse> getWithParamsAndPartialContentReturnRequest = new BaseRequest.Builder<WrapperStatusMessageResponse>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/datapartialcontent", WrapperStatusMessageResponse.class)
                .listener(new RequestListener<WrapperStatusMessageResponse>() {
                    @Override
                    public void onSuccess(@NonNull Request<WrapperStatusMessageResponse> request, @NonNull NetworkResponse response, WrapperStatusMessageResponse v) {
                        setSuccessForTextView(getWithoutParamsAndPartialContentReturnTextView, v != null && v.getStatusList() != null && !v.getStatusList().isEmpty());
                    }

                    @Override
                    public void onFailure(@NonNull Request<WrapperStatusMessageResponse> request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(getWithoutParamsAndPartialContentReturnTextView, false);

                    }
                }).build();
        addRequestWithRandom(getWithParamsAndPartialContentReturnRequest);

        final TextView getWithoutParamsAndEmptyReturnTextView = getTextViewForRequest("getWithoutParamsAndEmptyReturn");

        BaseRequest<Void> getWithoutParamsAndEmptyReturnRequest = new BaseRequest.Builder<Void>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/nodata", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(getWithoutParamsAndEmptyReturnTextView, true);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
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
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(getWithParamsTextView, true);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(getWithParamsTextView, false);

                    }
                }).build();

        addRequestWithRandom(getWithParamsRequest);

        final TextView getWithParamsErrorTextView = getTextViewForRequest("getWithParamsError");
        BaseRequest<Void> getWithParamsErrorRequest = new BaseRequest.Builder<Void>(Request.Method.GET,
                "https://private-4b982e-neorequest.apiary-mock.com/get/errordata/3", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(getWithParamsErrorTextView, false);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
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
            public void onSuccess(@NonNull Request<StatusMessageResponse> request, @NonNull NetworkResponse response, StatusMessageResponse v) {
                setSuccessForTextView(postWithParamsTextView, true);
            }

            @Override
            public void onFailure(@NonNull Request<StatusMessageResponse> request, NetworkResponse response, VolleyError volleyError) {
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
        postJsonRequest.setSpecialChars("% 🤞 🌎 $ ~ ! @ # $ % ^ & * ( ) _ + \\");

        final TextView postWithJsonAndHeaderTextView = getTextViewForRequest("postWithJsonAndHeader");
        BaseRequest<StatusMessageResponse> postWithJsonAndHeaderRequest = new BaseRequest.Builder<StatusMessageResponse>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/json", StatusMessageResponse.class).listener(new RequestListener<StatusMessageResponse>() {
            @Override
            public void onSuccess(@NonNull Request<StatusMessageResponse> request, @NonNull NetworkResponse response, StatusMessageResponse v) {
                setSuccessForTextView(postWithJsonAndHeaderTextView, true);
            }

            @Override
            public void onFailure(@NonNull Request<StatusMessageResponse> request, NetworkResponse response, VolleyError volleyError) {
                setSuccessForTextView(postWithJsonAndHeaderTextView, false);

            }
        })
                .headers(fullHeaders)
                .json(postJsonRequest)
                .build();

        addRequestWithRandom(postWithJsonAndHeaderRequest);



        final TextView postWithStatusErrorTextView = getTextViewForRequest("postWithStatusError");
        BaseRequest<Void> postWithStatusErrorTextViewRequest = new BaseRequest.Builder<Void>(Request.Method.POST,
                "https://private-4b982e-neorequest.apiary-mock.com/post/errordata-json", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(postWithStatusErrorTextView, false);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(postWithStatusErrorTextView, (response != null && response.statusCode == 400));

                    }
                }).build();

        addRequestWithRandom(postWithStatusErrorTextViewRequest);
    }

    private void loadPatchRequest() {

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
        postJsonRequest.setSpecialChars("% 🤞 🌎 $ ~ ! @ # $ % ^ & * ( ) _ + \\");

        final TextView patchWithJsonAndHeaderTextView = getTextViewForRequest("patchWithJsonAndHeader");
        BaseRequest<StatusMessageResponse> postWithJsonAndHeaderRequest = new BaseRequest.Builder<StatusMessageResponse>(Request.Method.PATCH,
                "https://private-4b982e-neorequest.apiary-mock.com/patch/json", StatusMessageResponse.class).listener(new RequestListener<StatusMessageResponse>() {
            @Override
            public void onSuccess(@NonNull Request<StatusMessageResponse> request, @NonNull NetworkResponse response, StatusMessageResponse v) {
                setSuccessForTextView(patchWithJsonAndHeaderTextView, true);
            }

            @Override
            public void onFailure(@NonNull Request<StatusMessageResponse> request, NetworkResponse response, VolleyError volleyError) {
                setSuccessForTextView(patchWithJsonAndHeaderTextView, false);

            }
        })
                .headers(fullHeaders)
                .json(postJsonRequest)
                .build();

        addRequestWithRandom(postWithJsonAndHeaderRequest);



        final TextView patchWithStatusErrorTextView = getTextViewForRequest("patchWithStatusError");
        BaseRequest<Void> postWithStatusErrorTextViewRequest = new BaseRequest.Builder<Void>(Request.Method.PATCH,
                "https://private-4b982e-neorequest.apiary-mock.com/patch/errordata-json", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(patchWithStatusErrorTextView, false);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
                        setSuccessForTextView(patchWithStatusErrorTextView, (response != null && response.statusCode == 400));

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
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(putImageStreamTextView, true);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
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
                    public void onSuccess(@NonNull Request<Void> request, @NonNull NetworkResponse response, Void v) {
                        setSuccessForTextView(putImageMultipartTextView, true);
                    }

                    @Override
                    public void onFailure(@NonNull Request<Void> request, NetworkResponse response, VolleyError volleyError) {
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
