package com.github.flycat.spi.impl.notifier;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.util.ConfigurationUtils;
import com.github.flycat.spi.json.JsonObject;
import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.spi.notifier.AbstractNotificationSender;
import com.github.flycat.spi.notifier.Message;
import com.github.flycat.util.http.HttpUtils;
import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

public class QywxNotificationSender extends AbstractNotificationSender {

    private final ApplicationConfiguration applicationConfiguration;
    private final String corpid;
    private final String secret;

    @Inject
    public QywxNotificationSender(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        corpid = ConfigurationUtils.getString(applicationConfiguration, "flycat.qywx.sender.corpid");
        secret = ConfigurationUtils.getString(applicationConfiguration, "flycat.qywx.sender.secret");
    }

    @Override
    public void doSend(Message message) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + secret;
        String content = HttpUtils.get(url);
        JsonObject jsonObject = JsonUtils.parseObject(content);
        String access_token = jsonObject.getString("access_token");
        String msgUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + access_token;
        HashMap<Object, Object> data = new HashMap<>();
        data.put("touser", "@all");
        data.put("toparty", "PartyID1|PartyID2");
        data.put("totag", "TagID1 | TagID2");
        data.put("msgtype", "text");
        data.put("agentid", 1000002);
        data.put("text", ImmutableMap.of("content", message.getDecoratedContent()));
        data.put("safe", 0);
        data.put("enable_id_trans", 0);
        data.put("enable_duplicate_check", 0);
        try {
            HttpUtils.postJson(msgUrl, JsonUtils.toJsonString(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
