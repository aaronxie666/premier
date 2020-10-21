package icn.premierandroid.misc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterAllFeeds;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.models.AllFeedsDataModel;

import static icn.premierandroid.misc.CONSTANTS.TOTAL_POSTS;
import static java.util.Locale.UK;

public class QueryUserNames {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager, uLayoutManager, tLayoutManager;
    private ArrayList<AllFeedsDataModel> latestUpdatesList;
    private ArrayList<ParseObject> allUsers;
    private List<ParseObject> objectList = new ArrayList<>();
    private List<ParseObject> listUsers = new ArrayList<>();
    private SimpleDateFormat dateformat;
    private int count = 0;
    private ArrayList<String> images = new ArrayList<>();
    private int likesSize;
    private int commentsShown;
    private ArrayList<AllFeedsDataModel> updatesList;
    private RecyclerViewAdapterAllFeeds adapter;
    private Context context;
    private ProgressBar progressBar;
    private ArrayList<ParseUser> allFollowedUsersList;
    private boolean loadOnce = true;
    private String tagName;
    private int followCount;
    private ArrayList<String> blockedUsers = new ArrayList<>();
    private TextView no_follow_display;

    public QueryUserNames(Context context, List<ParseObject> objectList, final ArrayList<ParseObject> allUsers, List<ParseObject> listUsers, int commentsShown, ArrayList<AllFeedsDataModel> latestUpdatesList, ArrayList<AllFeedsDataModel> updatesList, ArrayList<String> images, RecyclerViewAdapterAllFeeds adapter, RecyclerView recyclerView, ProgressBar progressBar, ArrayList<ParseUser> allFollowedUsersList, String tagName, TextView no_follow_display) {
        this.context = context;
        this.objectList = objectList;
        this.allUsers = allUsers;
        this.listUsers = listUsers;
        this.commentsShown = commentsShown;
        this.latestUpdatesList = latestUpdatesList;
        this.updatesList = updatesList;
        this.images = images;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
        this.allFollowedUsersList = allFollowedUsersList;
        this.tagName = tagName;
        this.no_follow_display = no_follow_display;
    }

    private int z = 0;
    public void queryUserNames(final Boolean update, final Boolean home, final Boolean search, final boolean tags) throws ParseException {
        if (objectList.size() > 0 ) {
            ParseObject.fetchAll(allUsers);
            for (ParseObject k : objectList) {
                if (z < commentsShown) {
                    String current_object_id = allUsers.get(z).getObjectId();
                    String user_name = allUsers.get(z).getString("name");
                    if (user_name == null) {
                        user_name = allUsers.get(z).getString("username");
                    }
                    Log.e("username", user_name);
                    String profile_pic_url = allUsers.get(z).getString("profilePicture");
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
                    dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", UK);
                    Date get_created = k.getCreatedAt();
                    String created_at = dateformat.format(get_created);
                    ParseFile image = (ParseFile) k.get("image");
                    String objectid = k.getObjectId();
                    images.add(objectid);
                    String comments = (String) k.get("comments");
                    JSONArray likesJSONArray = k.getJSONArray("peopleVoted");
                    if (likesJSONArray != null) {
                        likesSize = likesJSONArray.length();
                    } else {
                        likesSize = 0;
                    }
                    String tagz = (String) k.get("tags");
                    String caption = (String) k.get("caption");
                    JSONArray tagNamesJSONArray = k.getJSONArray("tagName");
                    JSONArray tagClothingJSONArray = k.getJSONArray("clothingArray");
                    JSONArray posXJSONArray = k.getJSONArray("tagPointX");
                    JSONArray posYJSONArray = k.getJSONArray("tagPointY");
                    if (!update) {
                        latestUpdatesList.add(new AllFeedsDataModel(current_object_id, created_at, image.getUrl(), comments, likesSize, tagz, caption, objectid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, user_name, profile_pic_url, true, tagClothingJSONArray));
                    } else {
                        latestUpdatesList.add(new AllFeedsDataModel(current_object_id, created_at, image.getUrl(), comments, likesSize, tagz, caption, objectid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, user_name, profile_pic_url, true, tagClothingJSONArray));
                        updatesList.add(new AllFeedsDataModel(current_object_id, created_at, image.getUrl(), comments, likesSize, tagz, caption, objectid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, user_name, profile_pic_url, true, tagClothingJSONArray));
                    }
                    z++;
                }
            }
            adapter = new RecyclerViewAdapterAllFeeds(context, latestUpdatesList, false, recyclerView, search);
            recyclerView.setAdapter(adapter);// set adapter on recyclerview
            if (commentsShown < TOTAL_POSTS) {
                adapter.setOnLoadMoreListener(new RecyclerViewAdapterAllFeeds.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        Log.e("haint", "Load More 2");
                        //Remove loading item
                        //Load data
                        int index = latestUpdatesList.size();
                        int end = index + 20;
                        if (loadOnce) {
                            populateRecyclerView(true, end, home, search, tags);
                            adapter.setLoaded();
                        }
                    }
                });
            }
            adapter.notifyDataSetChanged();
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
//                                adapter.notifyDataSetChanged();// Notify the adapter
        }
    }

    private void populateRecyclerView(final Boolean update, final int countRefresh, final boolean home, final boolean search, final boolean tags) {
        followCount = 0;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        count = 0;
        Log.e("countRefresh",String.valueOf(countRefresh));
        if (update) {
            commentsShown = countRefresh;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        if (tags) {
            query.whereEqualTo("tagName", tagName);
        }
        Log.e("SearchFrag", "query fashionfeed");
        query.orderByDescending("createdAt");
        query.setLimit(TOTAL_POSTS + 20);
        Log.e("SearchFrag", "ordered");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if (objects.size() > 0)  {
                        objectList = objects;
                                if (objects.size() < commentsShown) {
                                    commentsShown = objects.size();
                                    progressBar.setIndeterminate(false);
                                }

                        JSONArray blocked = ParseUser.getCurrentUser().getJSONArray("BlockedUsers");
//                        if (blocked != null) {
//                            Log.e("blockedlength", blocked.length() + "");
//                            for (int i = 0; i < blocked.length(); i++) {
//                                try {
//                                    blockedUsers.add(blocked.getString(i));
//                                    Log.e("optstring", blocked.getString(i));
//                                    Log.e("followingArray", "" + blockedUsers.size());
//                                } catch (JSONException e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//                        }
                                updatesList = new ArrayList<>();
                                latestUpdatesList = new ArrayList<>();
                                allUsers = new ArrayList<>();
                                for (int k = 0; k < commentsShown; k++) {
                                    ParseObject companyIdObject = (ParseObject) objects.get(k).get("uploader");
                                    allUsers.add(ParseObject.createWithoutData("_User", companyIdObject.getObjectId()));
                                }
                                loadOnce = false;
                                QueryUserNames queryUserNames = new QueryUserNames(context, objectList, allUsers, listUsers, commentsShown, latestUpdatesList, updatesList, images, adapter, recyclerView, progressBar, null, tagName, no_follow_display);
                        try {
                            queryUserNames.queryUserNames(update, home, search, tags);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }


            }
        });
    }
}
