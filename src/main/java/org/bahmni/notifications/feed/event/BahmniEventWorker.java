package org.bahmni.notifications.feed.event;

import org.bahmni.notifications.feed.transaction.HibernateUtil;
import org.hibernate.Session;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

public abstract class BahmniEventWorker implements EventWorker {
    @Override
    public void cleanUp(Event event) {
       Session session = HibernateUtil.getSession();
        session.clear();
    }

}
