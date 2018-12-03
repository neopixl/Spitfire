package com.neopixl.spitfireapp.model;

import java.util.List;

/**
 * Created by Florian ALONSO on 12/30/16.
 * For Neopixl
 */

public class WrapperStatusMessageResponse {

    private List<StatusMessageResponse> statusList;

    public WrapperStatusMessageResponse() {
    }

    public List<StatusMessageResponse> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusMessageResponse> statusList) {
        this.statusList = statusList;
    }
}
