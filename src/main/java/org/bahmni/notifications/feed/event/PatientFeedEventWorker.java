package org.bahmni.notifications.feed.event;

import org.apache.log4j.Logger;
import org.bahmni.notifications.feed.common.AtomFeedProperties;
import org.bahmni.notifications.feed.contract.openmrs.OpenMRSPatient;
import org.bahmni.notifications.feed.mapper.OpenMRSPatientMapper;
import org.bahmni.notifications.feed.service.impl.BahmniPatientService;
import org.bahmni.notifications.feed.transaction.HibernateUtil;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.hibernate.Session;
import org.ict4h.atomfeed.client.domain.Event;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

public class PatientFeedEventWorker extends BahmniEventWorker {

    private HttpClient webClient;
    private String urlPrefix;
    private static Logger logger = Logger.getLogger(PatientFeedEventWorker.class);

    private static final String BAHMNI_BASE = "bahmni.baseUrl";

    public PatientFeedEventWorker(HttpClient webClient, String urlPrefix) {
        this.webClient = webClient;
        this.urlPrefix = urlPrefix;
    }

    @Override
    public void process(Event event) {
        try {
            String content = event.getContent();
           // String patientJSON = webClient.get(URI.create(urlPrefix + content));

            String patientJSON = webClient.get(URI.create(AtomFeedProperties.getInstance().getProperty(BAHMNI_BASE) +
                    new JSONObject(content).get("rest")));

            OpenMRSPatientMapper openMRSPatientMapper = new OpenMRSPatientMapper(ObjectMapperRepository.objectMapper);
            OpenMRSPatient openMRSPatient = openMRSPatientMapper.map(patientJSON);
            logInfo(openMRSPatient);
            BahmniPatientService bahmniPatientService = new BahmniPatientService();
            bahmniPatientService.createOrUpdate(openMRSPatient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Session session = HibernateUtil.getSession();
            session.clear();
        }
    }

    private void logInfo(OpenMRSPatient openMRSPatient) {
        if (openMRSPatient.getIdentifiers().size() != 0)
            logger.info(String.format("Processing patient with ID=%s", openMRSPatient.getIdentifiers().get(0).getIdentifier()));
    }
}
