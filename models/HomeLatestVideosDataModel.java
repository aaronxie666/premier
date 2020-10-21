package icn.premierandroid.models;

import android.util.Log;

/**
 * Created by ICN on 31/08/2016.
 */
public class HomeLatestVideosDataModel {
    private String image;
    private String video_link;

    public HomeLatestVideosDataModel(String image, String video_link) {

        this.image = image;
        if (!video_link.isEmpty() || video_link != null) {
            this.video_link = video_link;
        } else {
            Log.e("no video link", "no video link");
        }
    }

    public String getImage() {
        return image;
    }
    public String getUrl() {return video_link;}
}
