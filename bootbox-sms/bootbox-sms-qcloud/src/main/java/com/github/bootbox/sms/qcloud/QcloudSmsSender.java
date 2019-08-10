package com.github.bootbox.sms.qcloud;

import com.alibaba.fastjson.JSON;
import com.github.bootbox.sms.SmsSender;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QcloudSmsSender implements SmsSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(QcloudSmsSender.class);

    private final int appid;
    private final String appkey;

    public QcloudSmsSender(int appid, String appkey) {
        this.appid = appid;
        this.appkey = appkey;
    }

    @Override
    public void send(String phone, String msg) {
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.send(0, "86", phone,
                    msg, "", "");
            LOGGER.info("Qcloud sms, phone:{}, msg:{}, result:{}", phone, msg,
                    JSON.toJSONString(result)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

