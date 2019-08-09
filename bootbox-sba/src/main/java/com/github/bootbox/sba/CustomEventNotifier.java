package com.github.bootbox.sba;

import com.github.bootbox.alarm.AlarmUtils;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import de.codecentric.boot.admin.server.notify.LoggingNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

public class CustomEventNotifier extends AbstractStatusChangeNotifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingNotifier.class);
    private String template = "classpath:/META-INF/spring-boot-admin-server/mail/status-changed.html";
    private TemplateEngine templateEngine;

    public CustomEventNotifier(InstanceRepository repository, TemplateEngine templateEngine) {
        super(repository);
        this.templateEngine = templateEngine;
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            Context ctx = new Context();
            ctx.setVariable("event", event);
            ctx.setVariable("instance", instance);
            ctx.setVariable("lastStatus", getLastStatus(event.getInstance()));
            final String process = templateEngine.process(this.template, ctx);
            AlarmUtils.sendNotify(process);
        });
    }
}
