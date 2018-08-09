package org.bahmni.notifications.feed.socket;

import org.bahmni.notifications.feed.NotificationsManager;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WebSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        NotificationsManager.getInstance().addSession(session);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {

    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {

    }
}
