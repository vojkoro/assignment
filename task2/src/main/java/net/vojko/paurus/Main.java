package net.vojko.paurus;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import net.vojko.paurus.services.EventProcessorService;
import net.vojko.paurus.services.ProducerService;

import java.util.concurrent.Future;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(ProcessingApp.class, args);
    }

    public static class ProcessingApp implements QuarkusApplication {

        @Inject
        EventProcessorService eventProcessorService;

        @Inject
        ProducerService producerService;

        @Override
        public int run(String... args) throws Exception {
            var isRunning = eventProcessorService.start();
            producerService.startStreamingEvents().get();
            isRunning.get();
            eventProcessorService.waitForProcessingToComplete();
            eventProcessorService.shutdown();
            eventProcessorService.getTimingInfo();
            System.exit(0);
            return 0;
        }
    }
}