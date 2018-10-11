package oracle.ge.manualgrid.websocket.service;

import oracle.ge.manualgrid.websocket.model.ChatMessage;
import oracle.ge.manualgrid.websocket.model.OutMessage;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WebSocketService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void sendTopicMessage(String dest, ChatMessage message){
        simpMessagingTemplate.convertAndSend(dest,message);
    }

    public void SendToUser(String sessionId){
        simpMessagingTemplate.convertAndSendToUser(sessionId,"/getMsg",
                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"),
                createHeaders(sessionId));
    }

    /**
     * Setting header
     *
     * @param sessionId
     * @return
     */
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    public void sendServerInfo() {
        int processors=Runtime.getRuntime().availableProcessors();
        Long freeMem=Runtime.getRuntime().freeMemory();
        Long maxMem=Runtime.getRuntime().maxMemory();
        String content=String.format("Server Info: [processor: %d, free memory: %s, max memory: %s",processors,freeMem,maxMem);

        simpMessagingTemplate.convertAndSend("/topic/serverInfo",new OutMessage(content));
    }
}
