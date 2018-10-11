package oracle.ge.manualgrid.websocket.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: tina.wang@oracle.com
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String to;
    private boolean status;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

}
