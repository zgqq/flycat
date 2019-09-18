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
package com.github.flycat.spi.impl.alarm;

import com.github.flycat.spi.alarm.AbstractAlarmSender;
import com.github.flycat.context.ApplicationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class MailAlarmSender extends AbstractAlarmSender {

    private final ApplicationConfiguration applicationConfiguration;
    private Mailer mailer;
    private String receiver;

    @Inject
    public MailAlarmSender(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.createSender();
    }

    private void createSender() {
        final String smtpHost = applicationConfiguration.getString("flycat.alarm.mail.sender.smtp");
        final Integer smtpPort = applicationConfiguration.getInteger("flycat.alarm.mail.sender.smtp.port");
        final String mailUser = applicationConfiguration.getString("flycat.alarm.mail.sender.user");
        final String mailPassword = applicationConfiguration.getString("flycat.alarm.mail.sender.password");
        receiver = applicationConfiguration.getString("flycat.alarm.mail.receiver");
        if (StringUtils.isNotBlank(smtpHost) && smtpPort != null) {
            mailer = MailerBuilder
                    .withSMTPServer(smtpHost, smtpPort, mailUser, mailPassword)
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .withSessionTimeout(10 * 1000)
                    .clearEmailAddressCriteria() // turns off email validation
                    .withProperty("mail.smtp.sendpartial", "true")
                    .withDebugLogging(true)
                    .buildMailer();
        }
    }

    @Override
    public void doSendNotify(String message) {
        if (message != null) {
            if (mailer != null && receiver != null) {
                Email email = EmailBuilder.startingBlank()
                        .from(receiver)
                        .to(receiver, receiver)
                        .withSubject("Alarming")
                        .withPlainText(message)
                        .withReturnReceiptTo()
                        .buildEmail();
                mailer.sendMail(email);
            }
        }
    }
}
