package icn.premierandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The ViewHolder for RecyclerViewAdapterModelTips
 */

public class RecyclerViewHolderTitleAndImage extends RecyclerView.ViewHolder {
    // View holder for gridview recycler view as we used in listview
    public TextView title;
    public ImageView imageview;


    public RecyclerViewHolderTitleAndImage(View view) {
        super(view);
        // Find all views ids

        this.title = (TextView) view
                .findViewById(R.id.title);
        this.imageview = (ImageView) view
                .findViewById(R.id.image);


    }

}
