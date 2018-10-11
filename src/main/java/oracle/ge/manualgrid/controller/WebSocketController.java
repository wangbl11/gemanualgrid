package oracle.ge.manualgrid.controller;

import com.alibaba.fastjson.JSONObject;
import oracle.ge.manualgrid.mongo.model.Connection;
import oracle.ge.manualgrid.mongo.repository.ConnectionRepository;
import oracle.ge.manualgrid.websocket.model.ChatMessage;
import oracle.ge.manualgrid.websocket.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Author: tina.wang@oracle.com
 */

@RestController
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    @Autowired
    private WebSocketService connectionService;

    @Autowired
    private ConnectionRepository connectionRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        return chatMessage;
    }

    @MessageMapping("/sendMessage")
    public void castMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, @Header("side") String side, @Header("httpSessionId") String sessionId) {

        String topic="/topic/private/";
        Map<String,Object> headers=headerAccessor.getSessionAttributes();

        Connection connection=null;
        if (side.equals("1"))
        {
            topic=topic+sessionId;
            connectionService.sendTopicMessage(topic,chatMessage);
        }
        else
        {
            connection=connectionRepository.findByIdeId(sessionId);
            if (connection!=null)
            {
                topic=topic+connection.getConsoleId();
                connectionService.sendTopicMessage(topic,chatMessage);
            }
        }
    }

//    @MessageMapping("/console.connect")
//    public void consoleConnect(@Payload ChatMessage chatMessage,
//                               SimpMessageHeaderAccessor headerAccessor) {
//        System.out.println(headerAccessor.getSessionAttributes());
//        String httpSessionId=headerAccessor.getSessionAttributes().get("httpSessionId").toString();
//        System.out.println("WebSocketController ==> console.connect => "+httpSessionId);
//        String smSessionId=headerAccessor.getSessionId();
//        Map<String,Object> headers=headerAccessor.getSessionAttributes();
//        Connection existing=connectionRepository.findByConsoleIdAndCHttpSessId(smSessionId,httpSessionId);
//
//        if (existing==null)
//        {
//
//            Connection bean=new Connection(smSessionId,null,httpSessionId);
//            connectionRepository.save(bean);
//            headers.put("username", chatMessage.getSender());
//            headers.put("side", 1);
//            chatMessage.setStatus(true);
//
//            connectionService.sendTopicMessage("/topic/private/"+httpSessionId,chatMessage);
//        }
//        else{
//            chatMessage.setContent("You have already logined in.");
//            chatMessage.setStatus(false);
//            connectionService.sendTopicMessage("/topic/private/"+smSessionId,chatMessage);
//        }
//
//    }

    @MessageMapping("/console.connect")
    @SendToUser("/queue/reply")
    public String consoleConnect(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        //System.out.println(headerAccessor.getSessionAttributes());
        String httpSessionId=headerAccessor.getSessionAttributes().get("httpSessionId").toString();
        //System.out.println("WebSocketController ==> console.connect => "+httpSessionId);
        String smSessionId=headerAccessor.getSessionId();
        Map<String,Object> headers=headerAccessor.getSessionAttributes();
        Connection existing=connectionRepository.findByConsoleIdAndCHttpSessId(smSessionId,httpSessionId);

        if (existing==null)
        {

            Connection bean=new Connection(smSessionId,null,httpSessionId);
            connectionRepository.save(bean);
            headers.put("username", chatMessage.getSender());
            headers.put("side", 1);
            chatMessage.setStatus(true);
        }
        else{
            chatMessage.setContent("You have already logined in.");
            chatMessage.setStatus(false);
        }
        return httpSessionId;
    }

    @MessageMapping("/ide.connect")
    @SendToUser("/queue/reply")
    public void ideConnect(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        Map<String,Object> headers=headerAccessor.getSessionAttributes();
        String smSessionId=headerAccessor.getSessionId();

        JSONObject _message=JSONObject.parseObject(chatMessage.getContent());
        String console_id=_message.getString("chatRoomId");
        System.out.println(console_id);
        Connection bean=connectionRepository.findByCHttpSessId(console_id);
        if (bean!=null){
            bean.setIdeId(smSessionId);
            connectionRepository.save(bean);
            headers.put("username", chatMessage.getSender());
            headers.put("side", 2);
            chatMessage.setStatus(true);
            connectionService.sendTopicMessage("/topic/private/"+console_id,chatMessage);
        }
        else{
            chatMessage.setStatus(false);
            chatMessage.setContent("cannot connect to console.");
            connectionService.sendTopicMessage("/topic/private/"+console_id,chatMessage);
        }
    }

//    @Scheduled(cron = "0/10 * * * * ?")
//    public void sendToClient(Long userId) {
//        Set<String> sessionList = redisService.setMembers(RedisConstant.WS_USER_TO_SESSION_PRE + userId);
//        sessionList.forEach(sessionId -> {
//            logger.info("sessionId : {}", sessionId);
//
//        });
//    }

}
