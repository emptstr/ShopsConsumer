package shpe.consumer.controller;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shpe.consumer.accessor.EventApiAccessor;
import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.StubHubEvent;
import shpe.util.Sleeper;
import shpe.util.SleeperImpl;
import shpe.util.Timer;
import shpe.util.TimerImpl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@RequiredArgsConstructor
public class EventUpdateControllerImpl extends EventUpdateController {

    private static final Logger logger = LoggerFactory.getLogger(EventUpdateControllerImpl.class);
    private static final int SECONDS_IN_MINUTE = 60;

    private final EventApiAccessor eventRetriever;
    private final Timer timeoutTimer;
    private final Timer requestTimer;
    private final Sleeper threadSleeper;
    private final int maxEventsPerRequest;
    private final int maxRequestsPerMinute;
    private final int timeoutDurationInSeconds;
    /// metrics below
    private final Histogram timeSpentSleepingMetric, requestFailureRatioMetric;
    /**
     * Production Constructor
     * @param eventRetriever
     * @param maxEventsPerRequest
     * @param maxRequestsPerMinute
     * @param timeoutDurationInSeconds
     */
    public EventUpdateControllerImpl(EventApiAccessor eventRetriever, int maxEventsPerRequest, int maxRequestsPerMinute, int timeoutDurationInSeconds, MetricRegistry metricRegistry){
        this.eventRetriever = eventRetriever;
        this.maxEventsPerRequest = maxEventsPerRequest;
        this.maxRequestsPerMinute = maxRequestsPerMinute;
        this.timeoutDurationInSeconds = timeoutDurationInSeconds;
        this.timeoutTimer = new TimerImpl();
        this.requestTimer = new TimerImpl();
        this.threadSleeper = new SleeperImpl();
        this.timeSpentSleepingMetric = metricRegistry.histogram("EVENT_API_TIME_SPENT_SLEEPING");
        this.requestFailureRatioMetric = metricRegistry.histogram("EVENT_API_REQUEST_FAILURE_RATIO");
    }

    public Collection<StubHubEvent> update(StubHubApiToken accessToken) {
            int requestsInMinute = 0;
            int eventsInLastRequest = Integer.MAX_VALUE;
            int requestRowStart = 0;

            requestTimer.start();
            timeoutTimer.start();
            Collection<StubHubEvent> modifiedEvents = new LinkedList<>();

            do {
                if (isMinuteElapsed()) {
                    if (isMaxRequestsPerMinuteReached(requestsInMinute)) {
                        long sleepTime = getSleepTime();
                        timeSpentSleepingMetric.update(sleepTime);
                        threadSleeper.sleep(sleepTime);
                        requestTimer.reset();
                        requestsInMinute = 0;
                    }
                } else {
                    requestTimer.reset();
                    requestsInMinute = 0;
                }
                retrieveEvents(accessToken, requestRowStart);
                Optional<Collection<StubHubEvent>> requestResult = retrieveEvents(accessToken, requestRowStart);
                if(requestResult.isPresent()) {
                    Collection<StubHubEvent> retrievedEvents = requestResult.get();
                    modifiedEvents.addAll(retrievedEvents);
                    eventsInLastRequest = retrievedEvents.size();
                }
                requestRowStart += maxEventsPerRequest;
                requestsInMinute++;

            }while(isMoreEventsAvailable(eventsInLastRequest) && isTimeOutDurationReached());

            return  modifiedEvents;
    }

    private Optional<Collection<StubHubEvent>> retrieveEvents(StubHubApiToken accessToken, int requestRowStart) {
        try {
          Optional<Collection<StubHubEvent>> events = Optional.of(eventRetriever.getEvents(accessToken
                  , requestRowStart, maxEventsPerRequest));
          requestFailureRatioMetric.update(0);
          return events;
        }catch(Exception e){
            logger.warn(String.format("Request failed at row :%d ",requestRowStart));
            requestFailureRatioMetric.update(1);
            // fail silently
        }
        return Optional.empty();
    }

    private boolean isTimeOutDurationReached() {
        return timeoutDurationInSeconds > timeoutTimer.getElapsedSecs();
    }

    private boolean isMoreEventsAvailable(int eventsInLastRequest) {
        return eventsInLastRequest >= maxEventsPerRequest;
    }

    private boolean isMaxRequestsPerMinuteReached(int requestsInMinute) {
        return requestsInMinute >= maxRequestsPerMinute;
    }

    private long getSleepTime() {
        return (SECONDS_IN_MINUTE - requestTimer.getElapsedSecs() + 1) * 1000;
    }

    private boolean isMinuteElapsed() {
        return requestTimer.getElapsedSecs() < SECONDS_IN_MINUTE;
    }
}
