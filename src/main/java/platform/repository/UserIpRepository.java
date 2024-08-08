package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Ip;

import java.util.Optional;

@Repository
public interface UserIpRepository extends JpaRepository<User_Ip, String> {
    Optional<User_Ip> findByUserIpCompositeKey_UserIdAndUserIpCompositeKey_IpAddr(String userId, String ipAddr);
}
