package platform.repository.querydsl;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import static platform.domain.QUser_Info.user_Info;

@AllArgsConstructor
public class UserInfoRepositoryImpl implements UserInfoRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private final ModelMapper modelMapper;

    @Override
    public void updatePostingCount(String userId, boolean updateCount) {
        jpaQueryFactory
                .update(user_Info)
                .set(user_Info.postingCount, updateUserPostCount(updateCount))
                .where(user_Info.userId.eq(userId))
                .execute();
    }

    @Override
    public void updateFollowerCount(String userId, boolean updateCount) {
        jpaQueryFactory
                .update(user_Info)
                .set(user_Info.followerCount, updateUserFollowerCount(updateCount))
                .where(user_Info.userId.eq(userId))
                .execute();
    }

    @Override
    public void updateFollowingCount(String userId, boolean updateCount) {
        jpaQueryFactory
                .update(user_Info)
                .set(user_Info.followingCount, updateUserFollowingCount(updateCount))
                .where(user_Info.userId.eq(userId))
                .execute();
    }



    private NumberExpression<Long> updateUserPostCount(boolean updateCount){
        return updateCount ? user_Info.postingCount.add(1) : user_Info.postingCount.subtract(1);
    }

    private NumberExpression<Long> updateUserFollowerCount(boolean updateCount){
        return updateCount ? user_Info.followerCount.add(1) : user_Info.followerCount.subtract(1);
    }

    private NumberExpression<Long> updateUserFollowingCount(boolean updateCount){
        return updateCount ? user_Info.followingCount.add(1) : user_Info.followingCount.subtract(1);
    }


}
