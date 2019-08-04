package com.github.bootbox.alarm;

import com.github.bootbox.util.env.ServerEnvUtils;
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
