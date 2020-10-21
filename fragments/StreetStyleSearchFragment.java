package icn.premierandroid.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import icn.premierandroid.CommentsActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterAllFeeds;
import icn.premierandroid.adapters.RecyclerViewAdapterSearch;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.EndlessRecyclerViewScrollListener;
import icn.premierandroid.misc.QueryUserNames;
import icn.premierandroid.models.AllFeedsDataModel;
import icn.premierandroid.models.SearchDataModel;

import static icn.premierandroid.misc.CONSTANTS.LOADED_POSTS;
import static icn.premierandroid.misc.CONSTANTS.TOTAL_POSTS;
import static java.util.Locale.UK;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Search Icon press in StreetStyleFragment Bottom Bar
 */

public class StreetStyleSearchFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager, uLayoutManager, tLayoutManager;
    private ArrayList<AllFeedsDataModel> latestUpdatesList;
    private ArrayList<String> allUsers;
    private ArrayList<ParseObject> endUsers = new ArrayList<>();
    private List<ParseObject> objectList = new ArrayList<>();
    private List<ParseObject> listUsers;
    private List<SearchDataModel> matchingUsers, matchingTags;
    private int count = 0;
    private TextView no_follow_display;
    private LinearLayout list_container, users_container, tags_container, root_layout;
    private RecyclerView userSearchList, tagsSearchList;
    private EditText search_bar;
    private String searchQuery;
    private ArrayList<String> images = new ArrayList<>();
    private int likesSize;
    private int commentsShown;
    private ArrayList<AllFeedsDataModel> updatesList;
    private RecyclerViewAdapterAllFeeds adapter;
    private Handler handler;
    private int refreshCount = 0;
    private ProgressBar progressBar;
    private ArrayList<String> blockedUsers = new ArrayList<>();
    private JSONArray blocked;


    public StreetStyleSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        populateRecyclerView(false, refreshCount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ss_search, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        list_container = new LinearLayout(getActivity());
        users_container = new LinearLayout(getActivity());
        tags_container = new LinearLayout(getActivity());
        tagsSearchList = new RecyclerView(getActivity());
        userSearchList = new RecyclerView(getActivity());
        handler = new Handler();
        root_layout = (LinearLayout) rootView.findViewById(R.id.scroll_child);
        no_follow_display = (TextView) rootView.findViewById(R.id.no_follow_message);
        no_follow_display.setVisibility(View.GONE);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        search_bar = (EditText) rootView.findViewById(R.id.search_edittext);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (list_container.getParent() != null) {
                    searchQuery = search_bar.getText().toString();
                    userSearchList.setHasFixedSize(true);
                    tagsSearchList.setHasFixedSize(true);
                    userSearchList.setLayoutManager(uLayoutManager);
                    tagsSearchList.setLayoutManager(tLayoutManager);
                } else {
                    searchQuery = search_bar.getText().toString();
                    LinearLayout.LayoutParams root_container_params = (new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    list_container.setLayoutParams(root_container_params);
                    list_container.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout.LayoutParams list_container_params = (new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    users_container.setLayoutParams(list_container_params);
                    tags_container.setLayoutParams(list_container_params);
                    LinearLayout.LayoutParams list_params = (new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    list_params.setMargins(3, 0, 3, 0);
                    userSearchList.setLayoutParams(list_params);
                    tagsSearchList.setLayoutParams(list_params);
                    list_container.setBackgroundResource(R.color.bg_grey);
                    root_layout.addView(list_container);
                    list_container.addView(users_container);
                    list_container.addView(tags_container);
                    tags_container.addView(tagsSearchList);
                    users_container.addView(userSearchList);
                    userSearchList.setHasFixedSize(true);
                    tagsSearchList.setHasFixedSize(true);
                    uLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    tLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    userSearchList.setLayoutManager(uLayoutManager);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers();
                searchTags();
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchUsers();
                searchTags();
            }
        });

        CommentsActivity.updateComments();
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.bringToFront();
//        swiper.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        if (adapter != null) {
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    refreshCount++;
                    if (refreshCount < 5) {
                        populateRecyclerView(true, refreshCount);
                    }
                    adapter.update(updatesList);
                    adapter.setLoaded();
                    System.out.println("load");
                }
            });
        }


//                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                visibleItemCount = recyclerView.getChildCount();
//                totalItemCount = mLayoutManager.getItemCount();
//                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
//                if (!loading) {
//                    if ((totalItemCount - visibleItemCount) <= firstVisibleItem) {
//                        // Loading NOT in progress and end of list has been reached
//                        // also triggered if not enough items to fill the screen
//                        // if you start loading
//                        loading = true;
//                        Log.e("Yaeye!", "end called");
//                    }
//                }
//
//                if (loading) {
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        refreshCount++;
//                        populateRecyclerView(true, refreshCount);
//                        adapter.update(updatesList);
//                        previousTotal = totalItemCount;
//                    }
//                }
//            }
//        });

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        return rootView;
    }

    private void populateRecyclerView(final Boolean update, int countRefresh) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        count = 0;
        Log.e("countRefresh",String.valueOf(countRefresh));
        if (countRefresh == 0) {
            commentsShown = LOADED_POSTS;
        }

        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    blocked = objects.get(0).getJSONArray("BlockedUsers");
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
                    Log.e("SearchFrag", "query fashionfeed");
                    query.orderByDescending("createdAt");
                    Log.e("SearchFrag", "ordered");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e == null) {
                                if (objects.size() > 0) {
                                    Log.e("objectsSize", "" + objectList.size());
                                    updatesList = new ArrayList<>();
                                    latestUpdatesList = new ArrayList<>();
                                    allUsers = new ArrayList<>();
                                    listUsers = new ArrayList<>();
                                    if (blocked != null) {
                                        for (int i = 0; i <= blocked.length(); i++) {
                                            try {
                                                blockedUsers.add(blocked.getString(i));
                                                Log.e("optstring", blocked.getString(i));
                                                Log.e("followingArray", "" + blockedUsers.size());
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }

                                    for (int k = 0; k < TOTAL_POSTS - 20 + blockedUsers.size(); k++) {
                                        ParseObject companyIdObject = (ParseObject) objects.get(k).get("uploader");
                                        if (blockedUsers.contains(companyIdObject.getObjectId())) {
                                            Log.e("blocked user", "" + k);
                                        } else {
                                            allUsers.add(companyIdObject.getObjectId());
                                            objectList.add(objects.get(k));
                                        }
                                    }

                                    for (int j = 0; j < allUsers.size(); j++) {
                                        if (blockedUsers.contains(allUsers.get(j))) {
                                            allUsers.remove(j);
                                            objectList.remove(j);
                                        }
                                        if (allUsers.get(j) != null) {
                                            endUsers.add(ParseObject.createWithoutData("_User", allUsers.get(j)));
                                        }
                                    }

                                    try {
                                        ParseObject.fetchAll(endUsers);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    QueryUserNames queryUserNames = new QueryUserNames(getContext(), objectList, endUsers, listUsers, allUsers.size(), latestUpdatesList, updatesList, images, adapter, recyclerView, progressBar, null, null, no_follow_display);
                                    try {
                                        queryUserNames.queryUserNames(update, false, true, false);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    no_follow_display.setVisibility(View.VISIBLE);
                                    no_follow_display.setText(R.string.no_users_posted);
                                    recyclerView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                Log.e("failed", "failed" + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private int z = 0;
    private void queryUserNames(boolean update, boolean search) throws ParseException {
        for (ParseObject k : objectList) {
            if (z < commentsShown) {
                String current_object_id = endUsers.get(z).getObjectId();
                String user_name = endUsers.get(z).getString("name");
                if (user_name == null) {
                    user_name = endUsers.get(z).getString("username");
                }
                Log.e("username", user_name);
                String profile_pic_url = (String) endUsers.get(z).get("profilePicture");
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
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", UK);
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
                String tags = (String) k.get("tags");
                String caption = (String) k.get("caption");
                JSONArray tagNamesJSONArray = k.getJSONArray("tagName");
                JSONArray tagClothingJSONArray = k.getJSONArray("clothingArray");
                JSONArray posXJSONArray = k.getJSONArray("tagPointX");
                JSONArray posYJSONArray = k.getJSONArray("tagPointY");
                if (!update) {
                    latestUpdatesList.add(new AllFeedsDataModel(current_object_id, created_at, image.getUrl(), comments, likesSize, tags, caption, objectid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, user_name, profile_pic_url, true, tagClothingJSONArray));
                } else {
                    latestUpdatesList.add(new AllFeedsDataModel(current_object_id, created_at, image.getUrl(), comments, likesSize, tags, caption, objectid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, user_name, profile_pic_url, true, tagClothingJSONArray));
                    updatesList.add(new AllFeedsDataModel(current_object_id, created_at, image.getUrl(), comments, likesSize, tags, caption, objectid, posXJSONArray, posYJSONArray, tagNamesJSONArray, images, user_name, profile_pic_url, true, tagClothingJSONArray));
                }
                z++;
            }
        }
        adapter = new RecyclerViewAdapterAllFeeds(getActivity(), latestUpdatesList, false, recyclerView, true);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
//        if (commentsShown < TOTAL_POSTS) {
//            adapter.setOnLoadMoreListener(new RecyclerViewAdapterAllFeeds.OnLoadMoreListener() {
//                @Override
//                public void onLoadMore() {
//                    Log.e("haint", "Load More 2");
//                    //Remove loading item
//                    //Load data
//                    int index = latestUpdatesList.size();
//                    int end = index + 20;
//                    if (loadOnce) {
//                        populateRecyclerView(true, end, home, search, tags);
//                        adapter.setLoaded();
//                    }
//                }
//            });
//        }
        adapter.notifyDataSetChanged();
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void searchTags() {
        ParseQuery<ParseObject> tagQuery = ParseQuery.getQuery("OfficialRetailers");
        tagQuery.whereStartsWith("retailer", searchQuery);
        tagQuery.whereContains("retailer", searchQuery);
        tagQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (e == null) {
                    Log.e("does it get here", "it got here");
                    matchingTags = new ArrayList<>();
                    for (ParseObject j : objects) {
                        String tagName = (String) j.get("retailer");
                        String objectId = j.getObjectId();
                        matchingTags.add(new SearchDataModel(tagName, "Tag", objectId, null));
                        Log.e("tags", "" + matchingTags.size());
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
                RecyclerViewAdapterSearch adapter = new RecyclerViewAdapterSearch(getActivity(), matchingTags, null, null, null, null, null, false, null, null, 0);
                tagsSearchList.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });
    }

    public void searchUsers(){
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
        userQuery.whereStartsWith("name", searchQuery);
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    Log.e("does it get here", "it got here");
                    matchingUsers = new ArrayList<>();
                    for (ParseObject j : objects) {
                        String username = (String) j.get("name");
                        String objectId = j.getObjectId();
                        matchingUsers.add(new SearchDataModel(username, "User", objectId, null));
                        Log.e("tags", "" + matchingUsers.size());
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

                RecyclerViewAdapterSearch adapter = new RecyclerViewAdapterSearch(getActivity(), matchingUsers, null, null, null, null, null, false, null, null, 0);
                userSearchList.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
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
