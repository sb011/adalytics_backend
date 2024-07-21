package com.adalytics.adalytics_backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Component
public class NotificationHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Store the session
        sessions.put(ContextUtil.getCurrentUserId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove the session
        sessions.remove(ContextUtil.getCurrentUserId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
    }

    public void sendPusherNotification(String eventType, String message) {
        if(isBlank(eventType) || isBlank(message)) {
            return;
        }
        // Assume the payload contains the type of notification and the message
        try {
            WebSocketSession webSocketSession = sessions.get(ContextUtil.getCurrentUserId());
            if (webSocketSession != null && webSocketSession.isOpen()) {
                Map<String, String> notification = Map.of(
                        "type", eventType,
                        "message", message
                );
                String jsonNotification = objectMapper.writeValueAsString(notification);
                webSocketSession.sendMessage(new TextMessage(jsonNotification));
            }
        } catch (Exception ex) {
            log.error("Error sending pusher notification : ", ex);
        }
    }
}