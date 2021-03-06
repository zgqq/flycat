package com.github.flycat.spi.notifier;

import java.util.Date;

public class Message {
    private String content;
    private int format;
    private Date createTime;

    private String decoratedContent;

    private String label;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean hasFormat(int format) {
        return (getFormat() & format) == format;
    }

    public String getDecoratedContent() {
        return decoratedContent;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDecoratedContent(String decoratedContent) {
        this.decoratedContent = decoratedContent;
    }
}
