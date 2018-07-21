package org.bahmni.notifications.feed.job;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.bahmni.notifications.feed.client.AtomFeedClientFactory;
import org.bahmni.notifications.feed.common.AtomFeedProperties;
import org.bahmni.webclients.Authenticator;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.client.service.FeedClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class BahmniFeedReaderJob implements Job {

    protected static Map<Class, FeedClient> atomFeedClients = new HashMap<>();
    private final Logger logger = Logger.getLogger(this.getClass());

    protected abstract EventWorker createWorker(HttpClient authenticatedWebClient, String urlPrefix);

    protected abstract Authenticator getAuthenticator(ConnectionDetails connectionDetails);

    protected abstract String getFeedName();

    protected abstract ConnectionDetails getConnectionDetails();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Job Started");
        try {
            processEvents();
        } catch (Exception e) {
            try {
                if (isUnauthorised(e)) {
                    initializeAtomFeedClient();
                }
            } finally {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected void processEvents() {
        if (atomFeedClients.get(this.getClass()) == null)
            initializeAtomFeedClient();
        FeedClient atomFeedClient = atomFeedClients.get(this.getClass());
        atomFeedClient.processEvents();
    }

    protected void initializeAtomFeedClient() {
        FeedClient atomFeedClient = createAtomFeedClient(AtomFeedProperties.getInstance(), new AtomFeedClientFactory());

        if (atomFeedClient != null) {
            atomFeedClients.put(this.getClass(), atomFeedClient);
        }
    }

    private FeedClient createAtomFeedClient(AtomFeedProperties atomFeedProperties, AtomFeedClientFactory atomFeedClientFactory) {
        ConnectionDetails connectionDetails = getConnectionDetails();

        String authUri = connectionDetails.getAuthUrl();
        String urlString = getURLPrefix(authUri);

        HttpClient authenticatedWebClient = new HttpClient(connectionDetails, getAuthenticator(connectionDetails));

        ClientCookies cookies = getCookies(authenticatedWebClient, authUri);
        return atomFeedClientFactory.getFeedClient(atomFeedProperties,
                getFeedName(), createWorker(authenticatedWebClient, urlString), cookies);
    }

    private ClientCookies getCookies(HttpClient authenticatedWebClient, String urlString) {
        try {
            return authenticatedWebClient.getCookies(new URI(urlString));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Is not a valid URI - " + urlString);
        }
    }

    //  TODO : should not depend on auth url to determine the prefix
    private static String getURLPrefix(String authenticationURI) {
        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }

    private boolean isUnauthorised(Exception e) {
        return ExceptionUtils.getStackTrace(e).contains("HTTP response code: 401")
                || ExceptionUtils.getStackTrace(e).contains("HTTP response code: 403");
    }
}
