package icn.premierandroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside StreetstyleHomeFragment,
 *              when the user clicks the Tutorial Button, this will be shown.
 */

public class RecyclerViewAdapterTutorial extends RecyclerView.Adapter<RecyclerViewAdapterTutorial.RecyclerViewHolderTutorial> {

    private int[] images;
    private Context context;

    public RecyclerViewAdapterTutorial(Context context) {
        this.context = context;
        images = new int[]{R.drawable.tutorial1, R.drawable.tutorial2, R.drawable.tutorial3,
                R.drawable.tutorial4, R.drawable.tutorial5, R.drawable.tutorial6, R.drawable.tutorial7,
                R.drawable.tutorial8};
    }

    @Override
    public RecyclerViewHolderTutorial onCreateViewHolder(ViewGroup parent, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.tutorial_item_row, parent, false);
        return new RecyclerViewHolderTutorial(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderTutorial holder, int position) {
        int id = images[position];
        Picasso.with(context).load(id).fit().centerInside().into(holder.tutorial_image);
    }

    @Override
    public int getItemCount() {
        return (null != images ? images.length : 0);
    }

    class RecyclerViewHolderTutorial extends RecyclerView.ViewHolder {
        private ImageView tutorial_image;
        RecyclerViewHolderTutorial(View itemView) {
            super(itemView);
            this.tutorial_image = (ImageView) itemView.findViewById(R.id.tutorial_image);
        }
    }
}
