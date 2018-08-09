package org.bahmni.notifications.feed.service.impl;

import org.bahmni.notifications.feed.NotificationsManager;

public class BahmniVisitService {

    public BahmniVisitService() {
    }

    public void createOrUpdate(String visit) {
        NotificationsManager.getInstance().add(visit);
    }
}
