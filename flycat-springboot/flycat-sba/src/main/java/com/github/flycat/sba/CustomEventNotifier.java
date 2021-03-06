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
package com.github.flycat.sba;

import com.github.flycat.spi.notifier.NotifierUtils;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

public class CustomEventNotifier extends AbstractStatusChangeNotifier {
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
            NotifierUtils.sendNotification(process);
        });
    }
}
