package icn.premierandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import icn.premierandroid.adapters.RecyclerViewAdapterComments;
import icn.premierandroid.models.CommentsDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: When user clicks comments button on an image in StreetStyleFragment this displays
 */

public class CommentsActivity extends AppCompatActivity {
    private EditText comment_message;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager mLayoutManager;
    private ArrayList<CommentsDataModel> commentsList, updatesList;
    private ArrayList<String> allUsers;
    private List<ParseObject> objectList = new ArrayList<>();
    private List<ParseObject> listUsers = new ArrayList<>();
    private SimpleDateFormat dateformat;
    private static String imageId;
    private String selectedUser;
    private int count;
    private static int totalComments;
    private Context context = this;
    private RecyclerViewAdapterComments adapter;
    private SwipeRefreshLayout swiper;
    private String final_comment;
    private int refreshCount = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int commentsShown;


    @Override
    protected void onStart() {
        super.onStart();
        populateRecyclerView(false, refreshCount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ss_comment);
        Bundle bundle = getIntent().getExtras();
        imageId = bundle.getString("imageId");
        selectedUser = bundle.getString("selectedUser");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCount++;
                populateRecyclerView(true, refreshCount);
                adapter.update(updatesList);
            }
        });

        Log.e("imageId", "" + imageId);
        final String userId = ParseUser.getCurrentUser().getObjectId();
        comment_message = (EditText) findViewById(R.id.et_commment);
        Button comment_submit = (Button) findViewById(R.id.submit_comment);
        comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject comment = new ParseObject("FashionComments");
                final_comment = comment_message.getText().toString();
                comment.put("photoCommentedOn", imageId);
                comment.put("userID", userId);
                comment.put("comment", final_comment);
                comment_message.setText("");
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            addItemToList(imageId, userId, final_comment);
                            notifyUser(final_comment);
                            updateComments();

                        } else {
                            Log.e("failed", "failed " + e.getMessage());
                        }
                    }
                });
            }
        });
        recyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        refreshCount++;
                        populateRecyclerView(true, refreshCount);
                        adapter.update(updatesList);
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    Log.i("Yaeye!", "end called");
                    loading = true;
                }
            }
        });

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
    }

    private void addItemToList(final String imageid, final String userId, final String comment) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionComments");
        query.whereEqualTo("photoCommentedOn", imageid);
        query.orderByAscending("createdAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                        Log.e("failed", "find first object failed");
                } else {
                    Log.e("ObjectId", object.getObjectId());
                    dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
                    Date get_created = object.getCreatedAt();
                    String created_at = dateformat.format(get_created);
                    Log.e("createdAt", "" + created_at);
                    getUserDetails(userId, comment, created_at);
                }
            }
        });
    }

    private void getUserDetails(final String userId, final String comment, final String created_at) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (object == null) {
                    Log.e("failed", "object retrieval failed");
                } else {
                    String username = object.getString("name");
                    String profile_picture = (String) object.get("profilePicture");
                    String profile_pic_url = profile_picture.replace("http", "https");
                    if (adapter != null) {
                        adapter.addComment(userId, profile_pic_url, username, created_at, comment);
                        adapter.notifyDataSetChanged();
                    } else {
                        populateRecyclerView(true, refreshCount);
                    }
                }
            }
        });
    }

    private void notifyUser(final String final_comment) {
            final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", selectedUser);
            ParseObject notifications = new ParseObject("Notifications");
            notifications.put("action", "comment");
            notifications.put("actionUser", ParseUser.getCurrentUser());
            notifications.put("receivedUser", parseUser);
            notifications.put("photoLiked", imageId);
            notifications.saveInBackground();

            ParseQuery<ParseInstallation> parseQueryInstallation = ParseQuery.getQuery(ParseInstallation.class);
            parseQueryInstallation.whereEqualTo("user", parseUser);
            //sends Push Notification of Friend Request
            ParsePush push = new ParsePush();
            push.setMessage(ParseUser.getCurrentUser().getUsername() + " has just left you a comment " + '"' + final_comment + '"');
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
    }

    private void populateRecyclerView(final Boolean update, int countRefresh) {
        count = 0;
        if (countRefresh == 0) {
            commentsShown = 8;
        }
        else {
            commentsShown = 8 * countRefresh;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionComments");
        query.whereEqualTo("photoCommentedOn", imageId);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    objectList = objects;
                    totalComments = objectList.size();
                    if (commentsShown > totalComments) {
                        commentsShown = totalComments;
                    }
                    commentsList = new ArrayList<>();
                    allUsers = new ArrayList<>();
                    updatesList = new ArrayList<>();
                    for (int k = 0; k < commentsShown; k++) {
                        String userId = objects.get(k).getString("userID");
                        allUsers.add(userId);
                    }
                        Log.e("allusers", "" + allUsers);
                    for (int i = 0; i < allUsers.size(); i++) {
                        Log.d("users" + i, "" + allUsers.get(i));
                    }
                    if (allUsers.size() > 0) {
                        queryUserNames(update);
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }


            }
        });
    }

    public static void updateComments() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("objectId", imageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        String current_total = String.valueOf(totalComments);
                        j.put("comments", current_total);
                        j.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                }

                else {
                    Log.e("failed", "failed " + e.getMessage());
                }
            }
        });

    }

    private void queryUserNames(final Boolean update) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", allUsers.get(count));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                for (ParseObject j : objects) {
                    Log.d("user_id", "" + j.get("name"));
                    listUsers.add(j);
                    count++;
                    if (count < allUsers.size()) {
                        queryUserNames(update);
                    } else {
                        int z = 0;
                        for (ParseObject k : objectList) {
                            if (z < commentsShown) {
                                ParseUser theUser = (ParseUser) listUsers.get(z);
                                String current_object_id = allUsers.get(z);
                                String user_name = (String) theUser.get("name");
                                String profile_picture = (String) theUser.get("profilePicture");
                                String profile_pic_url = profile_picture.replace("http", "https");
                                dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
                                Date get_created = k.getCreatedAt();
                                String created_at = dateformat.format(get_created);
                                String comment = (String) k.get("comment");
                                if (!update) {
                                    commentsList.add(new CommentsDataModel(current_object_id, profile_pic_url, user_name, created_at, comment));
                                } else {
                                    commentsList.add(new CommentsDataModel(current_object_id, profile_pic_url, user_name, created_at, comment));
                                    updatesList.add(new CommentsDataModel(current_object_id, profile_pic_url, user_name, created_at, comment));
                                }
                                z++;
                            }
                        }

                        adapter = new RecyclerViewAdapterComments(context, commentsList, swiper);
                        recyclerView.setAdapter(adapter);// set adapter on recyclerview
                        adapter.notifyDataSetChanged();// Notify the adapter
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}