package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Fav;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFavRepository extends JpaRepository<User_Fav, Long> {

    Optional<User_Fav> findByUserIdAndPostingId(String userId, UUID postingId);

}
