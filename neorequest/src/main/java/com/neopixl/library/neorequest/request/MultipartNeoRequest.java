package com.neopixl.library.neorequest.request;

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
 * Created by jjacquot on 04/10/16.
 */

public class MultipartNeoRequest<T> extends NeoRequest<T> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "bound-" + System.currentTimeMillis();

    private HashMap<String, NeoRequestData> multiPartData;

    public MultipartNeoRequest(int method, String url, Map<String, String> headers, HashMap<String, NeoRequestData> multipardData, NeoRequestListener<T> listener, Class<T> classResponse) {
        this(method, url, headers, null, null, multipardData, listener, classResponse);
    }

    public MultipartNeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, HashMap<String, NeoRequestData> multipardData, NeoRequestListener<T> listener, Class<T> classResponse) {
        this(method, url, headers, params, null, multipardData, listener, classResponse);
    }

    public MultipartNeoRequest(int method, String url, Map<String, String> headers, Object jsonObject, HashMap<String, NeoRequestData> multipardData, NeoRequestListener<T> listener, Class<T> classResponse) {
        this(method, url, headers, null, jsonObject, multipardData, listener, classResponse);
    }

    public MultipartNeoRequest(int method, String url, Map<String, String> headers, HashMap<String, NeoRequestData> multipardData, Class<T> classResponse) {
        this(method, url, headers, null, null, multipardData, null, classResponse);
    }

    public MultipartNeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, HashMap<String, NeoRequestData> multipardData, Class<T> classResponse) {
        this(method, url, headers, params, null, multipardData, null, classResponse);
    }

    public MultipartNeoRequest(int method, String url, Map<String, String> headers, Object jsonObject, HashMap<String, NeoRequestData> multipardData, Class<T> classResponse) {
        this(method, url, headers, null, jsonObject, multipardData, null, classResponse);
    }

    protected MultipartNeoRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Object jsonObject, HashMap<String, NeoRequestData> multipardData, NeoRequestListener<T> listener, Class<T> classResponse) {
        super(method, url, headers, params, jsonObject, listener, classResponse);
        this.multiPartData = multipardData;

        if (method == Method.GET) {
            throw new IllegalArgumentException("Cannot use multipart with GET request");
        }
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
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

    public HashMap<String, NeoRequestData> getMultiPartData() {
        return multiPartData;
    }

}
