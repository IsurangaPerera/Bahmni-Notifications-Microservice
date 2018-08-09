package org.bahmni.notifications.feed.job.openmrs;

import org.bahmni.notifications.feed.event.VisitFeedEventWorker;
import org.bahmni.notifications.feed.job.FeedNames;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class OpenMRSVisitFeedReaderJob extends OpenMRSFeedReaderJob {

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix) {
        return new VisitFeedEventWorker(authenticatedWebClient, urlPrefix);
    }

    @Override
    protected String getFeedName() {
        return FeedNames.OPENMRS_VISIT_FEED_NAME;
    }
}
