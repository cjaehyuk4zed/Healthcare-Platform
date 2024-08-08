package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Request_Log;

import java.util.Optional;

@Repository
public interface RequestLogRepository extends JpaRepository<Request_Log, Long> {

    Optional<Request_Log> findFirstByUserIdOrderByTimestampDesc(String userId);

}
