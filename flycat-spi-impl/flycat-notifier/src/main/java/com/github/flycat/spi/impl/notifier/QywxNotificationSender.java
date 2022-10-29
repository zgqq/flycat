package com.github.flycat.spi.impl.notifier;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.util.ConfigurationUtils;
import com.github.flycat.spi.cache.StandaloneCacheService;
import com.github.flycat.spi.json.JsonObject;
import com.github.flycat.spi.json.JsonUtils;
import com.github.flycat.spi.notifier.AbstractNotificationSender;
import com.github.flycat.spi.notifier.Message;
import com.github.flycat.util.http.HttpUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

public class QywxNotificationSender extends AbstractNotificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(QywxNotificationSender.class);

    private final ApplicationConfiguration applicationConfiguration;
    private final String corpid;
    private final String secret;
    private final Integer agentid;

    @Inject
    public QywxNotificationSender(ApplicationConfiguration applicationConfiguration, ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationConfiguration = applicationConfiguration;
        corpid = ConfigurationUtils.getString(applicationConfiguration, "flycat.qywx.sender.corpid");
        secret = ConfigurationUtils.getString(applicationConfiguration, "flycat.qywx.sender.secret");
        agentid = ConfigurationUtils.getInteger(applicationConfiguration, "flycat.qywx.receiver.agentid");
    }

    @Override
    public void doSend(Message message) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + secret;
        LOGGER.info("Getting token, url:{}", url);
        String content = HttpUtils.get(url);
        JsonObject jsonObject = JsonUtils.parseObject(content);
        String access_token = jsonObject.getString("access_token");
        if (Strings.isNullOrEmpty(access_token)) {
            LOGGER.error("Failed to get access token, url:{}, response:{}", url, content);
            return;
        }
        String msgUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + access_token;
        HashMap<Object, Object> data = new HashMap<>();
        data.put("touser", "@all");
        data.put("toparty", "PartyID1|PartyID2");
        data.put("totag", "TagID1 | TagID2");
        data.put("msgtype", "text");
        data.put("agentid", agentid);
        data.put("text", ImmutableMap.of("content", message.getDecoratedContent()));
        data.put("safe", 0);
        data.put("enable_id_trans", 0);
        data.put("enable_duplicate_check", 0);
        String json = JsonUtils.toJsonString(data);
        try {
            LOGGER.info("Sending message, url:{}, data:{}", msgUrl, json);
            String response = HttpUtils.postJson(msgUrl, json);
            LOGGER.info("Send message to qiye weixin, url:{}, data:{}, response:{}", msgUrl, json, response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message to qiye weixin, url:"+msgUrl+", data:"+ json, e);
        }
    }
}
