package platform.auth;

public class AuthHeaderConstants {

    // Prevent instantiation of this class
    private AuthHeaderConstants(){}

    public static final String USER_NOT_FOUND = "User not found";

    //HTTP Bearer Authentication - Originally for OAuth2, but is widely used for JWT as well
    public static final String BEARER = "Bearer";
}
