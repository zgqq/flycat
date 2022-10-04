package com.github.flycat.spi.notifier;

import java.util.Date;

public class Message {
    private String content;
    private int format;
    private Date createTime;

    private String decoratedContent;

    private String label;

    private boolean preventRepeat;
    private int repeatIntervalSeconds = 3600 * 24;
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

    public boolean isPreventRepeat() {
        return preventRepeat;
    }

    public void setPreventRepeat(boolean preventRepeat) {
        this.preventRepeat = preventRepeat;
    }

    public static Message createDecoratedMessage(String content) {
        final Message newMessage = new Message();
        newMessage.setContent(content);
        newMessage.setFormat(MessageFormat.WITH_NOTIFICATION_TIME | MessageFormat.WITH_APP_NAME | MessageFormat.WITH_SERVER_IP
        );
        return newMessage;
    }

    public int getRepeatIntervalSeconds() {
        return repeatIntervalSeconds;
    }

    public void setRepeatIntervalSeconds(int repeatIntervalSeconds) {
        this.repeatIntervalSeconds = repeatIntervalSeconds;
    }
}
