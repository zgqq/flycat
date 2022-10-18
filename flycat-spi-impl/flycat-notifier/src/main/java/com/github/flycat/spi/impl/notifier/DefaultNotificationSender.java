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
package com.github.flycat.spi.impl.notifier;

import com.github.flycat.context.ApplicationConfiguration;
import com.github.flycat.context.ApplicationContext;
import com.github.flycat.context.util.ConfigurationUtils;
import com.github.flycat.spi.notifier.AbstractNotificationSender;
import com.github.flycat.spi.notifier.Message;
import com.github.flycat.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class DefaultNotificationSender extends AbstractNotificationSender {

    private final ApplicationConfiguration applicationConfiguration;
    private final AbstractNotificationSender notificationSender;

    public DefaultNotificationSender() {
        this(null, null);
    }

    @Inject
    public DefaultNotificationSender(ApplicationConfiguration applicationConfiguration,
                                     ApplicationContext applicationContext
                                     ) {
        super(applicationContext);
        this.applicationConfiguration = applicationConfiguration;
        if (StringUtils.isNotBlank(ConfigurationUtils.getString(applicationConfiguration, "flycat.mail.sender.smtp"))) {
            this.notificationSender = new MailNotificationSender(this.applicationConfiguration, applicationContext);

        } else if (StringUtils.isNotBlank(ConfigurationUtils.getString(applicationConfiguration,
                "flycat.qywx.sender.corpid"))){
            this.notificationSender = new QywxNotificationSender(this.applicationConfiguration, applicationContext);
        } else {
            throw new RuntimeException("Not found notification");
        }
    }

    @Override
    public void doSend(Message message) {
        this.notificationSender.doSend(message);
    }



}
