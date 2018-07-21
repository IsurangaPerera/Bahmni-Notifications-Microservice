package org.bahmni.notifications.feed.job.openmrs;

import org.bahmni.notifications.feed.event.PatientFeedEventWorker;
import org.bahmni.notifications.feed.job.FeedNames;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class OpenMRSPatientFeedReaderJob extends OpenMRSFeedReaderJob {

    @Override
    protected EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix) {
        return new PatientFeedEventWorker(authenticatedWebClient, urlPrefix);
    }

    @Override
    protected String getFeedName() {
        return FeedNames.OPENMRS_PATIENT_FEED_NAME;
    }
}
