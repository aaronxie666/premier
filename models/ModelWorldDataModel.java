package icn.premierandroid.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ICN on 07/10/2016.
 */

public class ModelWorldDataModel {
    // Getter and Setter model for recycler view items
    private String profile_pic_url;
    private String name;
    private String username;
    private String image_one_url;
    private String image_two_url;
    private String image_three_url;
    ArrayList<String> imageUrls;


    public ModelWorldDataModel(String profile_pic_url, String name, String username, ArrayList<String> imageUrls) {

        this.profile_pic_url = profile_pic_url;
        this.name = name;
        this.username = username;
        this.imageUrls = imageUrls;

    }

    public String getProfilePicture() {
        return profile_pic_url;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {return username;}

    public String getImageOne() {return imageUrls.get(0);}
    public String getImageTwo() {return imageUrls.get(1);}
    public String getImageThree() {return imageUrls.get(2);}
    public ArrayList<String> getImages() {return imageUrls;}


}
