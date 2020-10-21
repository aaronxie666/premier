package icn.premierandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.adapters.RecyclerViewAdapterAllFeeds;
import icn.premierandroid.misc.QueryUserNames;
import icn.premierandroid.models.AllFeedsDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: When user presses a tag button in StreetStyleFragment feeds
 */

public class TagsActivity extends AppCompatActivity  {
    public RecyclerViewAdapterAllFeeds adapter;
    private String tagName;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<AllFeedsDataModel> latestUpdatesList;
    private ArrayList<ParseObject> allUsers;
    private List<ParseObject> objectList = new ArrayList<>();
    private List<ParseObject> listUsers = new ArrayList<>();
    private SimpleDateFormat dateformat;
    private int count = 0;
    private TextView no_follow_display;
    private ArrayList<String> images = new ArrayList<>();
    private int likesSize;
    private ProgressBar progressBar;
    private ArrayList<AllFeedsDataModel> updatesList;
    private int commentsShown;
    private int refreshCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ss_tag_results);

        Bundle bundle = getIntent().getExtras();
        tagName = bundle.getString("tag");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        TextView tag_display = (TextView) findViewById(R.id.tag_title);
        tag_display.setText("' " + tagName + " '");

        no_follow_display = (TextView) findViewById(R.id.no_follow_message);
        no_follow_display.setVisibility(View.GONE);

        populateRecyclerView(true, refreshCount);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

//        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//                refreshCount++;
//                if (refreshCount < 5) {
//                    populateRecyclerView(true, refreshCount);
//                }
//                adapter.update(updatesList);
//                adapter.setLoaded();
//                System.out.println("load");
//            }
//        });
    }

    public void populateRecyclerView(final boolean update, int countRefresh) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("tagName", tagName);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                    if (objects.size() > 0)  {
                        objectList = objects;
                        Log.e("objectsSize", "" + objectList.size());
                        updatesList = new ArrayList<>();
                        latestUpdatesList = new ArrayList<>();
                        allUsers = new ArrayList<>();
                        commentsShown = objects.size();
                        for (int k = 0; k < commentsShown; k++) {
                            ParseObject companyIdObject = (ParseObject) objects.get(k).get("uploader");
                            allUsers.add(ParseObject.createWithoutData("_User", companyIdObject.getObjectId()));
                        }

                        try {
                            ParseObject.fetchAll(allUsers);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        QueryUserNames queryUserNames = new QueryUserNames(TagsActivity.this, objectList, allUsers, listUsers, commentsShown, latestUpdatesList, updatesList, images, adapter, recyclerView, progressBar, null, tagName, no_follow_display);
                        try {
                            queryUserNames.queryUserNames(update, false, false, true);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else {
                        no_follow_display.setVisibility(View.VISIBLE);
                        no_follow_display.setText(R.string.no_users_tag_posted);
                        recyclerView.setVisibility(View.GONE);
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
