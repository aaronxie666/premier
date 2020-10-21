package icn.premierandroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: BlogContentFragment adapter for horizontal RecyclerView.
 */

public class RecyclerViewAdapterBlogImages extends RecyclerView.Adapter<RecyclerViewAdapterBlogImages.RecyclerViewHolderBlogImages> {

    private ArrayList<String> imageUrls = new ArrayList<>();
    private Context context;
    private onRecyclerViewItemClickListener mItemClickListener;

    public RecyclerViewAdapterBlogImages(Context context,
                                         ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public RecyclerViewHolderBlogImages onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.image_only_item_row, viewGroup, false);
        return new RecyclerViewHolderBlogImages(mainGroup);

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderBlogImages holder, int position) {
            if (imageUrls.get(position).isEmpty()) {
                Log.e("failed", "no images loaded");
            } else {
                Picasso.with(context).load(imageUrls.get(position)).into(holder.imageview);
            }
    }

    @Override
    public int getItemCount() {
        return (null != imageUrls ? imageUrls.size() : 0);

    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position, String s);
    }

    class RecyclerViewHolderBlogImages extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageview;

        RecyclerViewHolderBlogImages(View view) {
            super(view);
            this.imageview = (ImageView) view
                    .findViewById(R.id.image);
            imageview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition(), imageUrls.get(getAdapterPosition()));
            }
        }
    }
}
