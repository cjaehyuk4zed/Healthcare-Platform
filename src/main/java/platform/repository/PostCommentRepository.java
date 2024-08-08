package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Posting_Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostCommentRepository extends JpaRepository<Posting_Comment, Long> {

    Optional<Posting_Comment> findByUserIdAndId(String userId, Long id);

    List<Posting_Comment> findAllByPostingId(UUID postingId);

}
