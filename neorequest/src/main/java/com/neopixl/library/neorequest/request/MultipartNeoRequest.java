package com.neopixl.library.neorequest.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neopixl.library.neorequest.NeoRequestManager;
import com.neopixl.library.neorequest.listener.NeoRequestListener;
import com.neopixl.library.neorequest.model.NeoRequestData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian ALONSO on 12/30/16.
 */

public class MultipartNeoRequest<T> extends NeoRequest<T> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "bound-" + System.currentTimeMillis();

    @Nullable
    private HashMap<String, NeoRequestData> multiPartData;

    /**
     * Builder used to create the final request
     * @param <T> type for the request
     */
    public static class Builder<T> extends NeoRequest.Builder<T> {

        private HashMap<String, NeoRequestData> multiPartData;

        /**
         * Constructor for the builder
         * @param method The <b>com.android.volley.Request.Method</b> of the URL
         * @param url    The URL
         * @param classResponse the class used to parse the response associated to the request.
         */
        public Builder(int method, @NonNull String url, Class classResponse) {
            super(method, url, classResponse);
        }

        /**
         *
         * @param listener {@link NeoRequestListener}, can be null
         * @return
         */
        @Override
        public Builder listener(@Nullable NeoRequestListener listener) {
            super.listener(listener);
            return this;
        }

        /**
         * Set the multipart data for the request
         * @param multiPartData HashMap&lt;String, NeoRequestData&gt; multiPartData, not null
         * @return Builder {@link Builder}
         */
        public Builder multiPartData(@NonNull HashMap<String, NeoRequestData> multiPartData) {
            this.multiPartData = new HashMap<>(multiPartData);
            return this;
        }

        /**
         * Build a new MultipartNeoRequest object based on the Builder's parameters
         * @return MultipartNeoRequest {@link MultipartNeoRequest}
         */
        public MultipartNeoRequest<T> build() {
            return new MultipartNeoRequest<T>(this);
        }
    }

    private MultipartNeoRequest(Builder builder) {
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
            Object jsonBody = getJsonObjectBody();
            if (jsonBody == null) {
                Map<String, String> params = getParams();
                if (params != null && params.size() > 0) {
                    textParse(dos, params, getParamsEncoding());
                }
            } else {
                jsonParse(dos, jsonBody);
            }

            // populate data byte payload
            Map<String, NeoRequestData> data = getMultiPartData();
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
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
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
    private void jsonParse(DataOutputStream dataOutputStream, Object jsonObject) throws IOException {
        try {
            String json = NeoRequestManager.getObjectMapper().writeValueAsString(jsonObject);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(twoHyphens + boundary + lineEnd);
            stringBuilder.append("Content-Disposition: form-data; name=\"jsonObject\"" + lineEnd);
            stringBuilder.append("Content-Type: application/json" + lineEnd);
            stringBuilder.append(lineEnd);
            stringBuilder.append(json);
            stringBuilder.append(lineEnd);
            dataOutputStream.writeBytes(stringBuilder.toString());
        } catch (JsonProcessingException e) {
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
    private void dataParse(DataOutputStream dataOutputStream, Map<String, NeoRequestData> data) throws IOException {
        for (Map.Entry<String, NeoRequestData> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
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
    private void buildDataPart(DataOutputStream dataOutputStream, NeoRequestData dataFile, String inputName) throws IOException {
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
     * @return HashMap&lt;String, NeoRequestData&gt; {@link NeoRequestData}, can be null
     */
    @Nullable
    public HashMap<String, NeoRequestData> getMultiPartData() {
        return multiPartData;
    }

}
