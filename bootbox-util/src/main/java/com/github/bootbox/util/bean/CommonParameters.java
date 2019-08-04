package com.github.bootbox.util.bean;

import com.github.bootbox.util.page.PageQuery;

public class CommonParameters implements PageQuery {

    // data
    protected Integer pageSize;
    protected Integer pageNum;
    protected Integer opType;
    protected String debug;

    protected Integer term;
    protected Integer channel;
    protected String version;
    protected Integer uid;
    protected String app;
    protected String udid;

    // common
    protected Integer opUid;
    protected Integer opTerm;
    protected Integer opChannel;
    protected String opVersion;
    protected String opApp;
    protected String opUdid;

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Integer getOpTerm() {
        return opTerm;
    }

    public void setOpTerm(Integer opTerm) {
        this.opTerm = opTerm;
    }

    public Integer getOpChannel() {
        return opChannel;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public void setOpChannel(Integer opChannel) {
        this.opChannel = opChannel;
    }

    public String getOpVersion() {
        return opVersion;
    }

    public void setOpVersion(String opVersion) {
        this.opVersion = opVersion;
    }

    public String getOpApp() {
        return opApp;
    }

    public void setOpApp(String opApp) {
        this.opApp = opApp;
    }

    public String getOpUdid() {
        return opUdid;
    }

    public void setOpUdid(String opUdid) {
        this.opUdid = opUdid;
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public Integer getOpUid() {
        return opUid;
    }

    public void setOpUid(Integer opUid) {
        this.opUid = opUid;
    }

    public void makeOpFieldsAsData() {
        setUid(opUid);
        setUdid(opUdid);
        setTerm(opTerm);
        setVersion(opVersion);
        setChannel(opChannel);
    }
}
