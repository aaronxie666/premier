package icn.premierandroid.adapters;

import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import icn.premierandroid.R;
import icn.premierandroid.misc.CustomFontButton;
import icn.premierandroid.misc.CustomFontTextView;


class RecyclerViewHolderAdvent extends RecyclerView.ViewHolder {

    CustomFontTextView day_number, day_text;
    public ImageView background;

    RecyclerViewHolderAdvent(View itemView) {
        super(itemView);
        this.day_number = (CustomFontTextView) itemView.findViewById(R.id.advent_day_number);
        this.day_text = (CustomFontTextView) itemView.findViewById(R.id.advent_day);
        this.background = (ImageView) itemView.findViewById(R.id.day_background);
    }
}
