package oracle.ge.manualgrid.websocket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    @Override
    public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());

        //what we put in handShakeIntercepter can be gotten here.
        System.out.println("Http session id:"+ headerAccessor.getSessionAttributes().get("httpSessionId"));
    }
}
