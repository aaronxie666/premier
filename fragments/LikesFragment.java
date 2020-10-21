package icn.premierandroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterLikes;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.MyCustomLayoutManager;
import icn.premierandroid.misc.RenderProfileDetails;
import icn.premierandroid.models.LikesDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Likes Tab
 */

public class LikesFragment extends Fragment{

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<LikesDataModel> likes = new ArrayList<>();
    private boolean anon = false;

    public LikesFragment() {
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

            rootView =  inflater.inflate(R.layout.fragment_likes_social_media, container, false);
            RenderProfileDetails.renderProfileDetails(rootView, getContext(), false, null);
        } else if (AllLoginActivity.userType == UserType.userType.unsignedUser) {
            rootView =  inflater.inflate(R.layout.fragment_likes_anon, container, false);

            ImageButton buttonLogin = (ImageButton) rootView.findViewById(R.id.anon_login_home);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AllLoginActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
            });
            anon = true;
        }

        populateRecyclerView();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new MyCustomLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }

    private void populateRecyclerView() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
        query.orderByAscending("order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        /* Gets image and url of said image that will populate the list */
                        ParseFile listFile = j.getParseFile("image");
                        String listFileUrl;
                        if (listFile != null) {
                            listFileUrl = listFile.getUrl();
                        } else {
                            listFileUrl = "";
                        }
                        /*
                        * Gets Code Image and Url of said image that will show when isCode is true
                        */
                        ParseFile codeFile = j.getParseFile("codeViewImage");
                        String codeFileUrl;
                        if (codeFile != null) {
                            codeFileUrl = codeFile.getUrl();
                        } else {
                            codeFileUrl = "";
                        }

                        /*
                        *Gets More info image and url of said image which will display when a more_info button is pressed
                        */
                        ParseFile moreInfoFile = j.getParseFile("moreInfoImage");
                        String moreInfoFileUrl;
                        if (moreInfoFile != null) {
                            moreInfoFileUrl = moreInfoFile.getUrl();
                        } else {
                            moreInfoFileUrl = "";
                        }

                        /*Gets redeem info Iimage and url of said image which will display when a redeem button is pressed */
                        ParseFile redeemFile = j.getParseFile("redeemImage");
                        String redeemFileUrl;
                        if (redeemFile != null) {
                            redeemFileUrl = redeemFile.getUrl();
                        }  else {
                            redeemFileUrl = "";
                        }

                        /*
                        * Gets final image and url of said image that will display when the user has already participated
                        * or after the user has finished which the other images
                        */
                        String finalImageFileUrl;
                        ParseFile finalImageFile = j.getParseFile("finalImage");

                        if (finalImageFile != null) {
                            finalImageFileUrl = finalImageFile.getUrl();
                        } else {
                            finalImageFileUrl = "";
                        }

                        /* Gets code string */
                        String code = j.getString("code");

                        /* Gets website url */
                        String website_url = j.getString("websiteLink");

                        /* Gets points */
                        int points = j.getInt("points");

                        /* Gets Boolean values based on the name e.g. isAvailable is whether competition or redeem likes is available (true) or not (false)*/
                        Boolean isAvailable = j.getBoolean("isAvailableAndroid");
                        Boolean isAddress = j.getBoolean("isAddress");
                        Boolean isCode = j.getBoolean("isCode");
                        Boolean isCompetition = j.getBoolean("isCompetition");
                        Boolean isUrl = j.getBoolean("isUrl");
                        Boolean isSpecialCompetition = j.getBoolean("specialCompetition");
                        Boolean isMultiCodes = j.getBoolean("isMultipleCodes");

                        /* Gets int for the number which the item is ordered */
                        int order = j.getInt("order");

                        /* Gets ObjectId */
                        String objectid = j.getObjectId();
                        if (isAvailable) {
                            likes.add(new LikesDataModel(listFileUrl, codeFileUrl, moreInfoFileUrl, redeemFileUrl, finalImageFileUrl, code, website_url, points, isAddress, isCode, isCompetition, isUrl, isSpecialCompetition, order, objectid, anon, isMultiCodes));
                        }
                    }
                } else {
                    Log.e("failed", e.getMessage());
                }
                RecyclerViewAdapterLikes adapter = new RecyclerViewAdapterLikes(getActivity(), likes);
                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });
    }

    public static LikesFragment newInstance(int imageNum) {
        final LikesFragment f = new LikesFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }
}
