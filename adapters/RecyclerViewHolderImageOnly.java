package icn.premierandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The ViewHolder for RecyclerViewAdapterLatestVideos
 */

class RecyclerViewHolderImageOnly extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView imageview;
    private onRecyclerViewItemClickListener mItemClickListener;

    RecyclerViewHolderImageOnly(View view) {
        super(view);
        this.imageview = (ImageView) view
                .findViewById(R.id.image);
        imageview.setOnClickListener(this);
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }

    @Override
    public void onClick(View v) {
//       mItemClickListener.onItemClickListener(v, getAdapterPosition());
    }
}
