package icn.premierandroid.models;

import java.util.ArrayList;

import icn.premierandroid.misc.CustomFontTextView;

/**
 * Created by ICN on 16/09/2016.
 */
public class SearchDataModel {
    // Getter and Setter model for recycler view items
    private String name;
    private String item;
    private String current_objectId;
    private ArrayList<CustomFontTextView> list;

    public SearchDataModel(String name, String item, String current_objectid, ArrayList<CustomFontTextView> tagsViewList) {

        this.name =  name;
        this.item = item;
        this.current_objectId = current_objectid;
        this.list = tagsViewList;
    }


    public String getName() {return name;}
    public String getCurrent_objectId() {return current_objectId;}
    public String getItem() { return item;}
    public CustomFontTextView getTags(int index) {return list.get(index + 1);}
    public int getTagsSize() {return list.size();}
}
