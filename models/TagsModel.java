package icn.premierandroid.models;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TagsModel {

    private ArrayList<String> tags = new ArrayList<>();

    public TagsModel(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getTag(int i) {
        return tags.get(i);
    }

    public int getTagSize() {
        return tags.size();
    }
}
