package net.vojko.paurus.services;

import jakarta.enterprise.context.ApplicationScoped;
import net.vojko.paurus.models.IncomingEvent;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class FileService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FileService.class);
    private final AtomicLong atomicLong = new AtomicLong(1);

    private final Pattern pattern = Pattern.compile("'[^']*'|[^|]+");


    public List<IncomingEvent> readFile(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return parseEventsFromInputStream(inputStream);
        } catch (IOException e) {
           logger.error("Error loading file", e);
        }
        return List.of();
    }

    public List<IncomingEvent> parseEventsFromInputStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            List<IncomingEvent> events = new ArrayList<>();
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                var matchEvent = parseEventFromLine(line);
                events.add(matchEvent);
            }
            return events;
        } catch (IOException e) {
           logger.error("Error reading file", e);
        }
        return List.of();
    }

    public IncomingEvent parseEventFromLine(String line) {
        var matcher = pattern.matcher(line);
        var matchId = extractWithRegex(matcher,String.class).replace("'","");
        var marketId = extractWithRegex(matcher,Integer.class);
        var outcomeId = extractWithRegex(matcher,String.class).replace("'","");

        var specifiers = extractWithRegex(matcher,String.class);
        if(specifiers != null){
            specifiers = specifiers.replace("'","");
        }
        return new IncomingEvent(matchId, marketId, outcomeId, specifiers, atomicLong.getAndIncrement(), 0);
    }

    public <T> T extractWithRegex(Matcher matcher,Class<T> clazz) {
        if (matcher.find()) {
            try {
                if (clazz == String.class) {
                    return clazz.cast(matcher.group());
                } else if (clazz == Integer.class || clazz == int.class) {
                    return clazz.cast(Integer.parseInt(matcher.group()));
                }
            } catch (NullPointerException e) {
                if (clazz == Integer.class || clazz == int.class) {
                    return clazz.cast(-1);
                }
            }
        }
        return null;
    }
}
