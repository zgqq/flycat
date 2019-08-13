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
package com.github.bootbox.alarm;

import com.github.bootbox.server.config.ServerEnvUtils;
import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

public class MailAlarmSender extends AbstractAlarmSender {

    private static Mailer mailer;
    private static String receiver;

    static {
        final String smtpHost = ServerEnvUtils.getProperty("bootbox.alarm.mail.sender.smtp");
        final Integer smtpPort = ServerEnvUtils.getIntegerProperty("bootbox.alarm.mail.sender.smtp.port");
        final String mailUser = ServerEnvUtils.getProperty("bootbox.alarm.mail.sender.user");
        final String mailPassword = ServerEnvUtils.getProperty("bootbox.alarm.mail.sender.password");
        receiver = ServerEnvUtils.getProperty("bootbox.alarm.mail.receiver");
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
