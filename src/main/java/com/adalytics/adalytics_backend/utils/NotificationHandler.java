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
import static io.micrometer.common.util.StringUtils.isNotBlank;

@Slf4j
@Component
public class NotificationHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (isNotBlank(userId)) {
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
        if (isNotBlank(userId)) {
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
            }
        } catch (Exception ex) {
            log.error("Error sending pusher notification : ", ex);
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        String query = CommonUtil.resolve(() -> session.getUri().getQuery(), null);
        if (isNotBlank(query)) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    DecodedJWT validatedToken = jwtUtil.validateToken(keyValue[1]);
                    return validatedToken.getSubject();
                }
            }
        }
        return null;
    }
}