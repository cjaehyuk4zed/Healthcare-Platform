package platform.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser_Follower is a Querydsl query type for User_Follower
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser_Follower extends EntityPathBase<User_Follower> {

    private static final long serialVersionUID = -1480261373L;

    public static final QUser_Follower user_Follower = new QUser_Follower("user_Follower");

    public final StringPath followerId = createString("followerId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath userId = createString("userId");

    public QUser_Follower(String variable) {
        super(User_Follower.class, forVariable(variable));
    }

    public QUser_Follower(Path<? extends User_Follower> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser_Follower(PathMetadata metadata) {
        super(User_Follower.class, metadata);
    }

}

