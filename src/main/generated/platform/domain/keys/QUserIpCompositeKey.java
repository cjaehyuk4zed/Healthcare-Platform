package platform.domain.keys;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserIpCompositeKey is a Querydsl query type for UserIpCompositeKey
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserIpCompositeKey extends BeanPath<UserIpCompositeKey> {

    private static final long serialVersionUID = -211257899L;

    public static final QUserIpCompositeKey userIpCompositeKey = new QUserIpCompositeKey("userIpCompositeKey");

    public final StringPath ipAddr = createString("ipAddr");

    public final StringPath userId = createString("userId");

    public QUserIpCompositeKey(String variable) {
        super(UserIpCompositeKey.class, forVariable(variable));
    }

    public QUserIpCompositeKey(Path<? extends UserIpCompositeKey> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserIpCompositeKey(PathMetadata metadata) {
        super(UserIpCompositeKey.class, metadata);
    }

}

