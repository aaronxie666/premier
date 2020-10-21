package icn.premierandroid.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import icn.premierandroid.R;

class RecyclerViewHolderDialog extends RecyclerView.ViewHolder {

    public ImageView profile_picture;
    public TextView user_name;
    public LinearLayout container;

    RecyclerViewHolderDialog(View itemView) {
        super(itemView);
        profile_picture = (ImageView) itemView.findViewById(R.id.dialog_profile_image);
        user_name = (TextView) itemView.findViewById(R.id.dialog_user_name);
        container = (LinearLayout) itemView.findViewById(R.id.suggested_container);
    }
}
