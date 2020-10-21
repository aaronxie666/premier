package icn.premierandroid.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.R;
import icn.premierandroid.TagsActivity;
import icn.premierandroid.fragments.StreetStyleCameraFragment;
import icn.premierandroid.fragments.StreetStyleFragment;
import icn.premierandroid.interfaces.isCurrentUserProfile;
import icn.premierandroid.misc.CustomFontTextView;
import icn.premierandroid.models.SearchDataModel;

import static icn.premierandroid.misc.CONSTANTS.CAM_SHARED_PREFS;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the search recyclerviews inside StreetStyleSearchFragment
 *              seperate from the posts recyclerview, this is purely names and tags. Also
 *              for the search feature inside StreetStyleCameraFragment.
 */

public class RecyclerViewAdapterSearch extends RecyclerView.Adapter<RecyclerViewAdapterSearch.RecyclerViewHolderSearch> {

    private List<SearchDataModel> searchList;
    private String item;
    private Context context;
    private onRecyclerViewItemClickListener mItemClickListener;
    private ViewAnimator listViewAnimator;
    private CustomFontTextView tag;
    private RelativeLayout root_layout_items;
    private Boolean onlyTag = true;
    private int count = 0;
    private ArrayList<String> tagNamesList = new ArrayList<>();
    private ArrayList<Float> tagXList = new ArrayList<>(), tagYList = new ArrayList<>();
    private ArrayList<String> tagClothingList = new ArrayList<>();
    private String name;


    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }


    public RecyclerViewAdapterSearch(Context context,
                                     List<SearchDataModel> searchListm, ViewAnimator itemAnimator, CustomFontTextView tag, RelativeLayout root_layout_items, ArrayList<Float> tagXList, ArrayList<Float> tagYList, boolean onlyTag, ArrayList<String> tagNamesList, ArrayList<String> tagClothingList, int count) {
        this.context = context;
        this.searchList = searchListm;
        this.listViewAnimator = itemAnimator;
        this.tag = tag;
        this.root_layout_items = root_layout_items;
        this.tagXList = tagXList;
        this.tagYList = tagYList;
        this.onlyTag = onlyTag;
        this.count = count;
        this.tagNamesList = tagNamesList;
        this.tagClothingList = tagClothingList;
    }

    @Override
    public int getItemCount() {
        return (null != searchList ? searchList.size() : 0);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderSearch holder, int position) {
        final SearchDataModel model = searchList.get(position);

        RecyclerViewHolderSearch mainHolder = (RecyclerViewHolderSearch) holder;
        if (model.getItem() == "Tag") {
            mainHolder.name.setText(model.getName());
            mainHolder.item.setText(model.getItem());
        }

        else if (model.getItem() == "User") {
            mainHolder.name.setText(model.getName());
            mainHolder.item.setText(model.getItem());
        }

        else if (model.getItem() == "Retailer") {
            mainHolder.name.setText(model.getName());
            mainHolder.item.setText("");

        }

        else if (model.getItem() == "Item") {
            mainHolder.name.setText(model.getName());
            mainHolder.item.setText("");
        }

        mainHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (model.getItem()) {
                    case "User":
                        StreetStyleFragment.setObjectId(model.getCurrent_objectId());
                        Log.e("ObjectID", "" + StreetStyleFragment.getObjectId());
                        StreetStyleFragment.type = isCurrentUserProfile.type.no;
                        StreetStyleFragment.bottomBar.selectTabAtPosition(3);
                        break;
                    case "Tag":
                        Intent i = new Intent(context, TagsActivity.class);
                        i.putExtra("tag", model.getName());
                        context.startActivity(i);
                        break;
                    case "Retailer":
                        listViewAnimator.showNext();
                        String name = model.getName();
                        SharedPreferences sp = context.getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                        count = sp.getInt("counter", -1);
                        Log.e("counter", "" + count);
                        if (count == -1) {
                            tag = model.getTags(count + 1);
                            tag.setId(count);
                        } else {
                            tag = model.getTags(count - 1);
                            tag.setId(count - 1);
                        }
                        tag.setText(name);
                        break;
                    case "Item":
                        SharedPreferences sp_i = context.getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                        count = sp_i.getInt("counter", -1);
                        tag = model.getTags(count - 1);
                        tag.setId(count - 1);
                        Log.e("count", "" + count);
                        Log.d("item_clicked", "bro");
                        Log.e("id", tag.getId() + "");
                        item = model.getName();
                        Log.e("name", model.getName());
                        setItem(item);
                        String text = tag.getText().toString();
                        String item_of_clothing = getItem();
                        tag.setText(text + " - " + item_of_clothing);
                        onlyTag = count == 1;
                        tag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tag_delete, 0, 0, 0);
                        tag.setCompoundDrawablePadding(20);
                        tag.setPadding(20, 20, 20, 20);
                        root_layout_items.setVisibility(View.INVISIBLE);
                        listViewAnimator.setVisibility(View.INVISIBLE);
                        tagNamesList.add(text);
                        tagClothingList.add(item_of_clothing);
                        tagXList.add(StreetStyleCameraFragment.getXPosition());
                        tagYList.add(StreetStyleCameraFragment.getYPosition());
                        break;
                }
            }
        });
    }

    @Override
    public RecyclerViewAdapterSearch.RecyclerViewHolderSearch onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.search_item_row, viewGroup, false);
        return new RecyclerViewHolderSearch(mainGroup);

    }

    public String getItem() {return item;}

    public void setItem(String item) {
        this.item = item;
    }

    class RecyclerViewHolderSearch extends RecyclerView.ViewHolder implements View.OnClickListener {
        // View holder for gridview recycler view as we used in listview

        public TextView name;
        public TextView item;

        RecyclerViewHolderSearch(View view) {
            super(view);
            this.name = (TextView) view
                    .findViewById(R.id.search_result_name);
            this.item = (TextView) view
                    .findViewById(R.id.search_result_item);
            name.setOnClickListener(this);
        }
        @Override
        public void onClick(View v){
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }
}