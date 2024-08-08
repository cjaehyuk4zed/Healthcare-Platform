package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Tokens;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokensRepository extends JpaRepository<Tokens, String> {

    // TOKEN 검증 코드를 필터에 추가해야 된다
    // TOKEN 검증 코드를 필터에 추가해야 된다
    // TOKEN 검증 코드를 필터에 추가해야 된다
    List<Tokens> findAllByUserAuth_UsernameAndClientIp(String userId, String clientIp);

    Optional<Tokens> findByToken(String token);
}
