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
import icn.premierandroid.models.HomeLatestVideosDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerview inside
 *              HomeFragment, the horizontal one.
 */
public class RecyclerViewAdapterHomeLatestVideos extends
    RecyclerView.Adapter<RecyclerViewHolderImageOnly> {

        // recyclerview adapter
        private ArrayList<HomeLatestVideosDataModel> arrayList;
        private Context context;
        private String type;

    public RecyclerViewAdapterHomeLatestVideos(Context context,
                                                   ArrayList <HomeLatestVideosDataModel> arrayList, String type) {
        this.context = context;
        this.arrayList = arrayList;
        this.type = type;
    }

        @Override
        public int getItemCount () {
        return (null != arrayList ? arrayList.size() : 0);

    }

        @Override
        public void onBindViewHolder (RecyclerViewHolderImageOnly holder, final int position){
        final HomeLatestVideosDataModel model = arrayList.get(position);

            Picasso.with(context).load(model.getImage()).into(holder.imageview);
            if (type.equals("Model")) {
                holder.imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(context, VideoActivity.class);
                        myIntent.putExtra("VIDEO_URL", model.getUrl());
                        context.startActivity(myIntent);
                    }
                });
            }



    }

        @Override
        public RecyclerViewHolderImageOnly onCreateViewHolder (ViewGroup viewGroup, int viewType){
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.image_only_item_row, viewGroup, false);
            return new RecyclerViewHolderImageOnly(mainGroup);

    }
}

