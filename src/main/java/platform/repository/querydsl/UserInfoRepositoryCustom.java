package platform.repository.querydsl;

public interface UserInfoRepositoryCustom {

    void updatePostingCount(String userId, boolean updateCount);

    void updateFollowerCount(String userId, boolean updateCount);

    void updateFollowingCount(String userId, boolean updateCount);
}
