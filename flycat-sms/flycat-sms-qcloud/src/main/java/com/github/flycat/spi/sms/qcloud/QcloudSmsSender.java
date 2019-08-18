/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.spi.sms.qcloud;

import com.alibaba.fastjson.JSON;
import com.github.flycat.spi.SpiService;
import com.github.flycat.spi.sms.SmsService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class QcloudSmsSender implements SmsService, SpiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QcloudSmsSender.class);

    private int appid;
    private String appkey;
    private SmsSingleSender ssender;

    public QcloudSmsSender() {
    }

    public QcloudSmsSender(int appid, String appkey) {
        this.appid = appid;
        this.appkey = appkey;
    }

    @Override
    public void send(String phone, String msg) {
        try {
            SmsSingleSenderResult result = ssender.send(0, "86", phone,
                    msg, "", "");
            LOGGER.info("Qcloud sms, phone:{}, msg:{}, result:{}", phone, msg,
                    JSON.toJSONString(result)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void createService() {
        this.appid = getInteger("flycat.sms.qcloud.appid");
        this.appkey = getString("flycat.sms.qcloud.appKey");
        ssender = new SmsSingleSender(appid, appkey);
    }
}
