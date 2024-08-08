package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User_Auth;

@Repository
public interface UserAuthRepository extends JpaRepository<User_Auth, String> {
}
