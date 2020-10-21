package icn.premierandroid.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.premierandroid.R;
import icn.premierandroid.TagsActivity;
import icn.premierandroid.fragments.StreetStyleFragment;
import icn.premierandroid.fragments.StreetStyleFriendsProfileFragment;
import icn.premierandroid.fragments.StreetStyleProfileFragment;
import icn.premierandroid.interfaces.isCurrentUserProfile;
import icn.premierandroid.misc.CircleTransform;
import icn.premierandroid.misc.CustomFontTextView;
import icn.premierandroid.models.SuggestedUsersDataModel;
import icn.premierandroid.models.TagsModel;

import static icn.premierandroid.fragments.StreetStyleFragment.getObjectId;
import static icn.premierandroid.fragments.StreetStyleFragment.setObjectId;

public class RecyclerViewAdapterDialog extends RecyclerView.Adapter<RecyclerViewHolderDialog> {

    private Context context;
    private ArrayList<SuggestedUsersDataModel> userLikes = new ArrayList<>();
    private ArrayList<TagsModel> userTags = new ArrayList<>();
    private Dialog dialog;
    private ArrayList<TextView> usernames = new ArrayList<>();

    public RecyclerViewAdapterDialog(Context context, ArrayList<SuggestedUsersDataModel> userLikes, ArrayList<TagsModel> userTags, Dialog dialog) {
        this.context = context;
        this.userLikes = userLikes;
        this.userTags = userTags;
        this.dialog = dialog;
    }

    @Override
    public RecyclerViewHolderDialog onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.likes_user_item_row, parent, false);
        return new RecyclerViewHolderDialog(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderDialog holder, int position) {
        if (userLikes != null) {
            final SuggestedUsersDataModel model = userLikes.get(position);
            holder.user_name.setText(model.getUsername());
            holder.user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.getCurrentUser().equals(ParseUser.getCurrentUser().getObjectId())) {
                        setObjectId(model.getCurrentUser());
                        StreetStyleFragment.type = isCurrentUserProfile.type.yes;
                        StreetStyleFragment.bottomBar.selectTabAtPosition(3);

                        dialog.dismiss();

                    } else {
                        setObjectId(model.getCurrentUser());
                        StreetStyleFragment.type = isCurrentUserProfile.type.no;
//                        StreetStyleFragment.bottomBar.selectTabAtPosition(3);

                        FragmentTransaction trans = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        trans.replace(R.id.street_style_container, new StreetStyleFriendsProfileFragment());
                        trans.addToBackStack(null);
                        trans.commit();
                        if (StreetStyleFragment.type == isCurrentUserProfile.type.yes) {
                            setObjectId(ParseUser.getCurrentUser().getObjectId());
                            Log.e("selected user click", "" + getObjectId());
                        } else {
                            setObjectId(getObjectId());
                            Log.e("current user click", "" + getObjectId());
                        }
                        dialog.dismiss();
                    }
                }
            });
            holder.profile_picture.setVisibility(View.VISIBLE);
            holder.profile_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.getCurrentUser().equals(ParseUser.getCurrentUser().getObjectId())) {
                        setObjectId(model.getCurrentUser());
                        StreetStyleFragment.type = isCurrentUserProfile.type.yes;
                        StreetStyleFragment.bottomBar.selectTabAtPosition(3);
                        dialog.dismiss();
                    } else {
                        setObjectId(model.getCurrentUser());
                        StreetStyleFragment.type = isCurrentUserProfile.type.no;
//                        StreetStyleFragment.bottomBar.selectTabAtPosition(3);

                        FragmentTransaction trans = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        trans.replace(R.id.street_style_container, new StreetStyleFriendsProfileFragment());
                        trans.addToBackStack(null);
                        trans.commit();
                        if (StreetStyleFragment.type == isCurrentUserProfile.type.yes) {
                            setObjectId(ParseUser.getCurrentUser().getObjectId());
                            Log.e("selected user click", "" + getObjectId());
                        } else {
                            setObjectId(getObjectId());
                            Log.e("current user click", "" + getObjectId());
                        }
                        dialog.dismiss();
                    }
                }
            });
            if (model.getProfilePicture() != null) {
                if (model.getProfilePicture().isEmpty()) {
                    Picasso.with(context).load(R.drawable.profile_nopic).transform(new CircleTransform()).resize(300, 300).centerInside().into(holder.profile_picture);
                } else {
                    Picasso.with(context).load(model.getProfilePicture()).placeholder(R.drawable.profile_nopic).transform(new CircleTransform()).resize(300, 300).centerInside().into(holder.profile_picture);
                }
            } else {
                Picasso.with(context).load(R.drawable.profile_nopic).transform(new CircleTransform()).resize(300, 300).centerInside().into(holder.profile_picture);
            }
        } else if (userTags != null){
            final TagsModel model = userTags.get(position);
            for (int k = 0; k < model.getTagSize(); k++) {
                CustomFontTextView tag = new CustomFontTextView(context);
                ViewGroup.LayoutParams params = holder.user_name.getLayoutParams();
                tag.setLayoutParams(params);
                tag.setGravity(Gravity.CENTER_HORIZONTAL);
                usernames.add(new CustomFontTextView(context));
                holder.profile_picture.setVisibility(View.GONE);
            }

            for (int j = 0; j < usernames.size(); j++) {
                usernames.get(j).setText(model.getTag(j));
                final int finalJ = j;
                usernames.get(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent tagIntent = new Intent(context, TagsActivity.class);
                        tagIntent.putExtra("tag", model.getTag(finalJ));
                        context.startActivity(tagIntent);
                    }
                });
                holder.container.setOrientation(LinearLayout.VERTICAL);
                holder.container.addView(usernames.get(j));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (userLikes != null) {
            return userLikes.size();
        } else {
            return userTags.size();
        }
    }
}
