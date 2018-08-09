package org.bahmni.notifications.feed;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.eclipse.jetty.websocket.api.Session;

public class NotificationsManager<T> extends PriorityBlockingQueue<T> {

    private static NotificationsManager queue = null;

    private static Map<Session, String> userMap = new ConcurrentHashMap<>();
    private static int nextUserNumber = 1;

    private NotificationsManager(){
    }

    public static NotificationsManager getInstance() {
        queue = (queue == null)? new NotificationsManager<String>() : queue;
        return queue;
    }

    @Override
    public boolean add(T e) {
        return super.add(e);
    }

    public void addSession(Session session) {
        userMap.put(session, Integer.toString(nextUserNumber));
        ++nextUserNumber;
    }

    public int getUserMapSize() {
        return userMap.size();
    }

    public Map<Session, String> getUserMap() {
        return userMap;
    }
}
