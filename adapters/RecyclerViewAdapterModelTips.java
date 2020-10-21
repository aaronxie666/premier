package icn.premierandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.premierandroid.R;
import icn.premierandroid.VideoActivity;
import icn.premierandroid.models.ModelTipsDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside ModelTips for all
 *              the BottomBar tabs
 */
public class RecyclerViewAdapterModelTips extends
        RecyclerView.Adapter<RecyclerViewHolderTitleAndImage>{

    // recyclerview adapter
    private ArrayList<ModelTipsDataModel> latestVideosList;
    private Context context;

    public RecyclerViewAdapterModelTips(Context context,
                                        ArrayList<ModelTipsDataModel> latestVideosList) {
        this.context = context;
        this.latestVideosList = latestVideosList;
    }

    @Override
    public int getItemCount() {
        return (null != latestVideosList ? latestVideosList.size() : 0);

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderTitleAndImage holder, int position) {
        final ModelTipsDataModel model = latestVideosList.get(position);

        RecyclerViewHolderTitleAndImage mainHolder = (RecyclerViewHolderTitleAndImage) holder;// holder

        // setting title
        mainHolder.title.setText(model.getTitle());
        Picasso.with(context).load(model.getImage()).into(mainHolder.imageview);
        mainHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, VideoActivity.class);
                myIntent.putExtra("VIDEO_URL", model.getUrl());
                context.startActivity(myIntent);
            }
        });

    }

    @Override
    public RecyclerViewHolderTitleAndImage onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.latest_videos_item_row, viewGroup, false);
        RecyclerViewHolderTitleAndImage listHolder = new RecyclerViewHolderTitleAndImage(mainGroup);
        return listHolder;

    }
}
