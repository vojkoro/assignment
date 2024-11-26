package net.vojko.paurus;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.vojko.paurus.repositories.MatchEventRepository;
import net.vojko.paurus.services.FileService;
import net.vojko.paurus.services.EventProcessorService;
import net.vojko.paurus.services.ProducerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
class EventProcessingTest {


    @Inject
    EventProcessorService eventProcessorService;

    @Inject
    ProducerService producerService;

    @Inject
    FileService fileService;

    @Inject
    MatchEventRepository matchEventRepository;

    @BeforeEach
    void setUp() {
        //if we add some more test that will use database, we need to clean DB before each test
        //e.g. make test independent
        matchEventRepository.truncateAndResetSeq();
    }

    @Test
    void eventStreamingSimulation() throws InterruptedException, ExecutionException {
        var isRunning = eventProcessorService.start();
        producerService.startStreamingEvents().get();
        isRunning.get();
        eventProcessorService.waitForProcessingToComplete();
        eventProcessorService.shutdown();
        var timingInfo = eventProcessorService.getTimingInfo();
        assertThat(timingInfo.count(), is(302536L));
    }

    @Test
    void testFileContentParser(){
        String file = """
                MATCH_ID|MARKET_ID|OUTCOME_ID|SPECIFIERS
                'sr:match:14182869'|210|'4'|'setnr=1|gamenr=3'
                'sr:match:14182869'|207|'876'|'setnr=2
                'sr:match:13246018'|45|'322'|
                """ ;

        InputStream inputStream = new ByteArrayInputStream(file.getBytes(StandardCharsets.UTF_8));
        var events = fileService.parseEventsFromInputStream(inputStream);
        assertThat(events.size(), is(3));
    }

    @Test
    void testLineParser() {
        String data = """
                'sr:match:14182869'|210|'4'|'setnr=1|gamenr=3'
                'sr:match:14182869'|207|'876'|'setnr=2
                'sr:match:13246018'|45|'322'|
                """ ;

        String[] lines = data.split("\n");
        var match1 = fileService.parseEventFromLine(lines[0]);
        assertThat(match1.matchId(), equalTo("sr:match:14182869"));
        assertThat(match1.marketId(), equalTo(210));
        assertThat(match1.outcomeId(), equalTo("4"));
        assertThat(match1.specifiers(), equalTo("setnr=1|gamenr=3"));
        assertThat(match1.internalSequence(), equalTo(1L));

        var match3 = fileService.parseEventFromLine(lines[2]);
        assertThat(match3.matchId(), equalTo("sr:match:13246018"));
        assertThat(match3.marketId(), equalTo(45));
        assertThat(match3.outcomeId(), equalTo("322"));
        assertThat(match3.specifiers(), Matchers.nullValue());
        assertThat(match3.internalSequence(), equalTo(2L));
    }
}