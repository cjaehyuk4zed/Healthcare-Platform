package platform.constants;

public final class DirectoryMapConstants {

    // Prevent instantiation of this class - Seems to clash with Spring's CGLIB
//    private DirectoryMapConstants(){}
//    private static final DirectoryMapConstants directoryMap = new DirectoryMapConstants();
//    public static DirectoryMapConstants getInstance(){return directoryMap;}

    /**
     * Note : Any changes made here should be reflected in the messenger platform as well!
     */

    // Configurations for storage directories
    public static final String HOME_DIR = "C:/Users/User/Documents/AllofHealth/allofhealth/platform/src/main/resources/static/";

    public static final String DEFAULT_DIR = "C:/Users/User/Documents/AllofHealth/allofhealth/platform/src/main/resources/static/default/";

    public static final String PLATFORM_SERVER_SOCKET_ADDR = "http://192.168.0.2:8080";

    public static final String PLATFORM_SERVER_IP_ADDR = "192.168.0.2";

    public static final String MESSENGER_SERVER_SOCKET_ADDR = "http://192.168.0.2:8070";

    public static final String POSTING_CONTROLLER = "/api/v2/postings";

    public static final String IMAGE_CONTROLLER = "/api/v2/images";

    public static final String FILE_CONTROLLER = "/api/v2/files";

    public static final String USER_CONTROLLER = "/api/v2/users";

    public static final String AUTH_CONTROLLER = "/api/v2/auth";

}
