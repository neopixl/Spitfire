package com.neopixl.library.spitfire.mock;

import java.util.List;

/**
 * Created by Florian ALONSO on 8/28/17.
 * For Neopixl
 */

public class DummyResponse {
    private String message;
    private int id;
    private List<DummyResponse> childrens;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DummyResponse> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<DummyResponse> childrens) {
        this.childrens = childrens;
    }
}
