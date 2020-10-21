package icn.premierandroid.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.CommentsActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterAllFeeds;
import icn.premierandroid.adapters.RecyclerViewAdapterDialog;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.EndlessRecyclerViewScrollListener;
import icn.premierandroid.misc.RenderProfileDetails;
import icn.premierandroid.models.AllFeedsDataModel;
import icn.premierandroid.models.SuggestedUsersDataModel;

import static icn.premierandroid.misc.CONSTANTS.LOADED_POSTS;
import static icn.premierandroid.misc.CONSTANTS.TOTAL_POSTS;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Profile Icon press in StreetStyleFragment Bottom Bar
 */

public class StreetStyleProfileFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<AllFeedsDataModel> latestPostList;
    private SimpleDateFormat dateformat;
    private ParseUser user;
    private ParseObject user_;
    private String objectid;
    private TextView posts_total, followers_total, following_total, likes_total, likes_title, no_follow_display, posts_title, followers_title, following_title;
    private ImageButton follow_button, unfollow_button, redeem_button;
    private ArrayList<String> totalFollowers;
    private ArrayList<String> totalFollowing;
    private String imageid;
    private ArrayList<String> images = new ArrayList<>();
    public String imageId;
    private int likesSize;
    private EditText profile_description;
    private int likes, conversionRate;
    private int total;
    private ProgressBar progressBar;
    private int commentsShown;
    private ArrayList<AllFeedsDataModel> updatesList;
    private RecyclerViewAdapterAllFeeds adapter;
    private int refreshCount = 0;
    protected RecyclerView followers_dialog_recycler, following_dialog_recycler;
    private ArrayList<String> followersArray = new ArrayList<>();
    private ArrayList<String> followingArray = new ArrayList<>();
    Dialog followingDialog, followersDialog;
    private ArrayList<SuggestedUsersDataModel> userFollowing;
    private ArrayList<ParseObject> followingListUsers;
    private List<ParseObject> objectsList;
    private int count_following = 0;
    private JSONArray following;
    private int count_followers = 0;
    private ArrayList<SuggestedUsersDataModel> userFollowers;
    private ArrayList<ParseObject> followerListUsers;
    private JSONArray followers;

    public StreetStyleProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        if (AllLoginActivity.userType == UserType.userType.facebookUser
                || AllLoginActivity.userType == UserType.userType.instagramUser
                || AllLoginActivity.userType == UserType.userType.normalUser) {
            rootView = inflater.inflate(R.layout.fragment_ss_profile, container, false);
            posts_total = (TextView) rootView.findViewById(R.id.total_posts);
            posts_title = (TextView) rootView.findViewById(R.id.posts_title);
            likes_total = (TextView) rootView.findViewById(R.id.total_liked);
            likes_title = (TextView) rootView.findViewById(R.id.total_likes_title);
            followers_total = (TextView) rootView.findViewById(R.id.total_followers);
            followers_total.setOnClickListener(customListener);
            followers_title = (TextView) rootView.findViewById(R.id.followers_title);
            followers_title.setOnClickListener(customListener);
            following_total = (TextView) rootView.findViewById(R.id.total_following);
            following_total.setOnClickListener(customListener);
            following_title = (TextView) rootView.findViewById(R.id.following_title);
            following_title.setOnClickListener(customListener);
            follow_button = (ImageButton) rootView.findViewById(R.id.follow_button);
            TextView username_placeholder = (TextView) rootView.findViewById(R.id.user_name_placeholder);
            unfollow_button = (ImageButton) rootView.findViewById(R.id.unfollow_button);
            redeem_button = (ImageButton) rootView.findViewById(R.id.edit_button);
            profile_description = (EditText) rootView.findViewById(R.id.profile_description);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            objectid = StreetStyleFragment.getObjectId();
            Log.e("before if object", "" + objectid);
            no_follow_display = (TextView) rootView.findViewById(R.id.no_follow_message);
            follow_button.setVisibility(View.INVISIBLE);
            redeem_button.setVisibility(View.INVISIBLE);
            unfollow_button.setVisibility(View.INVISIBLE);

            CommentsActivity.updateComments();
            //user profile of selected user
            if (objectid != ParseUser.getCurrentUser().getObjectId()) {
                Log.e("current objectid", "" + objectid);
                populateSelectedUserRecyclerView(objectid, false);
                populateSelectedUserInfo(objectid);
                RenderProfileDetails.renderSelectedUserProfileDetails(rootView, objectid, getContext(), true, username_placeholder);
                isUserFollowingSelectedUser();
                follow_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        followSelectedUser();
                        addCurrentToFollowing();
                        follow_button.setVisibility(View.INVISIBLE);
                        redeem_button.setVisibility(View.INVISIBLE);
                        unfollow_button.setVisibility(View.VISIBLE);
                    }
                });
                unfollow_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unfollowUser(objectid);
                        unfollow_button.setVisibility(View.INVISIBLE);
                        redeem_button.setVisibility(View.INVISIBLE);
                        follow_button.setVisibility(View.VISIBLE);
                    }
                });
            }
            //current user profile
            else if (objectid == ParseUser.getCurrentUser().getObjectId()){
                Log.e("user objectid", "" + objectid);
                populateSelectedUserRecyclerView(objectid, false);
                populateCurrentUserInfo();
                RenderProfileDetails.renderProfileDetails(rootView, getContext(), true, username_placeholder);
                unfollow_button.setVisibility(View.INVISIBLE);
                redeem_button.setVisibility(View.VISIBLE);
                follow_button.setVisibility(View.INVISIBLE);
                profile_description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            closeKeyboard(v);
                            showSaveDialog();
                        }
                    }
                });
            }

            redeem_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRedeemDialog();
                }
            });

            recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);
            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false );
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if (StreetStyleFragment.getObjectId() != ParseUser.getCurrentUser().getObjectId()) {
                                refreshCount++;
                                if (refreshCount < 5) {
                                    populateSelectedUserRecyclerView(objectid, true);
                                }
                                if (updatesList != null) {
                                    adapter.update(updatesList);
                                    adapter.setLoaded();
                                }
                                progressBar.setIndeterminate(false);
                                System.out.println("load");
                            }
                }
            });
        }

        return rootView;
    }

    private void showRedeemDialog() {
        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.redeem_dialog);
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
        Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
        TextView description = (TextView) dialog.findViewById(R.id.redeem_description);
        TextView conversion_description = (TextView) dialog.findViewById(R.id.conversion_description);
        getLikes(description, conversion_description);
        // Click cancel to dismiss android custom dialog box
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total != 0) {
                    convertLikes();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.following_title:
                    showFollowingDialog();
                    break;
                case R.id.total_following:
                    showFollowingDialog();
                    break;
                case R.id.followers_title:
                    showFollowersDialog();
                    break;
                case R.id.total_followers:
                    showFollowersDialog();
                    break;
            }
        }
    };

    private void showFollowersDialog() {
        followersDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        followersDialog.setContentView(R.layout.recycler_view);
        followersDialog.setCancelable(true);
        followers_dialog_recycler = (RecyclerView) followersDialog.findViewById(R.id.dialog_recyclerview);
        populateFollowersDialog();
        followers_dialog_recycler.setHasFixedSize(true);
        followers_dialog_recycler.setBackgroundColor(Color.WHITE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        followers_dialog_recycler.setLayoutManager(mLayoutManager);
        followersDialog.show();
    }

    private void populateFollowersDialog() {
        count_followers = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        ParseObject user;
        if (objectid == ParseUser.getCurrentUser().getObjectId()) {
            user = ParseUser.getCurrentUser();
        } else {
            user = ParseObject.createWithoutData("_User", objectid);
        }
        Log.e("user", user.getObjectId());
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    userFollowers= new ArrayList<>();
                    followerListUsers = new ArrayList<>();
                    for (ParseObject j : objects) {
                        followers = j.getJSONArray("followersArray");
                        if (followers != null) {
                            if (followers.length() != 0) {
                                Log.e("followerslength", followers.length() + "");
                                for (int i = 0; i < followers.length(); i++) {
                                    try {
                                        followersArray.add(followers.getString(i));
                                        Log.e("optstring", followers.getString(i));
                                        Log.e("followingArray", "" + followersArray.size());
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                if (count_followers < followers.length()) {
                                    queryFollowersUsernames();
                                }
                            } else {
                                Log.e("no following", "following no one");
                            }
                        } else {
                            Log.e("following", "following is null");
                        }
                    }
                } else {
                    Log.e("failed", "failed" +  e.getMessage());
                }
            }
        });
    }

    private void queryFollowersUsernames() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", followersArray.get(count_followers));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            Log.e("objectid", j.getObjectId());
                            Log.d("user_id_profile", "" + j.get("name"));
                            followerListUsers.add(j);
                            Log.e("followinglistUsers", followerListUsers.toString());
                            count_followers++;
                            Log.e("count_following", "" + count_followers);
                            Log.e("following_array_size", "" + followersArray.size());
                            if (count_followers < followersArray.size()) {
                                queryFollowersUsernames();
                            } else {
                                Log.e("gets into else", "gets here");
                                int z = 0;
                                for (ParseObject k : followerListUsers) {
                                    if (z < followers.length()) {
                                        ParseUser theUser = (ParseUser) followerListUsers.get(z);
                                        String user_name = theUser.getUsername();
                                        if (user_name == null) {
                                            user_name = theUser.getString("name");
                                        }
                                        String objectId = followersArray.get(z);
                                        Log.e("user_name", user_name);
                                        String profile_pic_url = (String) theUser.get("profilePicture");
                                        if (profile_pic_url == null) {
                                            Log.e("profile_pic_url", "is null");
                                        } else {
                                            Log.e("url", profile_pic_url);
                                            if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                                                profile_pic_url = profile_pic_url.replace("http", "https");
                                                Log.e("url2", profile_pic_url);
                                            }

                                            if (profile_pic_url.startsWith("httpss")) {
                                                profile_pic_url = profile_pic_url.replace("httpss", "https");
                                                Log.e("url3", profile_pic_url);
                                            }
                                        }
                                        Log.e("profile", "profilepic" + profile_pic_url);
                                        userFollowers.add(new SuggestedUsersDataModel(user_name, profile_pic_url, objectId));
                                        z++;
                                    }
                                }

                                RecyclerViewAdapterDialog adapter = new RecyclerViewAdapterDialog(getActivity(), userFollowers, null, followersDialog);
                                followers_dialog_recycler.setAdapter(adapter);// set adapter on recyclerview
                                adapter.notifyDataSetChanged();// Notify the adapter
                            }
                        }
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            });
        } catch (IndexOutOfBoundsException e) {
            Log.e("failed", "failed " + e.getMessage());
        }

    }

    private void showFollowingDialog() {
        followingDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        followingDialog.setContentView(R.layout.recycler_view);
        followingDialog.setCancelable(true);
        following_dialog_recycler = (RecyclerView) followingDialog.findViewById(R.id.dialog_recyclerview);
        populateFollowingDialog();
        following_dialog_recycler.setHasFixedSize(true);
        following_dialog_recycler.setBackgroundColor(Color.WHITE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        following_dialog_recycler.setLayoutManager(mLayoutManager);
        followingDialog.show();
    }

    private void populateFollowingDialog() {
        count_following = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        ParseObject user;
        if (objectid == ParseUser.getCurrentUser().getObjectId()) {
            user = ParseUser.getCurrentUser();
        } else {
            user = ParseObject.createWithoutData("_User", objectid);
        }
        Log.e("user", user.getObjectId());
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    objectsList = new ArrayList<>();
                    userFollowing = new ArrayList<>();
                    followingListUsers = new ArrayList<>();
                    for (ParseObject j : objects) {
                        following = j.getJSONArray("followingArray");
                        Log.e("followinglength", following.length() + "");
                        if (following != null) {
                            if (following.length() != 0) {
                                for (int i = 0; i < following.length(); i++) {
                                    try {
                                        followingArray.add(following.getString(i));
                                        Log.e("optstring", following.getString(i));
                                        Log.e("followingArray", "" + followingArray.size());
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                if (count_following < following.length()) {
                                    queryFollowingUsernames();
                                }
                            } else {
                                Log.e("no following", "following no one");
                            }
                        } else {
                            Log.e("following", "following is null");
                        }
                    }
                } else {
                    Log.e("failed", "failed" +  e.getMessage());
                }
            }
        });
    }

    private void queryFollowingUsernames() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", followingArray.get(count_following));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            Log.e("objectid", j.getObjectId());
                            Log.d("user_id_profile", "" + j.get("name"));
                            followingListUsers.add(j);
                            Log.e("followinglistUsers", followingListUsers.toString());
                            count_following++;
                            Log.e("count_following", "" + count_following);
                            Log.e("following_array_size", "" + followingArray.size());
                            if (count_following < followingArray.size()) {
                                queryFollowingUsernames();
                            } else {
                                Log.e("gets into else", "gets here");
                                int z = 0;
                                for (ParseObject k : followingListUsers) {
                                    if (z < following.length()) {
                                        ParseUser theUser = (ParseUser) followingListUsers.get(z);
                                        String user_name = theUser.getUsername();
                                        if (user_name == null) {
                                            user_name = theUser.getString("name");
                                        }
                                        String objectId = followingArray.get(z);
                                        Log.e("user_name", user_name);
                                        String profile_picture = (String) theUser.get("profilePicture");
                                        if (profile_picture != null) {
                                            if (profile_picture.isEmpty()) {
                                                Log.e("profile_pic_url", "is null");
                                            } else {
                                                if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                                                    profile_picture = profile_picture.replace("http", "https");
                                                }

                                                if (profile_picture.startsWith("httpss")) {
                                                    profile_picture = profile_picture.replace("httpss", "https");
                                                }
                                            }
                                        }
                                        Log.e("profile", "profilepic" + profile_picture);
                                        userFollowing.add(new SuggestedUsersDataModel(user_name, profile_picture, objectId));
                                        z++;
                                    }
                                }

                                RecyclerViewAdapterDialog adapter = new RecyclerViewAdapterDialog(getActivity(), userFollowing, null, followingDialog);
                                following_dialog_recycler.setAdapter(adapter);// set adapter on recyclerview
                                adapter.notifyDataSetChanged();// Notify the adapter
                            }
                        }
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            });
        } catch (IndexOutOfBoundsException e) {
            Log.e("failed", "failed " + e.getMessage());
        }

    }

    private void convertLikes() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        int likesRedeemed = j.getInt("likesRedeemed");
                        likesRedeemed = likesRedeemed + likes;
                        j.put("likesRedeemed", likesRedeemed);
                        j.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    int points = ParseUser.getCurrentUser().getInt("points");
                                    points = points + total;
                                    ParseUser.getCurrentUser().put("points", points);
                                    ParseUser.getCurrentUser().saveInBackground();
                                } else {
                                    Log.e("failed", "failed" + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void showSaveDialog() {
        final AlertDialog diaBox = AskOption();
        diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        diaBox.show();
    }

    private void populateSelectedUserInfo(String objectid) {
        getSelectedUserFollowers(objectid);
        getSelectedUserPosts(objectid);
        getSelectedUserFollowing(objectid);
        getSelectedUserLikes(objectid);
    }

    private void populateCurrentUserInfo() {
        getCurrentUserFollowers();
        getCurrentUserPosts();
        getCurrentUserFollowing();
        getCurrentUserLikes();
    }

    private void getCurrentUserLikes() {
        user  = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    for (ParseObject j : objects) {
                        int totalLikes = j.getInt("likes");
                        likes_total.setText(String.valueOf(totalLikes));
                        if (totalLikes == 1){
                            likes_title.setText(R.string.one_like_title);
                        }
                        else {
                            likes_title.setText(R.string.liked_title);
                        }
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

            }

        });
    }

    private void getSelectedUserLikes(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        user_ = ParseObject.createWithoutData("_User", objectId);
        query.whereEqualTo("user", user_);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    for (ParseObject j : objects) {
                        int totalLikes = j.getInt("likes");
                        likes_total.setText(String.valueOf(totalLikes));
                        if (totalLikes == 1){
                            likes_title.setText(R.string.one_like_title);
                        }
                        else {
                            likes_title.setText(R.string.liked_title);
                        }
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

            }

        });
    }

    private void removeCurrentFromFollowing() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Follow");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        ArrayList<String> user_array = new ArrayList<>();
                        user_array.add(StreetStyleFragment.getObjectId());
                        j.removeAll("followingArray", user_array);
                        j.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.e("updated", "following array updated");
                                } else {
                                    Log.e("failed", "update failed for following array");
                                }
                            }
                        });
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void removeFollowingFromUser() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", StreetStyleFragment.getObjectId());
        query.whereEqualTo("user", parseUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                for (ParseObject j : objects) {
                    int following = j.getInt("following");
                    if (following == 0) {
                        j.put("following", following);
                        j.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    removeFollowerFromSelected();
                                } else {
                                    Log.e("failed", "failed" + e.getMessage());
                                }
                            }
                        });
                    } else {
                        int totalFollowing = following - 1;
                        j.put("following", totalFollowing);
                        j.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    removeFollowerFromSelected();
                                    Log.e("updated", "following array updated");
                                } else {
                                    Log.e("failed", "update failed for following array");
                                }
                            }
                        });
                    }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void removeFollowerFromSelected() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", StreetStyleFragment.getObjectId());
        query.whereEqualTo("user", parseUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        int follower = j.getInt("followers");
                        int totalFollowers = follower - 1;
                        if (totalFollowers > 0) {
                            j.put("followers", totalFollowers);
                            j.saveInBackground();
                        } else if (totalFollowers == 0) {
                            j.put("followers", 0);
                            j.saveInBackground();

                        }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });

    }

    private void unfollowUser(String objectid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        ParseObject parseUser = ParseObject.createWithoutData("_User", objectid);
        query.whereEqualTo("user", parseUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject j : objects) {
                    if (e == null) {
                        JSONArray followers = j.getJSONArray("followersArray");
                        if (followers != null) {
                            ArrayList<String> user_array = new ArrayList<>();
                            user_array.add(ParseUser.getCurrentUser().getObjectId());
                            j.removeAll("followersArray", user_array);
                            j.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        removeCurrentFromFollowing();
                                        removeFollowingFromUser();
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                }
                                }
                            });
                        } else {
                            removeFollowingFromUser();
                        }
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            }
        });
    }

    private void isUserFollowingSelectedUser() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(ParseObject j : objects) {
                    JSONArray following = j.getJSONArray("followingArray");
                    if(following != null) {
                        Log.e("followingArray", "JSON ARRAY ISN'T NULL");
                        if (following.toString().contains(objectid)) {
                            if (objectid != ParseUser.getCurrentUser().getObjectId()) {
                                Log.e("isfollowing", "is true");
                                redeem_button.setVisibility(View.INVISIBLE);
                                follow_button.setVisibility(View.INVISIBLE);
                                unfollow_button.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.e("isfollowing", "is False");
                            redeem_button.setVisibility(View.INVISIBLE);
                            follow_button.setVisibility(View.VISIBLE);
                            unfollow_button.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void followUser() {
        ParseQuery<ParseObject> followUser = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", StreetStyleFragment.getObjectId());
        followUser.whereEqualTo("user", parseUser);
        followUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        JSONArray followersArray = objects.get(0).getJSONArray("followersArray");
                        if (followersArray != null){
                            followersArray.put(ParseUser.getCurrentUser().getObjectId());
                            objects.get(0).put("followersArray", followersArray);
                            objects.get(0).saveEventually();
                        }
                        else {
                            //array is null
                             JSONArray new_followers_array = new JSONArray();
                             new_followers_array.put(ParseUser.getCurrentUser().getObjectId());
                             objects.get(0).put("followersArray", new_followers_array);
                             objects.get(0).saveEventually();
                        }
                    } else {
                        ParseObject j = new ParseObject("Follow");
                        j.put("user", parseUser);
//                        ArrayList<String> user_array1 = new ArrayList<>();
//                        user_array1.add(ParseUser.getCurrentUser().getObjectId());
                        j.add("followersArray", ParseUser.getCurrentUser().getObjectId());
                        j.saveEventually();
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void addCurrentToFollowing() {
        final ParseQuery<ParseObject> followUser = ParseQuery.getQuery("Follow");
        followUser.whereEqualTo("user", ParseUser.getCurrentUser());
        followUser.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        ArrayList<String> user_array = new ArrayList<>();
                        if (e == null) {
                            if (objects.size() > 0) {
                                JSONArray followingArray = objects.get(0).getJSONArray("followingArray");
                                if (followingArray != null){
                                    followingArray.put(objectid);
                                    objects.get(0).put("followingArray", followingArray);
                                    objects.get(0).saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            followUser();
                                            addFollowingToCurrent();
                                            NotifyUser();
                                        }
                                    });
                                }
                                else {
                                    //array is null
                                    JSONArray new_following_array = new JSONArray();
                                    new_following_array.put(StreetStyleFragment.getObjectId());
                                    objects.get(0).put("followingArray", new_following_array);
                                    objects.get(0).saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            followUser();
                                            addFollowingToCurrent();
                                            NotifyUser();
                                        }
                                    });

                                }
                            } else {
                                ParseObject followUserObject = new ParseObject("Follow");
                                followUserObject.put("user", ParseUser.getCurrentUser());
                                followUserObject.put("likes", 0);
                                user_array.add(StreetStyleFragment.getObjectId());
                                followUserObject.addAll("followingArray", user_array);
                                followUserObject.saveEventually(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        followUser();
                                        addFollowingToCurrent();
                                        NotifyUser();
                                    }
                                });

                            }
                        } else {
                            Log.e("failed", "failed" + e.getMessage());
                        }
                    }
                        });

    }

    private void addFollowingToCurrent() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        int following = j.getInt("following");
                        int totalFollowing = following + 1;
                        j.put("following", totalFollowing);
                        j.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    addFollowerToSelected();
                                } else {
                                    Log.e("failed", "failed" + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void addFollowerToSelected() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", StreetStyleFragment.getObjectId());
        query.whereEqualTo("user", parseUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        int follower = j.getInt("followers");
                        int totalFollower = follower + 1;
                        j.put("followers", totalFollower);
                        j.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.e("success", "success");
                                } else {
                                    Log.e("failed", "failed" + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void NotifyUser() {
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", StreetStyleFragment.getObjectId());
        ParseObject notifications = new ParseObject("Notifications");
        notifications.put("action", "follow");
        notifications.put("actionUser", ParseUser.getCurrentUser());
        notifications.put("receivedUser", parseUser);
        notifications.saveEventually();

        //queries the installation class in the user column for the object ID of the user that the friend request is getting sent to
        ParseQuery<ParseInstallation> parseQueryInstallation = ParseQuery.getQuery(ParseInstallation.class);
        parseQueryInstallation.whereEqualTo("user", parseUser);
        //sends Push Notification of Friend Request
        ParsePush push = new ParsePush();
        push.setMessage(ParseUser.getCurrentUser().getUsername() + " has just followed you.");
        push.setQuery(parseQueryInstallation);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.e("SAVE SUCCESS", "");
                }else{
                    Log.e("ERROR", e.getMessage());
                }
            }
        });


//        //alert popup stating the username inputted is invalid
//        AlertDialog.Builder alertFriendsRequestConfirmation = new AlertDialog.Builder(getActivity());
//        alertFriendsRequestConfirmation.setTitle("Success");
//        alertFriendsRequestConfirmation.setMessage("You successfully");
//        alertFriendsRequestConfirmation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // Do something with value!
//                dialog.cancel();
//                addFriendsDialog.dismiss();
//            }
//        });
//        alertFriendsRequestConfirmation.show();

    }

    private void populateSelectedUserRecyclerView(final String objectid, final boolean update) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        Log.e("countRefresh",String.valueOf(refreshCount));
        if (refreshCount == 0) {
            commentsShown = LOADED_POSTS;
        }
        else if (update) {
            commentsShown = LOADED_POSTS * refreshCount;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("uploader", ParseObject.createWithoutData("_User", objectid));
        query.orderByDescending("createdAt");
        Log.e("get order", "ordered");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    updatesList = new ArrayList<>();
                    if (objects.size() > 0) {
                        Log.e("does it get here", "it got here");
                        if (commentsShown > TOTAL_POSTS) {
                            commentsShown = TOTAL_POSTS;
                        }
                        latestPostList = new ArrayList<>();
                        for (ParseObject j : objects) {
                            if (objects.size() < TOTAL_POSTS) {
                                dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
                                Date get_created = j.getCreatedAt();
                                String created_at = dateformat.format(get_created);
                                imageid = j.getObjectId();
                                setImageId(imageid);
                                images.add(imageid);
                                Log.e("OBJECTID", "" + imageid);
                                String currentObjectId = StreetStyleFragment.getObjectId();
                                JSONArray likesJSONArray = j.getJSONArray("peopleVoted");
                                if (likesJSONArray != null) {
                                    likesSize = likesJSONArray.length();
                                } else {
                                    likesSize = 0;
                                }
                                ParseFile image = (ParseFile) j.get("image");
                                String comments = (String) j.get("comments");
                                String tags = (String) j.get("tags");
                                String caption = (String) j.get("caption");
                                JSONArray tagNamesJSONArray = j.getJSONArray("tagName");
                                JSONArray tagClothingJSONArray = j.getJSONArray("clothingArray");
                                JSONArray posXJSONArray = j.getJSONArray("tagPointX");
                                JSONArray posYJSONArray = j.getJSONArray("tagPointY");
                                if (!update) {
                                    if (latestPostList.size() < objects.size()) {
                                        latestPostList.add(new AllFeedsDataModel(currentObjectId, created_at, image.getUrl(), comments, likesSize, tags, caption, imageid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, null, null, false, tagClothingJSONArray));
                                    }
                                } else {
                                    if (latestPostList.size() < objects.size()) {
                                        latestPostList.add(new AllFeedsDataModel(currentObjectId, created_at, image.getUrl(), comments, likesSize, tags, caption, imageid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, null, null, false, tagClothingJSONArray));
                                        updatesList.add(new AllFeedsDataModel(currentObjectId, created_at, image.getUrl(), comments, likesSize, tags, caption, imageid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, null, null, false, tagClothingJSONArray));
                                    }
                                }
                            }
                        }
                    }
                    else {
                        no_follow_display.setVisibility(View.VISIBLE);
                        no_follow_display.setText(R.string.no_posts);
                        no_follow_display.bringToFront();
                        recyclerView.setVisibility(View.INVISIBLE);
                    }

                    if (objectid == ParseUser.getCurrentUser().getObjectId()) {
                        adapter = new RecyclerViewAdapterAllFeeds(getActivity(), latestPostList, true, recyclerView, false);
                    } else {
                        adapter = new RecyclerViewAdapterAllFeeds(getActivity(), latestPostList, false, recyclerView, false);
                    }
                    recyclerView.setAdapter(adapter);// set adapter on recyclerview
//                    adapter.setOnLoadMoreListener(new RecyclerViewAdapterAllFeeds.OnLoadMoreListener() {
//                        @Override
//                        public void onLoadMore() {
//                            if (StreetStyleFragment.getObjectId() != ParseUser.getCurrentUser().getObjectId()) {
//                                refreshCount++;
//                                populateSelectedUserRecyclerView(objectid, true);
//                                if (updatesList != null) {
//                                    adapter.update(updatesList);
//                                    adapter.setLoaded();
//                                }
//                                progressBar.setIndeterminate(false);
//                                System.out.println("load");
//                            }
//                        }
//                    });
                    adapter.notifyDataSetChanged();
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    public void getCurrentUserPosts() {
        user  = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<>("FashionFeed");
        query.whereEqualTo("uploader", user);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    int objects_size = objects.size();
                    user.put("posts", objects_size);
                    posts_total.setText(String.valueOf(objects_size));
                    if (objects_size == 1){
                        posts_title.setText(R.string.one_post);
                    }
                    else {
                        posts_title.setText(R.string.got_posts);
                    }

                        user.saveInBackground();
                    }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

            }

        });
    }

    public void getSelectedUserPosts(String objectId) {
        ParseQuery<ParseObject> query = new ParseQuery<>("FashionFeed");
        user_ = ParseObject.createWithoutData("_User", objectId);
        query.whereEqualTo("uploader", user_);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    int objects_size = objects.size();
                    user_.put("posts", objects_size);
                    posts_total.setText(String.valueOf(objects_size));
                    if (objects_size == 1){
                        posts_title.setText(R.string.one_post);
                    }
                    else {
                        posts_title.setText(R.string.got_posts);
                    }
                    user_.saveInBackground();
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

            }

        });
    }




    public void getCurrentUserFollowers() {
        user  = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<>("Follow");
        query.whereEqualTo("user", user);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    if (objects.size() > 0) {
                        JSONArray followersArray = objects.get(0).getJSONArray("followersArray");
                        totalFollowers = new ArrayList<>();
                        if (followersArray != null) {
                            for (int i = 0; i < followersArray.length(); i++) {
                                String user_ids = followersArray.optString(i);
                                totalFollowers.add(user_ids);
                            }
                            user.put("followers", totalFollowers.size());
                            followers_total.setText(String.valueOf(totalFollowers.size()));
                            if (followersArray.length() == 1){
                                followers_title.setText(R.string.one_follower);
                            }
                            else{
                                followers_title.setText(R.string.got_followers);
                            }
                            user.saveInBackground();
                        } else {
                            followers_total.setText(R.string.none);
                            followers_title.setText(R.string.got_followers);
                        }
                    }
                    else {
                        Log.e("failed", "failed: objects is null");
                        followers_total.setText(R.string.none);
                        followers_title.setText(R.string.got_followers);
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

            }

        });

    }

    public void getSelectedUserFollowers(String objectid) {
        user  = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<>("Follow");
        user_ = ParseObject.createWithoutData("_User", objectid);
        query.whereEqualTo("user", user_);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    JSONArray followersArray = objects.get(0).getJSONArray("followersArray");
                    if (followersArray != null) {
                        int objects_size = followersArray.length();
                        user_.put("followers", objects_size);
                        followers_total.setText(String.valueOf(objects_size));
                        followers_total.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSelectedFollowersDialog();
                            }
                        });
                        if (followersArray.length() == 1) {
                            followers_title.setText(R.string.one_follower);
                        }
                        else {
                            followers_title.setText(R.string.got_followers);
                        }
                    }
                    else {
                        followers_title.setText(R.string.got_followers);
                    }
                    user_.saveInBackground();
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }

        });
    }

    private void showSelectedFollowersDialog() {

    }

    public void getCurrentUserFollowing() {
        user  = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<>("Follow");
        query.whereEqualTo("user", user);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                        if (objects.size() > 0) {
                            JSONArray followingArray = objects.get(0).getJSONArray("followingArray");
                            totalFollowing = new ArrayList<>();
                            if (followingArray != null) {
                                for (int i = 0; i < followingArray.length(); i++) {
                                    String user_ids = followingArray.optString(i);
                                    totalFollowing.add(user_ids);
                                }
                                user.put("following", totalFollowing.size());
                                following_total.setText(String.valueOf(totalFollowing.size()));
                                user.saveInBackground();
                            } else {
                                following_total.setText("0");
                            }
                        }
                         else {
                            following_total.setText("0");
                            }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

            }

        });

    }

    public void getSelectedUserFollowing(String objectid) {
        user  = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = new ParseQuery<>("Follow");
        user_ = ParseObject.createWithoutData("_User", objectid);
        query.whereEqualTo("user", user_);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    JSONArray followingArray = objects.get(0).getJSONArray("followingArray");
                    int objects_size = 0;
                    if (followingArray != null) {
                        if (followingArray.length() > 0) {
                            objects_size = followingArray.length();
                        }
                    }
                    user_.put("following", objects_size);
                    following_total.setText(String.valueOf(objects_size));
                    user_.saveInBackground();
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }

        });
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("Save Description")
                .setMessage("Are you sure you would like to save " + profile_description.getText().toString() + "?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        saveDescriptionToParse();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void saveDescriptionToParse() {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("description", profile_description.getText().toString());
        user.saveInBackground();
    }

    public void getLikes(final TextView description, final TextView conversion_description) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject j : objects) {
                    int total = j.getInt("likes");
                    int difference = j.getInt("likesRedeemed");
                    likes = total - difference;
                    description.setText("You have recieved " + String.valueOf(likes) + " likes on your photos, you can convert these to Premier likes currency.");
                    Log.e("likes", "" + likes);
                    getConversionRate(conversion_description);
                }
            }
        });
    }

    public void getConversionRate(final TextView conversion_description) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Points");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject j : objects) {
                    conversionRate = j.getInt("redeemMultiplier");
                    total = likes * conversionRate;
                    conversion_description.setText("You can convert " + likes + " likes into " + total + " Premier likes.");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    trans.replace(R.id.street_style_container, new StreetStyleHomeFragment());
                    trans.addToBackStack(null);
                    trans.commit();
                    return true;
                }
                return false;
            }
        });
    }

}
