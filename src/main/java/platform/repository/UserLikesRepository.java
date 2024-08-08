package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Like;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserLikesRepository extends JpaRepository<User_Like, Long> {

    Optional<User_Like> findByUserIdAndPostingId(String userId, UUID postingId);

    List<User_Like> findByUserIdAndPostingLiked(String userId, boolean postingLiked);
}
