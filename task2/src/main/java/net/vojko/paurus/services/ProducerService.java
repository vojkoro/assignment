package net.vojko.paurus.services;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.vojko.paurus.models.IncomingEvent;
import org.jboss.logging.Logger;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class ProducerService {

    @Inject
    FileService fileService;
    @Inject
    EventBus eventBus;
    @Inject
    Logger logger;

    public CompletableFuture<Void> startStreamingEvents() {
        var list = fileService.readFile("fo_random.txt");
        return CompletableFuture.runAsync(() -> {
                    list.forEach(this::sendMatchEvent);
                }
        );
    }


    public void sendMatchEvent(IncomingEvent element)  {
            eventBus.publish("incoming.events", element);
    }
}
