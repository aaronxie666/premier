package icn.premierandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The ViewHolder for RecyclerViewAdapterNotifications
 */

class RecyclerViewHolderNotifications extends RecyclerView.ViewHolder {
    // View holder for gridview recycler view as we used in listview
    ImageView profile_image;
    public TextView user_name;
    public TextView createdAt;
    public TextView action;


    RecyclerViewHolderNotifications(View view) {
        super(view);
        // Find all views ids
        this.profile_image = (ImageView) view
                .findViewById(R.id.notification_profile_picture);
        this.user_name = (TextView) view
                .findViewById(R.id.notification_user_name);
        this.createdAt = (TextView) view
                .findViewById(R.id.notification_time);
        this.action = (TextView) view
                .findViewById(R.id.tv_action);
    }
}
