package icn.premierandroid.models;

/**
 * Created by Bradley on 28/08/2016.
 */
public class ModelTipsDataModel {

    // Getter and Setter model for recycler view items
    private String title;
    private String image;
    private String url;

    public ModelTipsDataModel(String title, String image, String url) {

        this.title = title;
        this.image = image;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {return url;}
}
