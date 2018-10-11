package oracle.ge.manualgrid.mongo.repository;

import oracle.ge.manualgrid.mongo.model.Connection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepository extends MongoRepository<Connection, String> {
    Connection findByConsoleIdAndCHttpSessId(String consoleId, String cHttpSessId);
    Connection findByCHttpSessId(String cHttpSessId);
    Connection findByConsoleId(String consoleId);
    Connection findByIdeId(String ideId);
}
