package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Interest;
import platform.domain.keys.UserInterestCompositeKey;

import java.util.List;

@Repository
public interface UserInterestRepository extends JpaRepository<User_Interest, UserInterestCompositeKey> {

    List<User_Interest> findAllByUserInterestCompositeKey_UserIdAndUserInterested(String userId, boolean userInterested);


}
