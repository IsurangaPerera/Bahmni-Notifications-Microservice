package org.bahmni.notifications.feed;

import org.apache.log4j.BasicConfigurator;
import org.bahmni.notifications.feed.job.BahmniFeedReaderJob;
import org.bahmni.notifications.feed.job.openmrs.OpenMRSVisitFeedReaderJob;
import org.bahmni.notifications.feed.socket.WebSocketHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.Map;

import static spark.Spark.init;
import static spark.Spark.webSocket;

public class ServiceInitializer {

    public static void main(String[] args) throws JobExecutionException{
        BasicConfigurator.configure();

        Thread t1 = new Thread(() -> {
            webSocket("/notifications", WebSocketHandler.class);
            init();
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            Map<Session,String> userMap = NotificationsManager.getInstance().getUserMap();
            while(true) {
                if (NotificationsManager.getInstance().getUserMapSize() > 0 &&
                        NotificationsManager.getInstance().size() > 0) {
                    for(Session session : userMap.keySet()) {
                        try {
                            session.getRemote().sendString((String) NotificationsManager.getInstance().poll());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t2.start();

        BahmniFeedReaderJob j = new OpenMRSVisitFeedReaderJob();
        j.execute(null);
    }
}
