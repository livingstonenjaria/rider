package ke.co.struct.chauffeurrider.Remote;

public class Common {
    public static final String notifications = "notifications";
    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";
    public  static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
