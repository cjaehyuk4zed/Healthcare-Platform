package platform.repository.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import platform.domain.Posting_Info;
import platform.domain.User_Interest;
import platform.dto.postdto.PostInfoResponseDTO;
import platform.dto.postdto.PostingSearchDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.domain.QPosting_Info.posting_Info;

@Slf4j
@AllArgsConstructor
public class PostInfoRepositoryImpl implements PostInfoRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final ModelMapper modelMapper;

    @Override
    public List<PostInfoResponseDTO> findIdsByCategory(String category, String subcategory) {
        return jpaQueryFactory
                .selectFrom(posting_Info)
                .where(posting_Info.postingSaved.eq(true),
                        posting_Info.category.eq(category)
                        .or(posting_Info.subcategory.eq(subcategory)))
                .fetch()
                .stream()
                .map(post_Info -> modelMapper.map(post_Info, PostInfoResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Posting_Info> findBySearchQueryPaged(PostingSearchDTO postingSearchDTO, Pageable pageable) {

        Sort sort = pageable.getSort();
        OrderSpecifier<?> orderSpecifier = sortCondition(sort);

        List<Posting_Info> postInfos = jpaQueryFactory
                .selectFrom(posting_Info)
                .where(posting_Info.postingSaved.eq(true),
                        categoryEq(postingSearchDTO.getCategory()),
                        subcategoryEq(postingSearchDTO.getSubcategory()),
                        userIdEq(postingSearchDTO.getUserId()))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
// Separate COUNT query for efficiency
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(posting_Info.count())
                .from(posting_Info)
                .where(posting_Info.postingSaved.eq(true),
                        categoryEq(postingSearchDTO.getCategory()),
                        subcategoryEq(postingSearchDTO.getSubcategory()),
                        userIdEq(postingSearchDTO.getUserId()));

        return PageableExecutionUtils.getPage(postInfos, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public List<Posting_Info> findBySearchQueryList(PostingSearchDTO postingSearchDTO, Pageable pageable) {

        Sort sort = pageable.getSort();
        OrderSpecifier<?> orderSpecifier = sortCondition(sort);

        return jpaQueryFactory
                .selectFrom(posting_Info)
                .where(posting_Info.postingSaved.eq(true),
                        queryContains(postingSearchDTO.getQuery()),
                        categoryEq(postingSearchDTO.getCategory()),
                        subcategoryEq(postingSearchDTO.getSubcategory()),
                        userIdEq(postingSearchDTO.getUserId()))
                .orderBy(orderSpecifier)
                .fetch();
// Separate COUNT query for efficiency
// BUT COUNT query is not needed in this function, as it will change after parsing <span> tags
    }

    @Override
    public Page<Posting_Info> findBySort(Pageable pageable) {
        Sort sort = pageable.getSort();
        OrderSpecifier<?> orderSpecifier = sortCondition(sort);

        List<Posting_Info> postInfos = jpaQueryFactory
                .selectFrom(posting_Info)
                .where(posting_Info.postingSaved.eq(true))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
// Separate COUNT query for efficiency
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(posting_Info.count())
                .from(posting_Info)
                .where(posting_Info.postingSaved.eq(true));

        return PageableExecutionUtils.getPage(postInfos, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public Page<Posting_Info> findBySortAndInterest(Pageable pageable, List<User_Interest> userInterests) {
        Sort sort = pageable.getSort();
        OrderSpecifier<?> orderSpecifier = sortCondition(sort);

        List<Predicate> predicates = new ArrayList<>();
        for(User_Interest ui : userInterests){
            String category = ui.getUserInterestCompositeKey().getCategory();
            String subcategory = ui.getUserInterestCompositeKey().getSubcategory();
            Predicate p = posting_Info.category.eq(category).and(posting_Info.subcategory.eq(subcategory));
            predicates.add(p);
        }

        BooleanBuilder whereBuilder = new BooleanBuilder();
//        predicates.forEach(whereBuilder::or);  is the same as the below Enhanced For Loop
        for (Predicate predicate : predicates) {
            whereBuilder.or(predicate);
        }

        List<Posting_Info> postInfos = jpaQueryFactory
                .selectFrom(posting_Info)
                .where(posting_Info.postingSaved.eq(true),
                        whereBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
// Separate COUNT query for efficiency
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(posting_Info.count())
                .from(posting_Info)
                .where(posting_Info.postingSaved.eq(true),
                        whereBuilder);

        return PageableExecutionUtils.getPage(postInfos, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public void updateLikes(UUID postingId, boolean userLiked) {
        jpaQueryFactory
                .update(posting_Info)
                .set(posting_Info.likes, updatePostLikes(userLiked))
                .where(posting_Info.postingId.eq(postingId))
                .execute();
    }

    @Override
    public void updateFaves(UUID postingId, boolean userFaved) {
        jpaQueryFactory
                .update(posting_Info)
                .set(posting_Info.faves, updatePostFaves(userFaved))
                .where(posting_Info.postingId.eq(postingId))
                .execute();
    }

    @Override
    public void updateThumbnail(String userId, UUID postingId, String thumbnail) {
        jpaQueryFactory
                .update(posting_Info)
                .set(posting_Info.thumbnail, thumbnail)
                .where(posting_Info.userId.eq(userId),
                        (posting_Info.postingId.eq(postingId)))
                .execute();
    }

    private BooleanExpression categoryEq(String category){
        return category==null || category.isEmpty() ? null : posting_Info.category.eq(category);
    }

    private BooleanExpression subcategoryEq(String subcategory){
        return subcategory==null || subcategory.isEmpty() ? null : posting_Info.subcategory.eq(subcategory);
    }

    private BooleanExpression queryContains(String titleAndContent){
        return titleAndContent==null || titleAndContent.isEmpty() ? null : posting_Info.title.containsIgnoreCase(titleAndContent)
                .or(posting_Info.postingContent.containsIgnoreCase(titleAndContent))
                .or(posting_Info.subtitle.containsIgnoreCase(titleAndContent));
    }

    private BooleanExpression subtitleContains(String subtitle){
        return subtitle==null || subtitle.isEmpty() ? null : posting_Info.subtitle.containsIgnoreCase(subtitle);
    }

    private BooleanExpression userIdEq(String userId){
        return userId==null || userId.isEmpty() ? null : posting_Info.userId.eq(userId);
    }

    private NumberExpression<Long> updatePostLikes(boolean userLiked){
        return userLiked ? posting_Info.likes.add(1) : posting_Info.likes.subtract(1);
    }

    private NumberExpression<Long> updatePostFaves(boolean userFaved){
        return userFaved ? posting_Info.faves.add(1) : posting_Info.faves.subtract(1);
    }

    private OrderSpecifier<?> sortCondition(Sort sort){
        String searchCondition = sort.toString().split(":")[0];
        boolean isAscending = sort.getOrderFor(searchCondition).isAscending();

        log.info("PostInfoRepositoryImpl sortCondition sort : {}", sort);
        log.info("PostInfoRepositoryImpl sortCondition isAscending : {}", isAscending);

        if(isAscending){
            return switch (searchCondition) {
                case "recent" -> posting_Info.timestamp.asc();
                case "views" -> posting_Info.views.asc();
                case "likes" -> posting_Info.likes.asc();
                default -> posting_Info.timestamp.asc();
            };
        } else{
            return switch (searchCondition) {
                case "recent" -> posting_Info.timestamp.desc();
                case "views" -> posting_Info.views.desc();
                case "likes" -> posting_Info.likes.desc();
                default -> posting_Info.timestamp.desc();
            };
        }
    }
}
