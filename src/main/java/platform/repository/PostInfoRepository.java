package platform.repository;

import org.springframework.data.jpa.repository.Query;
import platform.domain.Posting_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.repository.querydsl.PostInfoRepositoryCustom;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostInfoRepository extends JpaRepository<Posting_Info, UUID>, PostInfoRepositoryCustom {

    List<String> findAllByTimestampBetween(LocalDate start, LocalDate finish);

    List<Posting_Info> findAllByUserId(String userId);

    List<Posting_Info> findAllByUserIdAndPostingSaved(String userId, boolean postingSaved);

    List<Posting_Info> findAllByUserIdAndCategoryAndSubcategory(String userId, String category, String subcategory);

    Optional<Posting_Info> findByPostingId(UUID postingId);

    Optional<Posting_Info> findByPostingIdAndUserId(UUID postingId, String userId);

    List<Posting_Info> findAllByPostingSaved(boolean postingSaved);

    @Query("SELECT p.userId FROM Posting_Info p WHERE p.postingId = :postingId")
    Optional<String> findUserIdByPostingId(UUID postingId);

    int countByUserIdAndPostingId(String userId, UUID postingId);

    int countByPostingId(UUID postingId);
}
