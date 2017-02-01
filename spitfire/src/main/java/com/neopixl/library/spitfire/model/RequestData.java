package com.neopixl.library.spitfire.model;

import android.support.annotation.NonNull;

/**
 * Created by Florian ALONSO on 10/14/16.
 * For Neopixl
 */

public class RequestData {

    @NonNull
    private String fileName;

    @NonNull
    private byte[] content;

    @NonNull
    private String type;

    /**
     * Default data part
     */
    public RequestData() {
    }

    /**
     * Constructor with data.
     *
     * @param name label of data, not null
     * @param data byte data, not null
     */
    public RequestData(@NonNull String name, @NonNull byte[] data) {
        fileName = name;
        content = data;
    }

    /**
     * Constructor with mime data type.
     *
     * @param name     label of data, not null
     * @param data     byte data, not null
     * @param mimeType mime data like "image/jpeg", not null
     */
    public RequestData(@NonNull String name, @NonNull byte[] data, @NonNull String mimeType) {
        fileName = name;
        content = data;
        type = mimeType;
    }

    /**
     * Get the file name.
     *
     * @return file name, not null
     */
    @NonNull
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name.
     *
     * @param fileName string file name, not null
     */
    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the content
     *
     * @return byte file data, not null
     */
    @NonNull
    public byte[] getContent() {
        return content;
    }

    /**
     * Set the content
     *
     * @param content byte file data, not null
     */
    public void setContent(@NonNull byte[] content) {
        this.content = content;
    }

    /**
     * Get the mime type.
     *
     * @return mime type, not null
     */
    public String getType() {
        return type;
    }

    /**
     * Set the mime type.
     *
     * @param type mime type, not null
     */
    public void setType(@NonNull String type) {
        this.type = type;
    }

    /**
     * Clear the current content
     */
    public void clear() {
        content = new byte[0];
    }
}
