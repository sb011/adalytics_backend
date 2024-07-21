package com.adalytics.adalytics_backend.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JWTUtil jwtUtil;

    private String getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    DecodedJWT validatedToken = jwtUtil.validateToken(keyValue[1]);
                    System.out.println("Validated token: " + validatedToken.getSubject());
                    return validatedToken.getSubject();
                }
            }
        }
        return null;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.put(userId, session);
        } else {
            log.warn("User ID not found in WebSocket session");
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remove the session
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.remove(userId);
        } else {
            log.warn("User ID not found in WebSocket session");
        }
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
            System.out.println(webSocketSession);
            if (webSocketSession != null && webSocketSession.isOpen()) {
                Map<String, String> notification = Map.of(
                        "type", eventType,
                        "message", message
                );
                String jsonNotification = objectMapper.writeValueAsString(notification);
                webSocketSession.sendMessage(new TextMessage(jsonNotification));
                System.out.println("Notification sent");
            }
        } catch (Exception ex) {
            log.error("Error sending pusher notification : ", ex);
        }
    }
}