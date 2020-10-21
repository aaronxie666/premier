package icn.premierandroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.CommentsActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterNotifications;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.RenderProfileDetails;
import icn.premierandroid.models.NotificationsDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Notification Icon press in StreetStyleFragment Bottom Bar
 */

public class StreetStyleNotificationFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<NotificationsDataModel> notificationsList;
    private ArrayList<ParseObject> allUsers;
    private List<ParseObject> listUsers = new ArrayList<>();
    private List<ParseObject> listNotifications = new ArrayList<>();
    private SimpleDateFormat dateformat;
    private int count = 0;
    private TextView no_follow_display;


    public StreetStyleNotificationFragment() {
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
                || AllLoginActivity.userType == UserType.userType.instagramUser
                || AllLoginActivity.userType == UserType.userType.normalUser) {
            rootView = inflater.inflate(R.layout.fragment_ss_notifications, container, false);
            no_follow_display = (TextView) rootView.findViewById(R.id.no_follow_message);
            no_follow_display.setVisibility(View.GONE);
            RenderProfileDetails.renderProfileDetails(rootView, getContext(), false, null);
            CommentsActivity.updateComments();
            populateRecyclerView();
        }

        assert rootView != null;
        recyclerView = (RecyclerView) rootView.findViewById(R.id.feed_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    private void populateRecyclerView() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notifications");
        query.whereEqualTo("receivedUser", user);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {
                        notificationsList = new ArrayList<>();
                        allUsers = new ArrayList<>();
                        listNotifications = objects;
                        if (objects.size() > 0) {
                            for (ParseObject j : objects) {
                                ParseObject companyIdObject = (ParseObject) j.get("actionUser");
                                allUsers.add(ParseObject.createWithoutData("_User", companyIdObject.getObjectId()));
                            }

                        try {
                            ParseObject.fetchAll(allUsers);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        if (allUsers.size() > 0) {
                            try {
                                queryUserNames();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    else {
                        no_follow_display.setVisibility(View.VISIBLE);
                        no_follow_display.setText(R.string.no_notifications);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }


            }
        });
    }

    private int z = 0;
    private void queryUserNames() throws ParseException {
        for (ParseObject k : listNotifications) {
            String current_object_id = allUsers.get(z).getObjectId();
            String user_name = k.getParseObject("actionUser").fetchIfNeeded().getString("name");
            if (user_name == null) {
                user_name = k.getParseObject("actionUser").fetchIfNeeded().getString("username");
            }
            String profile_pic_url = (String) allUsers.get(z).get("profilePicture");
            if (profile_pic_url == null) {
                profile_pic_url = "null";
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
            dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
            Date get_created = k.getCreatedAt();
            String created_at = dateformat.format(get_created);
            String action = (String) k.get("action");
            switch (action) {
                case "follow":
                    action = "has followed you";
                    break;
                case "like":
                    action = "has liked your photo";
                    break;
                case "comment":
                    action = "has left you a comment";
                    break;
            }
            String photo_liked = (String) k.get("photoLiked");
            notificationsList.add(new NotificationsDataModel(current_object_id, profile_pic_url, user_name, created_at, action, photo_liked));
            z++;
        }
        RecyclerViewAdapterNotifications adapter = new RecyclerViewAdapterNotifications(getActivity(), notificationsList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter
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
