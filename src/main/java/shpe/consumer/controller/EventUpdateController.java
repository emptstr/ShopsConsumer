package shpe.consumer.controller;

import shpe.consumer.model.StubHubApiToken;
import shpe.consumer.model.StubHubEvent;

import java.util.Collection;

/**
 * Created by jordan on 6/22/17.
 */
public abstract class EventUpdateController {

    public abstract Collection<StubHubEvent> update(StubHubApiToken accessToken);
}
