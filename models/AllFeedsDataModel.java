package icn.premierandroid.models;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Bradley on 28/08/2016.
 */
public class AllFeedsDataModel {

    // Getter and Setter model for recycler view items
    private String created_at;
    private String image;
    private String comments;
    private int likes;
    private String tags;
    private String caption;
    private String imageid;
    private String currentUser;
    private String profile_url;
    private String username;
    private JSONArray xpoints;
    private JSONArray ypoints;
    private JSONArray tagName;
    private JSONArray tagClothing;
    private ArrayList<String> imageIds;
    private Boolean feeds;

    public AllFeedsDataModel(String current_user, String created_at, String imageUrl, String comments, int likes, String tags, String caption, String imageId, JSONArray posXArray, JSONArray posYArray, JSONArray tagNamesJSONArray, ArrayList<String> images, String username, String profileImageUrl, Boolean feeds, JSONArray tagClothingJSONArray) {
        this.currentUser = current_user;
        this.created_at = created_at;
        this.image = imageUrl;
        this.comments = comments;
        this.likes = likes;
        this.tags = tags;
        this.caption = caption;
        this.imageid = imageId;
        this.tagName = tagNamesJSONArray;
        this.xpoints = posXArray;
        this.ypoints = posYArray;
        this.imageIds = images;
        this.profile_url = profileImageUrl;
        this.username = username;
        this.feeds = feeds;
        this.tagClothing = tagClothingJSONArray;
        Log.e("OBJECTID", "" + imageid);
    }



    public String getCurrentUser() {return currentUser;}

    public String getCreated_at() {
        return created_at;
    }

    public String getImage() {
        return image;
    }

    public String getComments() {return comments;}

    public String getProfile_url() {return profile_url;}

    public String getUsername() { return username; }

    public int getLikes() {return likes;}

    public String getTags() {return tags;}

    public String getCaption() {return caption; }

    public String getImageId() {return imageid;}

    public String getXpoints(int index) {return xpoints.optString(index); }

    public String getYpoints(int index) {return ypoints.optString(index);}

    public String getTagName(int index) {return tagName.optString(index);}

    public String getImages(int index) {return imageIds.get(index);}

    public int getImageList() {return imageIds.size();}

    public int getTagSize() {
            return tagName.length();
    }

    public boolean getFeeds() {return feeds;}

    public JSONArray getTagsList() {
        return tagName;
    }

    public String getTagClothing(int index) {return tagClothing.optString(index);}
}
