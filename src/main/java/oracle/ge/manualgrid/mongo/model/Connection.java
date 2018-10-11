package oracle.ge.manualgrid.mongo.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

;

/**
 * Author: tina.wang@oracle.com*/

@Data
@Getter
@Setter
@Document
public class Connection {
    @Id
    private String id;

    // console sm ID
    private String consoleId;
    // console http session id
    private String cHttpSessId;

    // IDE sm ID
    private String ideId;

    // websocket url
    private String websocketUrl;

    /*
     * 00=0: both offline
     * 01=1: ide online
     * 10=2: console online
     * 11=3: both online
     */
    private Integer status;

    private long lastActiveTime;

    public Connection(String console_id, String ide_id,String cHttpSessId) {
        this.consoleId = console_id;
        this.ideId = ide_id;
    }
}
