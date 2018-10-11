package oracle.ge.manualgrid.websocket.listener;

import oracle.ge.manualgrid.mongo.model.Connection;
import oracle.ge.manualgrid.mongo.repository.ConnectionRepository;
import oracle.ge.manualgrid.websocket.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

/**
 * Author: tina.wang@oracle.com
 */

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ConnectionRepository connectionRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        Principal principal = SimpMessageHeaderAccessor.getUser(headers);
        if (principal!=null)
        {
            String username = principal.getName();
        }

        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String,Object> headers=headerAccessor.getSessionAttributes();
        Object side=headers.getOrDefault("side","1");
        Object httpSessionId=headers.getOrDefault("httpSessionId",event.getSessionId());

        if(side=="1") {//console side
            Connection connection=connectionRepository.findByCHttpSessId(httpSessionId.toString());
            if (connection==null) return;
            connection.setConsoleId(null);
            connection.setStatus(connection.getStatus()&01);
            connectionRepository.save(connection);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setContent("Console is offline.");
            chatMessage.setSender("SYSTEM");

            messagingTemplate.convertAndSend("/topic/private/"+httpSessionId, chatMessage);
        }
        else{
            Connection connection=connectionRepository.findByIdeId(event.getSessionId());
            if (connection==null) return;
            connection.setIdeId(null);
            connectionRepository.save(connection);
            connection.setStatus(connection.getStatus()&10);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setContent("Recorder is offline.");
            chatMessage.setSender("SYSTEM");
            messagingTemplate.convertAndSend("/topic/private/"+connection.getCHttpSessId(), chatMessage);
        }
    }
}
