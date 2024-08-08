package platform.repository;

import platform.domain.User_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<User_Info, String> {

//    Optional<User_Info> findByUserId(String userId);
    void updatePostingCount(String userId, boolean addCount);

    void updateFollowerCount(String userId, boolean updateCount);

    void updateFollowingCount(String userId, boolean updateCount);
}
