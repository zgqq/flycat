package com.github.bootbox.starter.app.config;

import java.util.StringJoiner;

public class MaintainConf {
    private String responseMsg;
    private Integer responseSwitch;

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public Integer getResponseSwitch() {
        return responseSwitch;
    }

    public void setResponseSwitch(Integer responseSwitch) {
        this.responseSwitch = responseSwitch;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MaintainConf.class.getSimpleName() + "[", "]")
                .add("responseMsg='" + responseMsg + "'")
                .add("responseSwitch=" + responseSwitch)
                .toString();
    }
}
