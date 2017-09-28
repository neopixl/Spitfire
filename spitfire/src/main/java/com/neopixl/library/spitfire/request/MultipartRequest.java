package com.neopixl.library.spitfire.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neopixl.library.spitfire.SpitfireManager;
import com.neopixl.library.spitfire.listener.RequestListener;
import com.neopixl.library.spitfire.model.RequestData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Florian ALONSO on 12/30/16.
 */

public class MultipartRequest<T> extends BaseRequest<T> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "bound-" + System.currentTimeMillis();

    @Nullable
    private HashMap<String, List<RequestData>> multiPartData;

    /**
     * Builder used to create the final request
     * @param <T> type for the request
     */
    public static class Builder<T> extends BaseRequest.Builder<T> {

        private HashMap<String, List<RequestData>> multiPartData = new HashMap<>();

        /**
         * Constructor for the builder
         * @param method The <b>com.android.volley.Request.Method</b> of the URL
         * @param url    The URL
         * @param classResponse the class used to parse the response associated to the request.
         */
        public Builder(int method, @NonNull String url, Class<T> classResponse) {
            super(method, url, classResponse);
        }

        /**
         * Specifies the object
         * @param jsonObject The object to be embedded in the body, can be null
         * @return Builder {@link BaseRequest.Builder}
         */
        public Builder<T> object(@Nullable Object jsonObject) {
            super.object(jsonObject);
            return this;
        }

        /**
         * Set the parameters for the request
         * @param parameters Map&lt;String, String&gt;, not null
         * @return Builder {@link BaseRequest.Builder}
         */
        public Builder<T> parameters(@NonNull  Map<String, String> parameters) {
            super.parameters(parameters);
            return this;
        }

        /**
         * Set the listener for the request
         * @param listener {@link RequestListener}, can be null
         * @return
         */
        @Override
        public Builder<T> listener(@Nullable RequestListener<T> listener) {
            super.listener(listener);
            return this;
        }

        /**
         * Set the headers for the request
         * @param headers used to send the request, not null
         * @return
         */
        @Override
        public Builder<T> headers(@NonNull Map<String, String>  headers) {
            super.headers(headers);
            return this;
        }

        /**
         * Add the multipartData map for the request builder
         * @param multiPartData HashMap&lt;String, NeoRequestData&gt; multiPartData, not null
         * @return Builder {@link Builder}
         */
        public Builder<T> multiPartData(@NonNull HashMap<String, RequestData> multiPartData) {
            for (Map.Entry<String, RequestData> entry : multiPartData.entrySet()) {
                List<RequestData> currentAddedList = this.multiPartData.get(entry.getKey());
                if (currentAddedList == null) {
                    currentAddedList = new ArrayList<>();
                }

                currentAddedList.add(entry.getValue());
                this.multiPartData.put(entry.getKey(), currentAddedList);
            }

            return this;
        }

        /**
         * Set the multipart data list for the request builder
         * @param multiPartData HashMap&lt;String, List&lt;NeoRequestData&gt;&gt; multiPartData, not null
         * @return Builder {@link Builder}
         */
        public Builder<T> multiPartDataList(@NonNull HashMap<String, List<RequestData>> multiPartData) {
            for (Map.Entry<String, List<RequestData>> entry : multiPartData.entrySet()) {
                List<RequestData> currentAddedList = this.multiPartData.get(entry.getKey());
                if (currentAddedList == null) {
                    currentAddedList = new ArrayList<>();
                }

                currentAddedList.addAll(entry.getValue());
                this.multiPartData.put(entry.getKey(), currentAddedList);
            }

            return this;
        }

        /**
         * Insert the data in the given key
         * @param key String key of the data, not null
         * @param partData RequestData data, not null
         * @return
         */
        public Builder<T> insertMultiPartData(@NonNull String key, @NonNull RequestData partData) {
            List<RequestData> currentAddedList = this.multiPartData.get(key);
            if (currentAddedList == null) {
                currentAddedList = new ArrayList<>();
            }

            currentAddedList.add(partData);
            this.multiPartData.put(key, currentAddedList);
            return this;
        }

        /**
         * Build a new MultipartNeoRequest object based on the Builder's parameters
         * @return MultipartNeoRequest {@link MultipartRequest}
         */
        public MultipartRequest<T> build() {
            return new MultipartRequest<T>(this);
        }
    }

    private MultipartRequest(Builder<T> builder) {
        super(builder);

        this.multiPartData = builder.multiPartData;

        if (builder.method == Method.GET) {
            throw new IllegalArgumentException("Cannot use multipart with GET request");
        }
    }

    /**
     * Returns the content type of the POST or PUT body.
     * @return String
     */
    @Override
    @NonNull
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    /**
     * Returns the raw POST or PUT body to be sent. (Can be null)
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    @Override
    @Nullable
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // populate text payload
            Object jsonBody = getJsonObject();
            if (jsonBody == null) {
                Map<String, String> params = getParams();
                if (params != null && params.size() > 0) {
                    textParse(dos, params, getParamsEncoding());
                }
            } else {
                jsonParse(dos, jsonBody);
            }

            // populate data byte payload
            Map<String, List<RequestData>> data = getMultiPartData();
            if (data != null && data.size() > 0) {
                dataParse(dos, data);
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     * @throws IOException
     */
    public void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    /**
     * Parse object into data output stream with JSON
     * With callback to text params
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param jsonObject       the object to parse
     * @throws IOException
     */
    public void jsonParse(DataOutputStream dataOutputStream, Object jsonObject) throws IOException {
        try {
            String json = SpitfireManager.getObjectMapper().writeValueAsString(jsonObject);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(twoHyphens + boundary + lineEnd);
            stringBuilder.append("Content-Disposition: form-data; name=\"jsonObject\"" + lineEnd);
            stringBuilder.append("Content-Type: application/json" + lineEnd);
            stringBuilder.append(lineEnd);
            stringBuilder.append(json);
            stringBuilder.append(lineEnd);
            dataOutputStream.writeBytes(stringBuilder.toString());
        } catch (JsonProcessingException e) {// shouldn't really happen, but is declared as possibility so:
            textParse(dataOutputStream, getParams(), getParamsEncoding());
        }
    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @param data             loop through data
     * @throws IOException
     */
    public void dataParse(DataOutputStream dataOutputStream, Map<String, List<RequestData>> data) throws IOException {
        for (Map.Entry<String, List<RequestData>> entry : data.entrySet()) {
            for (RequestData datapart : entry.getValue()) {
                buildDataPart(dataOutputStream, datapart, entry.getKey());
            }
        }
    }

    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     * @throws IOException
     */
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        //dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as CovedDataPartRequest from collection
     * @param inputName        name of data input
     * @throws IOException
     */
    private void buildDataPart(DataOutputStream dataOutputStream, RequestData dataFile, String inputName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    /**
     * Get multiPart data for the current request.
     * @return HashMap&lt;String, List&lt;NeoRequestData&gt;&gt; {@link RequestData}, can be null
     */
    @Nullable
    public HashMap<String, List<RequestData>> getMultiPartData() {
        return multiPartData;
    }

}
