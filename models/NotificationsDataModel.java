package icn.premierandroid.models;

/**
 * Created by ICN on 14/09/2016.
 */
public class NotificationsDataModel {
    // Getter and Setter model for recycler view items
    private String currentObjectId;
    private String created_at;
    private String action;
    private String photoLiked;
    private String profile_url;
    private String username;

    public NotificationsDataModel(String currentObjectId, String profileImageUrl, String username, String created_at, String action, String photoLiked) {

        this.currentObjectId = currentObjectId;
        this.profile_url = profileImageUrl;
        this.username = username;
        this.created_at = created_at;
        this.action = action;
        this.photoLiked = photoLiked;

    }

    public String getCurrentObjectId() {return currentObjectId;}

    public String getProfile_url() {return profile_url;}

    public String getUsername() { return username; }

    public String getCreated_at() {
        return created_at;
    }

    public String getAction() {return action;}

    public String getPhotoLiked() {return photoLiked;}

}

