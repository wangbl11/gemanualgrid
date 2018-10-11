package oracle.ge.manualgrid.websocket.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
public class OutMessage {
    private String content;
    private String sender;
    private String to;

    public OutMessage(String content){
        this.content=content;
    }

}
