/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.spi.sms.qcloud;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.spi.SpiService;
import com.github.flycat.spi.sms.SmsService;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named
public class QcloudSmsSender implements SmsService, SpiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QcloudSmsSender.class);

    private int appid;
    private String appkey;
    private SmsSingleSender ssender;
    private final ApplicationConfiguration applicationConfiguration;

    public QcloudSmsSender(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        createSender();
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public QcloudSmsSender(int appid, String appkey) {
        this.appid = appid;
        this.appkey = appkey;
        this.applicationConfiguration = null;
    }

    @Override
    public void send(String phone, String msg) {
        try {
            SmsSingleSenderResult result = ssender.send(0, "86", phone,
                    msg, "", "");
            LOGGER.info("Qcloud sms, phone:{}, msg:{}, result:{}", phone, msg,
                    result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createSender() {
        this.appid = getInteger("flycat.sms.qcloud.appid");
        this.appkey = getString("flycat.sms.qcloud.appKey");
        ssender = new SmsSingleSender(appid, appkey);
    }
}

