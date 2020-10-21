package icn.premierandroid.models;

/**
 * Created by ICN on 19/10/2016.
 */
public class SuggestedUsersDataModel {

    private String username;
    private String profile_pic;
    private String objectId;

    public SuggestedUsersDataModel(String user_name, String profile_pic_url, String objectId) {
        this.username = user_name;
        this.profile_pic = profile_pic_url;
        this.objectId = objectId;
    }

    public String getUsername() {return username;}
    public String getProfilePicture() {return profile_pic;}
    public String getCurrentUser() {return objectId;}
}
