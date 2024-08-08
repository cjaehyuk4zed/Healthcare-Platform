package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Follower;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowerRepository extends JpaRepository<User_Follower, Long> {

    Optional<User_Follower> findByUserIdAndFollowerId(String userId, String followerId);

    List<User_Follower> findByUserId(String userId);

    List<User_Follower> findByFollowerId(String followerId);
}
