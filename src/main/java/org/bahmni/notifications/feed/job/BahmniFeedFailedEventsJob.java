package org.bahmni.notifications.feed.job;

import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class BahmniFeedFailedEventsJob extends BahmniFeedReaderJob {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        processFailedEvents();
    }

    private void processFailedEvents() {
        if (atomFeedClients.get(this.getClass()) == null)
            initializeAtomFeedClient();
        FeedClient atomFeedClient = atomFeedClients.get(this.getClass());
        atomFeedClient.processFailedEvents();
    }

}
