package oracle.ge.manualgrid.websocket.intercepter;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class HttpHandShakeIntercepter implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {

        if (serverHttpRequest instanceof ServletServerHttpRequest){
            ServletServerHttpRequest request=(ServletServerHttpRequest)serverHttpRequest;
            HttpSession session=(HttpSession)request.getServletRequest().getSession();
            String sessionId=session.getId();

            //put http session id in headerAccessor.getSessionAttributes
            map.put("httpSessionId",sessionId);
            map.put("sessionId",sessionId);
            //System.out.println("HttpHandShakeIntercepter => beforeHandshake: "+sessionId);
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        /*
        if (serverHttpRequest instanceof ServletServerHttpRequest){
            ServletServerHttpRequest request=(ServletServerHttpRequest)serverHttpRequest;
            HttpSession session=(HttpSession)request.getServletRequest().getSession();
            String sessionId=session.getId();
            System.out.println("HttpHandShakeIntercepter => afterHandshake: "+sessionId);
        }
       */
    }
}
