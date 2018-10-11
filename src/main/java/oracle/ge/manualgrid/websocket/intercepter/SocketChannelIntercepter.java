package oracle.ge.manualgrid.websocket.intercepter;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import static java.util.Objects.isNull;

public class SocketChannelIntercepter extends ChannelInterceptorAdapter {

    @Override
    public boolean preReceive(MessageChannel channel) {
        return super.preReceive(channel);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        final StompCommand command = headerAccessor.getCommand();
        if (!isNull(command)) {
            System.out.println("SocketChannelIntercepter ~~~");
            System.out.println(headerAccessor.getSessionAttributes());
            switch (command) {
                case CONNECT:
                    headerAccessor.addNativeHeader("CUSTOM01", "CUSTOM01");
                    headerAccessor.getSessionAttributes().put("ddd","ddd");
                    headerAccessor.setHeader("tina","eee");
                    if (headerAccessor.getSessionAttributes().containsKey("httpSessionId"))
                        headerAccessor.setSessionId(headerAccessor.getSessionAttributes().get("httpSessionId").toString());
                default:
                    break;
            }
        }
        return message;
    }


    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        super.afterSendCompletion(message, channel, sent, ex);
    }
}
