package shpe.consumer.controller;

import shpe.consumer.model.StubHubEvent;
import shpe.consumer.model.StubHubApiToken;

import java.util.Collection;

public abstract class EventListingUpdateController {

    public abstract Collection<StubHubEvent> update(StubHubApiToken accessToken, Collection<StubHubEvent> activeEventIDList);
}
