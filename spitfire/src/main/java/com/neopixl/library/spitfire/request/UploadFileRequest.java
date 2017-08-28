package com.neopixl.library.spitfire.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.neopixl.library.spitfire.listener.RequestListener;
import com.neopixl.library.spitfire.model.RequestData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Florian ALONSO on 14/10/16.
 */

public class UploadFileRequest<T> extends AbstractRequest<T> {

    private RequestData partData;

    public static class Builder<T> extends AbstractBuilder<T, UploadFileRequest<T>> {

        private RequestData partData;

        /**
         * Default
         *
         * @param method        used to send the request
         * @param url           given url to access the resource
         * @param classResponse class used to parse the response
         */
        public Builder(int method, @NonNull String url, Class<T> classResponse) {
            super(method, url, classResponse);
        }

        public Builder<T> partData(@NonNull RequestData partData) {
            this.partData = partData;
            return this;
        }

        @Override
        public Builder<T> listener(@Nullable RequestListener<T> listener) {
            super.listener(listener);
            return this;
        }

        @Override
        public Builder<T> headers(@NonNull Map headers) {
            super.headers(headers);
            return this;
        }

        /**
         * Create a request based on the current request
         * @return The request
         */
        public UploadFileRequest<T> build() {
            return new UploadFileRequest<T>(this);
        }
    }

    /**
     * Constructor using the builder
     * @param builder {@link Builder}
     */

    public UploadFileRequest(Builder<T> builder) {
        super(builder);
        this.partData = builder.partData;

        if (partData == null) {
            throw new IllegalArgumentException("Partdata should not be null.");
        }

        if (builder.method == Method.GET) {
            throw new IllegalArgumentException("Cannot use streamfile with GET request");
        }
    }

    /**
     * Returns the content type of the POST or PUT body.
     * @return String
     */
    @Override
    @NonNull
    public String getBodyContentType() {
        return getPartData().getType();
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(getPartData().getContent());
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the part data of the request {@link RequestData}
     * @return NeoRequestData
     */
    public RequestData getPartData() {
        return partData;
    }
}
