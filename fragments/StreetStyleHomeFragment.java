package icn.premierandroid.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.CommentsActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterAllFeeds;
import icn.premierandroid.adapters.RecyclerViewAdapterSuggestedUsers;
import icn.premierandroid.adapters.RecyclerViewAdapterTutorial;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.LikesDialog;
import icn.premierandroid.misc.QueryUserNames;
import icn.premierandroid.misc.RenderProfileDetails;
import icn.premierandroid.models.AllFeedsDataModel;
import icn.premierandroid.models.SuggestedUsersDataModel;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_TUTORIAL;
import static icn.premierandroid.misc.CONSTANTS.TOTAL_POSTS;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Home Icon press in StreetStyleFragment Bottom Bar
 */

public class StreetStyleHomeFragment extends Fragment {

    protected RecyclerView recyclerView, suggestedRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager, sLayoutManager;
    private ArrayList<AllFeedsDataModel> latestUpdatesList;
    private ArrayList<SuggestedUsersDataModel> suggestedUsers;
    private ArrayList<String> allUsers;
    private ArrayList<String> suggestedUsersList;
    private ArrayList<String> selectedUser;
    private ArrayList<String> allFollowedUsers;
    private ArrayList<ParseUser> allFollowedUsersList = new ArrayList<>();
    private List<ParseObject> objectList = new ArrayList<>();
    private List<ParseObject> suggestedObjectList = new ArrayList<>();
    private List<ParseObject> selectedUserObjectList = new ArrayList<>();
    private List<ParseObject> listUsers = new ArrayList<>();
    private List<ParseObject> suggestedListUsers = new ArrayList<>();
    private List<ParseObject> selectedUsersList = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private SimpleDateFormat dateformat;
    private TextView no_follow_display, unlock_tutorial_text;
    private int likesSize;
    private int count = 0, count1 = 0, followCount = 0;
    private ImageButton unlock_tutorial_button, unlocked_tutorial_button;
    private int finalFollowers;
    private FrameLayout container;
    private Boolean image;
    private ArrayList<AllFeedsDataModel> updatesList;
    private RecyclerViewAdapterAllFeeds adapter;
    private Handler handler;
    private int refreshCount = 0;
    private ProgressBar progressBar;
    private int commentsShown;
    private ImageView nin3lives;
    private ArrayList<String> allFollowedCopy = new ArrayList<>();
    private JSONArray blocked;
    private ArrayList<ParseObject> endUsers;

    public StreetStyleHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        if (AllLoginActivity.userType == UserType.userType.facebookUser
                || AllLoginActivity.userType == UserType.userType.normalUser || AllLoginActivity.userType == UserType.userType.instagramUser) {
            rootView = inflater.inflate(R.layout.fragment_ss_social_media, container, false);
            no_follow_display = (TextView) rootView.findViewById(R.id.no_follow_message);
            RenderProfileDetails.renderProfileDetails(rootView, getContext(), false, null);
            CommentsActivity.updateComments();
            getAllUsersFollowing(rootView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
            recyclerView.setHasFixedSize(true);
            container = (FrameLayout) rootView.findViewById(R.id.street_style_container);
            suggestedRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_suggested_users);
            suggestedRecyclerView.setHasFixedSize(true);
            unlock_tutorial_text = (TextView) rootView.findViewById(R.id.tutorial_text);
            unlock_tutorial_button = (ImageButton) rootView.findViewById(R.id.tutorial_locked);
            unlock_tutorial_button.setOnClickListener(customListener);
            unlocked_tutorial_button = (ImageButton) rootView.findViewById(R.id.tutorial_unlocked);
            unlocked_tutorial_button.setOnClickListener(customListener);

//            nin3lives = (ImageView) rootView.findViewById(R.id.nine_logo);
//            nin3lives.setOnClickListener(customListener);




            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            sLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            suggestedRecyclerView.setLayoutManager(sLayoutManager);

//            if (adapter != null) {
//                adapter.setOnLoadMoreListener(new RecyclerViewAdapterAllFeeds.OnLoadMoreListener() {
//                    @Override
//                    public void onLoadMore() {
//                        Log.e("haint", "Load More 2");
//                        //Remove loading item
//                        //Load data
//                        int index = latestUpdatesList.size();
//                        int end = index + 20;
//                        populateRecyclerView(true, end);
//                        adapter.setLoaded();
//                    }
//                });
//            }
        }
        return rootView;
    }

    private View getAllUsersFollowing(View rootView) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        Log.e("Follow Query", "created query for follow");
        query.whereEqualTo("user", user);
        Log.e("Follow Query", "restricts query to user column");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        JSONArray followingArray = objects.get(0).getJSONArray("followingArray");
                        allFollowedUsers = new ArrayList<>();
                        if (followingArray != null) {
                            for (int i = 0; i < followingArray.length(); i++) {
                                String user_ids = followingArray.optString(i);
                                allFollowedUsers.add(user_ids);
                                Log.e("followed users", "" + allFollowedUsers.get(i));
                            }

                            allFollowedCopy = allFollowedUsers;
                            if (allFollowedUsers.size() > 0) {
                                populateRecyclerView(false, refreshCount);
                            } else {
                                displayNoFollow();
                            }

                        } else {
                            displayNoFollow();
                        }
                    } else {
                        displayNoFollow();
                    }
                } else {
                    displayNoFollow();
                }
            }
        });

        return rootView;
    }

    private void displayNoFollow() {
        no_follow_display.setText(R.string.not_following_anyone);
        unlock_tutorial_button.setVisibility(View.VISIBLE);
        populateSuggestedRecyclerView();
    }

    private void populateSuggestedRecyclerView() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SuggestedUsers");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    suggestedUsers = new ArrayList<>();
                    suggestedUsersList = new ArrayList<>();
                    suggestedObjectList = objects;
                    for (ParseObject j : objects) {
                        ParseObject users = (ParseObject) j.get("user");
                        suggestedUsersList.add(users.getObjectId());
                        Log.e("objectId", suggestedUsersList.toString());
                    }
                    getUserDetails();
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });

    }

    private void getUserDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", suggestedUsersList.get(count1));
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            Log.d("user_id_home", "" + j.get("name"));
                            suggestedListUsers.add(j);
                            count1++;
                            if (count1 < suggestedUsersList.size()) {
                                getUserDetails();
                            } else {
                                int z = 0;
                                for (ParseObject k : suggestedObjectList) {
                                    ParseUser theUser = (ParseUser) suggestedListUsers.get(z);
                                    String user_name = (String) theUser.get("name");
                                    String objectId = theUser.getObjectId();
                                    String profile_pic_url = theUser.getString("profilePicture");
                                    if (profile_pic_url.isEmpty()) {
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
                                    suggestedUsers.add(new SuggestedUsersDataModel(user_name, profile_pic_url, objectId));
                                    z++;
                                }
                                RecyclerViewAdapterSuggestedUsers adapter = new RecyclerViewAdapterSuggestedUsers(getActivity(), suggestedUsers, null);
                                adapter.setOnItemClickListener(new RecyclerViewAdapterSuggestedUsers.onRecyclerViewItemClickListener() {
                                    @Override
                                    public void onItemClickListener(View view, int position, String currentUser) {
                                        addCurrentToFollowing(suggestedUsersList.get(position));
                                        unlock_tutorial_button.setVisibility(View.INVISIBLE);
                                        unlocked_tutorial_button.setVisibility(View.VISIBLE);
                                        view.setVisibility(View.INVISIBLE);
                                    }
                                });
                                suggestedRecyclerView.setAdapter(adapter);// set adapter on recyclerview
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

    private void populateRecyclerView(final boolean update, int countRefresh) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        count = 0;


        Log.e("countRefresh",String.valueOf(countRefresh));
        if (countRefresh == 0) {
            commentsShown = 30;
        }
        recyclerView.setVisibility(View.VISIBLE);
        unlock_tutorial_button.setVisibility(View.INVISIBLE);

        ParseUser parseUser;
        for (int i = 0; i < allFollowedUsers.size(); i++) {
            parseUser = (ParseUser) ParseUser.createWithoutData("_User", allFollowedUsers.get(i));
            allFollowedUsersList.add(parseUser);
            Log.e("followed list", "" + allFollowedUsersList.get(i));
        }
//        add by chang start
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
 //        add by chang end

        Log.e("get query", "gets query");
          for (int i = 0; i < allFollowedUsersList.size(); i++) {
              ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
            query.whereEqualTo("uploader", allFollowedUsersList.get(i));
            //add by chang
            queries.add(query);
              System.out.println("--------------------------"+query+"-------------------------------------"+queries+"---------------------------------------------------------------");
          }

          //add by chang start
        ParseQuery<ParseObject> mainquery = ParseQuery.or(queries);


        //add by chang end
        mainquery.orderByDescending("createdAt");
        Log.e("get order", "ordered");
        mainquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (e == null) {

                    if (objects.size() > 0) {
                        if (objects.size() < commentsShown) {
                            commentsShown = objects.size();
                            progressBar.setIndeterminate(false);
                        }
//                        followCount++;
//                        if (followCount <= allFollowedUsers.size()) {
//                            populateRecyclerView(false, refreshCount);
//                            System.out.println("------------------------------------=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
//                        } else {
                            endUsers = new ArrayList<>();
                            updatesList = new ArrayList<>();
                            latestUpdatesList = new ArrayList<>();

                            for (int k = 0; k < commentsShown; k++) {
                                ParseObject companyIdObject = (ParseObject) objects.get(k).get("uploader");
                                if (k < TOTAL_POSTS) {
                                    endUsers.add(companyIdObject);
                                    System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+endUsers +"())()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()()("+ commentsShown+ "---" + allFollowedUsersList);
                                }
                            }
                        QueryUserNames queryUserNames = new QueryUserNames(getContext(), objects, endUsers, listUsers, commentsShown, latestUpdatesList, updatesList, images, adapter, recyclerView, progressBar, allFollowedUsersList, null, no_follow_display);
                        try {
                            queryUserNames.queryUserNames(update, true, false, false);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


//                        }
                    } else {
                        no_follow_display.setText("The users you are following, currently haven't posted anything.");
                        no_follow_display.setVisibility(View.VISIBLE);
                }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }


            }
        });




    }

    private void addCurrentToFollowing(final String objectid) {
        final ParseQuery<ParseObject> followUser = ParseQuery.getQuery("Follow");
        followUser.whereEqualTo("user", ParseUser.getCurrentUser());
        followUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ArrayList<String> user_array = new ArrayList<>();
                if (e == null) {
                    if (objects.size() > 0) {
                        JSONArray followingArray = objects.get(0).getJSONArray("followingArray");
                        if (followingArray != null) {
                            followingArray.put(objectid);
                            objects.get(0).put("followingArray", followingArray);
                            objects.get(0).saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    followUser(objectid);
                                    NotifyUser(objectid);
                                }
                            });
                        } else {
                            //array is null
                            JSONArray new_following_array = new JSONArray();
                            new_following_array.put(StreetStyleFragment.getObjectId());
                            objects.get(0).put("followingArray", new_following_array);
                            objects.get(0).saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    followUser(objectid);
                                    NotifyUser(objectid);
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
                                followUser(objectid);
                                NotifyUser(objectid);
                            }
                        });

                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void followUser(final String objectid) {
        ParseQuery<ParseObject> followUser = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", objectid);
        followUser.whereEqualTo("user", parseUser);
        followUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        JSONArray followersArray = objects.get(0).getJSONArray("followersArray");
                        if (followersArray != null) {
                            followersArray.put(ParseUser.getCurrentUser().getObjectId());
                            objects.get(0).put("followersArray", followersArray);
                            objects.get(0).saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        addFollowingToCurrent();
                                        addFollowerToSelected(objectid);
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            //array is null
                            JSONArray new_followers_array = new JSONArray();
                            new_followers_array.put(ParseUser.getCurrentUser().getObjectId());
                            objects.get(0).put("followersArray", new_followers_array);
                            objects.get(0).saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        addFollowingToCurrent();
                                        addFollowerToSelected(objectid);
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        ParseObject j = new ParseObject("Follow");
                        j.put("user", parseUser);
//                        ArrayList<String> user_array1 = new ArrayList<>();
//                        user_array1.add(ParseUser.getCurrentUser().getObjectId());
                        j.add("followersArray", ParseUser.getCurrentUser().getObjectId());
                        j.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    addFollowingToCurrent();
                                    addFollowerToSelected(objectid);
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

    private void NotifyUser(String objectid) {
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", objectid);
        ParseObject notifications = new ParseObject("Notifications");
        notifications.put("action", "follow");
        notifications.put("actionUser", ParseUser.getCurrentUser());
        notifications.put("receivedUser", parseUser);
        notifications.saveEventually();
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tutorial_locked:
                    unlock_tutorial_text.setVisibility(View.VISIBLE);
                    break;
                case R.id.tutorial_unlocked:
                    showTutorialDialog();
                    getActivity().getFragmentManager().popBackStack();
                    signUpStreetStyle();
                    break;
//                case R.id.nine_logo:
//                    Intent tagIntent = new Intent(getContext(), TagsActivity.class);
//                    tagIntent.putExtra("tag", "Nin3Lives");
//                    getActivity().startActivity(tagIntent);
//                    break;
            }
        }
    };

    private void signUpStreetStyle() {
        ParseObject signUp = new ParseObject("StreetStyle_SignUps");
        signUp.put("email", ParseUser.getCurrentUser().getEmail());
        signUp.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());
                    logger.logEvent("StreetStyle Sign Up");
                    Map<String, String> articleParams = new HashMap<String, String>();
                    //param keys and values have to be of String type
                    articleParams.put("Street Style Sign Up", "User signed up to Street Style.");
                    //up to 10 params can be logged with each event
                    FlurryAgent.logEvent("Street Style Sign Up", articleParams);
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void showTutorialDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_dialog);
        recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerViewAdapterTutorial adapter = new RecyclerViewAdapterTutorial(getActivity());
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter

        ImageButton dialogButtonCancel = (ImageButton) dialog.findViewById(R.id.close_button);
        // Click cancel to dismiss android custom dialog box
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLikesDialog();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showLikesDialog() {
        LikesDialog likesDialog = new LikesDialog(getActivity(), PARSE_ID_TUTORIAL);
        likesDialog.display();
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.street_style_container, new StreetStyleHomeFragment());
        trans.addToBackStack(null);
        trans.commit();
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
                        j.saveInBackground(new SaveCallback() {
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

    private void addFollowerToSelected(final String objectid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", objectid);
        query.whereEqualTo("user", parseUser);
        query.include("_User");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                selectedUsersList = new ArrayList<>();
                selectedUser = new ArrayList<>();
                selectedUserObjectList = objects;
                for (ParseObject j : objects) {
                    try {
                        ParseObject companyIdObject = (ParseObject) j.get("user");
                        selectedUser.add(companyIdObject.getObjectId());
                        int followers = objects.get(0).getParseUser("user").fetchIfNeeded().getInt("followers");
                        finalFollowers = followers + 1;
                        saveFollowers();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void saveFollowers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", selectedUser.get(0));
        query.whereEqualTo("user", parseUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        selectedUsersList.add(j);
                        for (ParseObject k : selectedUserObjectList) {
                            ParseObject user = (ParseObject) selectedUsersList.get(0);
                            user.put("followers", finalFollowers);
                            user.saveInBackground(new SaveCallback() {
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
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }
}



