package org.bahmni.notifications.feed.client;

import org.bahmni.notifications.feed.common.AtomFeedProperties;
import org.bahmni.notifications.feed.transaction.AtomFeedHibernateTransactionManager;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.bahmni.webclients.ClientCookies;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;

import java.net.URI;
import java.net.URISyntaxException;

public class AtomFeedClientFactory {

    public FeedClient getFeedClient(AtomFeedProperties atomFeedProperties, String feedName,
                                    EventWorker eventWorker, ClientCookies cookies) {
        String uri = atomFeedProperties.getProperty(feedName);
        try {

            AtomFeedHibernateTransactionManager transactionManager = new AtomFeedHibernateTransactionManager();
            org.ict4h.atomfeed.client.AtomFeedProperties atomFeedClientProperties = createAtomFeedClientProperties(atomFeedProperties);

            AllFeeds allFeeds = new AllFeeds(atomFeedClientProperties, cookies);
            AllMarkersJdbcImpl allMarkers = new AllMarkersJdbcImpl(transactionManager);
            AllFailedEventsJdbcImpl allFailedEvents = new AllFailedEventsJdbcImpl(transactionManager);

            return new AtomFeedClient(allFeeds, allMarkers, allFailedEvents,
                    atomFeedClientProperties, transactionManager, new URI(uri), eventWorker);

        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Is not a valid URI - %s", uri));
        }
    }

    private org.ict4h.atomfeed.client.AtomFeedProperties createAtomFeedClientProperties(AtomFeedProperties atomFeedProperties) {
        org.ict4h.atomfeed.client.AtomFeedProperties feedProperties = new org.ict4h.atomfeed.client.AtomFeedProperties();
        feedProperties.setConnectTimeout(Integer.parseInt(atomFeedProperties.getFeedConnectionTimeout()));
        feedProperties.setReadTimeout(Integer.parseInt(atomFeedProperties.getFeedReplyTimeout()));
        feedProperties.setMaxFailedEvents(Integer.parseInt(atomFeedProperties.getMaxFailedEvents()));
        feedProperties.setFailedEventMaxRetry(Integer.parseInt(atomFeedProperties.getFailedEventMaxRetry()));
        feedProperties.setControlsEventProcessing(true);
        return feedProperties;
    }

}
