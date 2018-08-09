package org.bahmni.notifications.feed.event;

import org.bahmni.notifications.feed.common.AtomFeedProperties;
import org.bahmni.notifications.feed.service.impl.BahmniVisitService;
import org.bahmni.notifications.feed.transaction.HibernateUtil;
import org.bahmni.webclients.HttpClient;
import org.hibernate.Session;
import org.ict4h.atomfeed.client.domain.Event;
import org.json.JSONObject;

import java.net.URI;

public class VisitFeedEventWorker  extends BahmniEventWorker  {

    private HttpClient webClient;

    private static final String BAHMNI_BASE = "bahmni.baseUrl";

    public VisitFeedEventWorker(HttpClient webClient, String urlPrefix) {
        this.webClient = webClient;
        String urlPrefix1 = urlPrefix;
    }

    @Override
    public void process(Event event) {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println(event);
        try {
            String content = event.getContent();

            String patientJSON = webClient.get(URI.create(AtomFeedProperties.getInstance().getProperty(BAHMNI_BASE) +
                    new JSONObject(content).get("rest")));
            System.out.println(patientJSON);

            BahmniVisitService bahmniVisitService = new BahmniVisitService();
            bahmniVisitService.createOrUpdate(patientJSON);
        } finally {
            Session session = HibernateUtil.getSession();
            session.clear();
        }
    }
}
