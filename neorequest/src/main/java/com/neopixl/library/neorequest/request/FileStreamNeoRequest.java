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
        super(method, url, headers, listener, classResponse);
        this.partData = partData;

        if (method == Method.GET) {
            throw new IllegalArgumentException("Cannot use streamfile with GET request");
        }
    }

    @Override
    public String getBodyContentType() {
        return getPartData().getType();
    }

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

    public NeoRequestData getPartData() {
        return partData;
    }
}
