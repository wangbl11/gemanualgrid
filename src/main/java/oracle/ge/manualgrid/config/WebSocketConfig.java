package oracle.ge.manualgrid.config;

import oracle.ge.manualgrid.websocket.intercepter.HttpHandShakeIntercepter;
import oracle.ge.manualgrid.websocket.intercepter.SocketChannelIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Author: tina.wang@oracle.com
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ge").addInterceptors(new HttpHandShakeIntercepter()).setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/oraclege");

        // one to one, default is /user/
        //registry.setUserDestinationPrefix("/showcase/ws");

        registry.enableSimpleBroker("/ge/topic","/queue/", "/topic/");   // Enables a simple in-memory broker
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelIntercepter());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelIntercepter());

    }
}
