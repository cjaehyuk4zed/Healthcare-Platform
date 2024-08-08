package platform.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.domain.Posting_Info;
import platform.domain.User_Interest;
import platform.dto.postdto.PostInfoResponseDTO;
import platform.dto.postdto.PostingSearchDTO;

import java.util.List;
import java.util.UUID;

public interface PostInfoRepositoryCustom {
    List<PostInfoResponseDTO> findIdsByCategory(String category, String subcategory);

    Page<Posting_Info> findBySearchQueryPaged(PostingSearchDTO postingSearchDTO, Pageable pageable);

    List<Posting_Info> findBySearchQueryList(PostingSearchDTO postingSearchDTO, Pageable pageable);

    Page<Posting_Info> findBySort(Pageable pageable);

    Page<Posting_Info> findBySortAndInterest(Pageable pageable, List<User_Interest> userInterests);

    void updateLikes(UUID postingId, boolean userLiked);

    void updateFaves(UUID postingId, boolean userFaved);

    void updateThumbnail(String userId, UUID postingId, String thumbnail);
}
