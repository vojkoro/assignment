package net.vojko.paurus.services;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import net.vojko.paurus.config.AppConfig;
import net.vojko.paurus.models.IncomingEvent;
import net.vojko.paurus.repositories.MatchEventRepository;
import net.vojko.paurus.models.TimingInfo;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;


@ApplicationScoped
public class EventProcessorService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EventProcessorService.class);


    public static final int CHECK_EVENT_PERIOD = 5;
    public static final int CAPACITY = 100000;
    public static final int POOL_SIZE = 32;
    private final ReentrantLock lock = new ReentrantLock();

    private final AtomicInteger atomicLatch = new AtomicInteger(0);
    private final ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<CompletableFuture<IncomingEvent>>> eventsInProcessing = new ConcurrentHashMap<>();
    private final BlockingQueue<IncomingEvent> backPressureQueue = new LinkedBlockingQueue<>(CAPACITY);
    private final BlockingQueue<IncomingEvent> eventsCompleted = new LinkedBlockingQueue<>();


    @Inject
    AppConfig appConfig;
    @Inject
    MatchEventRepository matchEventRepository;

    void onStart(@Observes StartupEvent ev) {
        scheduler.scheduleAtFixedRate(this::checkEvents, 0, CHECK_EVENT_PERIOD, TimeUnit.MILLISECONDS);
    }

    @ConsumeEvent(value = "incoming.events", blocking = true)
    public void handleIncomingEvent(Message<IncomingEvent> message) {
        IncomingEvent event = message.body();
        try {
            backPressureQueue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Failed to add event to the queue", e);
        }
    }

    public CompletableFuture<Void> start() {
        return startEventProcessor();
    }

    private CompletableFuture<Void> startEventProcessor() {
        return CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    IncomingEvent event = backPressureQueue.poll(1, TimeUnit.SECONDS);
                    addEvent(event);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, executorService);
    }

    private void addEvent(IncomingEvent event) {
        eventsInProcessing.computeIfAbsent(event.matchId(), e -> new ConcurrentLinkedQueue<>())
                .add(CompletableFuture.supplyAsync(() -> processEvent(event), executorService));
        atomicLatch.incrementAndGet();
    }

    public void waitForProcessingToComplete() {
        while (atomicLatch.get() > 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void checkEvents() {
        if (atomicLatch.get() == 0) {
            return;
        }
        
        lock.lock();
        try {
            collectProcessedEvents();
            saveProcessedEvents();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }


    public void saveProcessedEvents() {
        if (eventsCompleted.isEmpty()) {
            return;
        }

        List<IncomingEvent> batch = Collections.synchronizedList(new ArrayList<>());

        if (appConfig.maxBatchSize() > 0) {
            eventsCompleted.drainTo(batch, appConfig.maxBatchSize());
        } else {
            eventsCompleted.drainTo(batch);
        }

        if (!batch.isEmpty()) {
            insertWithCopyCommand(batch);
            batch.forEach(v -> atomicLatch.decrementAndGet());
        }
    }



    private void insertWithCopyCommand(List<IncomingEvent> batch) {
        StringBuilder matchEventBuilder = new StringBuilder();
        for (IncomingEvent match : batch) {
            matchEventBuilder.append(match.matchId())
                    .append(',')
                    .append(match.marketId())
                    .append(',')
                    .append(match.outcomeId())
                    .append(',')
                    .append(match.specifiers() != null ? match.specifiers() + "," : ",")
                    .append(match.internalSequence())
                    .append(',')
                    .append(match.processingTime())
                    .append('\n');
        }
        String matchEventCsv = matchEventBuilder.toString();
        matchEventRepository.copyInsertMatchEvent(matchEventCsv);
    }


    public void collectProcessedEvents() throws ExecutionException, InterruptedException {
        AtomicLong counter = new AtomicLong(0);
        if (!areAllEventsProcessed()) {
            for (var entry : eventsInProcessing.keySet()) {
                var iterator = eventsInProcessing.get(entry).iterator();
                while (iterator.hasNext()) {
                    var eventFuture = iterator.next();
                    if (eventFuture == null || !eventFuture.isDone()) {
                        break;
                    }
                    var result = eventFuture.get();
                    eventsCompleted.add(result);
                    counter.incrementAndGet();
                    iterator.remove();
                }
            }
        }
        counter.get();
    }

    public boolean areAllEventsProcessed() {
        return eventsInProcessing.values()
                .stream()
                .allMatch(Queue::isEmpty);
    }

    public IncomingEvent processEvent(IncomingEvent event) {
        long startTime = System.currentTimeMillis();
        if (appConfig.useDelay()) {
            simulateDelay();
        }
        long stopTime = System.currentTimeMillis();
        long randomProcessingTime = stopTime - startTime;
        return new IncomingEvent(event.matchId(), event.marketId(), event.outcomeId(), event.specifiers(), event.internalSequence(), randomProcessingTime);

    }

    public TimingInfo getTimingInfo() {
        var timeInfo = matchEventRepository.getTiming();
        if (timeInfo != null) {
            logger.info("Inserted {} rows in {} miliseconds", timeInfo.count(), timeInfo.elapsedTime());
        }
        return timeInfo;
    }

    public void shutdown() throws InterruptedException {
        scheduler.shutdown();
        scheduler.awaitTermination(200, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(200, TimeUnit.SECONDS);
    }

    //used for internal testing to simulate delays.
    //if this is used, we need to change configuration of thread pool size and backpressure queue size
    //changes depend on system resources and expected throughput
    private void simulateDelay() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 200));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
