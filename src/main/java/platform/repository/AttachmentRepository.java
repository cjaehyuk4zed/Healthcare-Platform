package platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import platform.domain.Posting_Attachment;
import platform.domain.keys.PostingAttachmentCompositeKey;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Posting_Attachment, PostingAttachmentCompositeKey> {

    List<Posting_Attachment> findAllByPostingAttachmentCompositeKey_UserIdAndPostingId(String userId, UUID postingId);

    List<Posting_Attachment> findAllByPostingId(UUID postingId);

    void deleteAlLByPostingId(UUID postingId);
}
