package icn.premierandroid.adapters;

import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The ViewHolder for RecyclerViewAdapterAllFeeds
 */

class RecyclerViewHolderAllFeeds extends RecyclerView.ViewHolder {
    // View holder for gridview recycler view as we used in listview
    TextView createdAt, caption, number_of_likes, user_name, number_of_comments, number_of_tags, comments, likesImage, tagsImage;
    ImageView uploadedImage;
    ImageView profile_image;
    RelativeLayout imageContainer;
    LinearLayout row_container;

    RecyclerViewHolderAllFeeds(View view) {
        super(view);
        // Find all views ids
        this.profile_image = (ImageView) view
                .findViewById(R.id.feed_profile_picture);
        this.user_name = (TextView) view
                .findViewById(R.id.feed_user_name);
        this.createdAt = (TextView) view
                .findViewById(R.id.created_date);
        this.uploadedImage = (ImageView) view
                .findViewById(R.id.image);
        this.caption = (TextView) view
                .findViewById(R.id.caption_post);
        this.number_of_likes = (TextView) view
                .findViewById(R.id.number_of_likes);
        this.number_of_comments = (TextView) view
                .findViewById(R.id.number_of_comments);
        this.likesImage = (TextView) view
                .findViewById(R.id.likes_or_like);
        this.number_of_tags = (TextView) view
                .findViewById(R.id.number_of_tags);
        this.tagsImage = (TextView) view
                .findViewById(R.id.tags_or_tag);
        this.comments = (TextView) view
                .findViewById(R.id.comments_or_comment);
        this.imageContainer = (RelativeLayout) view
                .findViewById(R.id.image_container);
        this.row_container = (LinearLayout) view
                .findViewById(R.id.profile_image_user_container);
    }
}