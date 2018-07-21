package org.bahmni.notifications.feed.service.impl;

import org.bahmni.notifications.feed.contract.openmrs.OpenMRSPatient;

public class BahmniPatientService {

    public BahmniPatientService() {

    }

    public void createOrUpdate(OpenMRSPatient openMRSPatient) {
        System.out.println("#######################################################");
        System.out.println(openMRSPatient);
    }
}
