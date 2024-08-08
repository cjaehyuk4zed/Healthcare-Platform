package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Tab;
import platform.domain.keys.UserTabCompositeKey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTabRepository extends JpaRepository<User_Tab, UserTabCompositeKey> {

//    Optional<User_Tab> findByTabId(UUID tabId);

    List<User_Tab> findAllByUserTabCompositeKeyUserIdOrderByTabIdAsc(String userId);

    Optional<User_Tab> findByUserTabCompositeKeyUserIdAndTabId(String userId, UUID tabId);

}
