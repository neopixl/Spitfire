package com.neopixl.library.neorequest.model;

/**
 * Created by Florian ALONSO on 10/14/16.
 * For Neopixl
 */

public class NeoRequestData {
    private String fileName;
    private byte[] content;
    private String type;

    /**
     * Default data part
     */
    public NeoRequestData() {
    }

    /**
     * Constructor with data.
     *
     * @param name label of data
     * @param data byte data
     */
    public NeoRequestData(String name, byte[] data) {
        fileName = name;
        content = data;
    }

    /**
     * Constructor with mime data type.
     *
     * @param name     label of data
     * @param data     byte data
     * @param mimeType mime data like "image/jpeg"
     */
    public NeoRequestData(String name, byte[] data, String mimeType) {
        fileName = name;
        content = data;
        type = mimeType;
    }

    /**
     * Get the file name.
     *
     * @return file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name.
     *
     * @param fileName string file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the content
     *
     * @return byte file data
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Set the content
     *
     * @param content byte file data
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Get the mime type.
     *
     * @return mime type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the mime type.
     *
     * @param type mime type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Clear the current content
     */
    public void clear() {
        content = new byte[0];
    }
}
