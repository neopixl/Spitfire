package com.neopixl.library.neorequest.request;

import com.android.volley.AuthFailureError;
import com.neopixl.library.neorequest.listener.NeoRequestListener;
import com.neopixl.library.neorequest.model.NeoRequestData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Florian ALONSO on 14/10/16.
 */

public class FileStreamNeoRequest<T> extends AbstractNeoRequest<T> {

    private NeoRequestData partData;

    public FileStreamNeoRequest(int method, String url, Map<String, String> headers, NeoRequestData partData, Class<T> classResponse) {
        this(method, url, headers, partData, null, classResponse);
    }

    public FileStreamNeoRequest(int method, String url, Map<String, String> headers, NeoRequestData partData, NeoRequestListener<T> listener, Class<T> classResponse) {
        super(method, url, headers, listener, classResponse, false);
        this.partData = partData;

        if (method == Method.GET) {
            throw new IllegalArgumentException("Cannot use streamfile with GET request");
        }
    }

    /**
     * Returns the content type of the POST or PUT body.
     * @return String
     */
    @Override
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
     * Get the part data of the request {@link NeoRequestData}
     * @return
     */
    public NeoRequestData getPartData() {
        return partData;
    }
}
