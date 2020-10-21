package icn.premierandroid.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import icn.premierandroid.R;
import icn.premierandroid.misc.CircleTransform;
import icn.premierandroid.models.SuggestedUsersDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerview inside StreetstyleHomeFragment,
 *              that is populated by the suggestedusers from Parse.
 */

public class RecyclerViewAdapterSuggestedUsers extends RecyclerView.Adapter<RecyclerViewAdapterSuggestedUsers.RecyclerViewHolderSuggestedUsers> {

    private ArrayList<SuggestedUsersDataModel> suggestedUsers = new ArrayList<>();
    private ArrayList<SuggestedUsersDataModel> blockedUsers = new ArrayList<>();
    private ArrayList<String> blocked_list = new ArrayList<>();
    private Context context;
    private onRecyclerViewItemClickListener mItemClickListener;
    private Boolean blocked = false;
    private String currentUser;
    private SuggestedUsersDataModel model;

    public RecyclerViewAdapterSuggestedUsers(Context context, ArrayList<SuggestedUsersDataModel> suggestedUsers, ArrayList<SuggestedUsersDataModel> userBlocked) {
        this.context = context;
        this.suggestedUsers = suggestedUsers;
        this.blockedUsers = userBlocked;
    }

    @Override
    public RecyclerViewHolderSuggestedUsers onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.suggested_users_item_row, parent, false);
        return new RecyclerViewHolderSuggestedUsers(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderSuggestedUsers holder, int position) {
        if (blockedUsers != null) {
            blocked = true;
            model = blockedUsers.get(position);
            currentUser = model.getCurrentUser();
            blocked_list.add(currentUser);
            holder.bind(blockedUsers.get(position), mItemClickListener, context, blocked, model);
        } else {
            model = suggestedUsers.get(position);
            holder.bind(suggestedUsers.get(position), mItemClickListener, context, blocked, model);
        }
    }

    @Override
    public int getItemCount () {
        if (suggestedUsers != null) {
            return suggestedUsers.size();
        } else {
            return blockedUsers.size();
        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, int position, String currentUser) throws JSONException;
    }

    class RecyclerViewHolderSuggestedUsers extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView profile_picture;
        public TextView user_name;
        ImageView follow_button;
        private RecyclerViewAdapterSuggestedUsers.onRecyclerViewItemClickListener mItemClickListener;

        RecyclerViewHolderSuggestedUsers(View itemView) {
            super(itemView);
            this.profile_picture = (ImageView) itemView.findViewById(R.id.suggested_profile_image);
            this.user_name = (TextView) itemView.findViewById(R.id.suggested_user_name);
            follow_button = (ImageView) itemView.findViewById(R.id.follow_suggested_user);
            follow_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                if (mItemClickListener != null) {
                    try {
                        mItemClickListener.onItemClickListener(v, getAdapterPosition(), currentUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("clicked", "CLICKED, CLICKED, CLICKED");
                }
        }

        private void bind(final SuggestedUsersDataModel model, RecyclerViewAdapterSuggestedUsers.onRecyclerViewItemClickListener mItemClickListener, final Context context, Boolean blocked, SuggestedUsersDataModel model1) {
            if (blocked) {
                follow_button.setImageResource(R.drawable.unblocked);
                follow_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < blocked_list.size(); i++) {
                            if (blocked_list.get(i) == model.getCurrentUser()) {
                                blocked_list.remove(i);
                                ParseUser.getCurrentUser().put("BlockedUsers", blocked_list);
                                final int finalI = i;
                                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            blockedUsers.remove(finalI);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "User has now been unblocked.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e("failed", "failed" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
            this.mItemClickListener = mItemClickListener;
            user_name.setText(model1.getUsername());
            if (model1.getProfilePicture().isEmpty()) {
                Picasso.with(context).load(R.drawable.profile_nopic).transform(new CircleTransform()).resize(300, 300).centerInside().into(profile_picture);
            } else {
                Picasso.with(context).load(model1.getProfilePicture()).placeholder(R.drawable.profile_nopic).transform(new CircleTransform()).resize(300, 300).centerInside().into(profile_picture);
            }

//        if (profile_picture.getDrawable() == null) {
//            Picasso.with(context).load(R.drawable.profile_nopic).transform(new CircleTransform()).resize(300, 300).centerInside().into(profile_picture);
//        }
        }
    }


}
