package icn.premierandroid.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.parse.ParseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterTutorial;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.interfaces.isCurrentUserProfile;
import icn.premierandroid.interfaces.isReselected;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Street Style Tab
 */

public class StreetStyleFragment extends Fragment {

    private static String tempObjectId;
    public static BottomBar bottomBar;
    public static isCurrentUserProfile.type type;
    public isReselected.reselected selected;
    public Boolean alreadyPressed = false;
    public FrameLayout container_layout;
    private Boolean terms;
    private static android.support.v4.view.ViewPager viewPager;
    private Handler handler;
    private int count = 0;

    public StreetStyleFragment() {
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
            rootView = inflater.inflate(R.layout.fragment_ss_home, container, false);
            terms = ParseUser.getCurrentUser().getBoolean("hasUserAcceptedTerms");
            if (!terms || terms == null) {
                    final AlertDialog diaBox = TCDialog();
                    diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        }
                    });
                    diaBox.show();
            }
            bottomBar = (BottomBar) rootView.findViewById(R.id.bottomBar);
            initBottomBar();
            container_layout = (FrameLayout) rootView.findViewById(R.id.street_style_container);
        } else {
            rootView = inflater.inflate(R.layout.fragment_ss_anon, container, false);
            ImageButton loginButton = (ImageButton) rootView.findViewById(R.id.anon_login_home);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AllLoginActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
            });
        }
        return rootView;
    }

    private AlertDialog TCDialog() {
        return new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle("EULA Terms and Conditions")
                .setMessage("To use Street Style, you need to accept our EULA agreement in our Terms & Conditions. ")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        terms = true;
                        ParseUser.getCurrentUser().put("hasUserAcceptedTerms", terms);
                        ParseUser.getCurrentUser().saveEventually();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(8);
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public void initBottomBar() {
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes final int tabId) {
                new Handler().post(new Runnable() {
                    public void run() {
                        FragmentTransaction trans = getFragmentManager().beginTransaction();
                        switch (tabId) {
                            case R.id.tab_home:
                                type = isCurrentUserProfile.type.yes;
                                trans.replace(R.id.street_style_container, new StreetStyleHomeFragment());
                                trans.addToBackStack(null);
                                trans.commit();
                                break;
                            case R.id.tab_search:
                                type = isCurrentUserProfile.type.yes;
                                trans.replace(R.id.street_style_container, new StreetStyleSearchFragment());
                                trans.addToBackStack(null);
                                trans.commit();
                                break;
                            case R.id.tab_camera:
                                type = isCurrentUserProfile.type.yes;
                                trans.replace(R.id.street_style_container, new StreetStyleCameraFragment());
                                trans.addToBackStack(null);
                                trans.commit();
                                alreadyPressed = false;
                                break;
                            case R.id.tab_profile:
                                trans.replace(R.id.street_style_container, new StreetStyleProfileFragment());
                                trans.addToBackStack(null);
                                trans.commit();
                                if (type == isCurrentUserProfile.type.yes) {
                                    setObjectId(ParseUser.getCurrentUser().getObjectId());
                                    Log.e("selected user click", "" + getObjectId());
                                } else {
                                    setObjectId(getObjectId());
                                    Log.e("current user click", "" + getObjectId());
                                }
                                break;
                            case R.id.tab_notification:
                                type = isCurrentUserProfile.type.yes;
                                trans.replace(R.id.street_style_container, new StreetStyleNotificationFragment());
                                trans.addToBackStack(null);
                                trans.commit();
                                break;

                            case 5:
                                trans.replace(R.id.street_style_container, new StreetStyleProfileFragment());
                                trans.addToBackStack(null);
                                trans.commit();
                                if (type == isCurrentUserProfile.type.yes) {
                                    setObjectId(ParseUser.getCurrentUser().getObjectId());
                                    Log.e("selected user click", "" + getObjectId());
                                } else {
                                    setObjectId(getObjectId());
                                    Log.e("current user click", "" + getObjectId());
                                }
                                break;

                        }
                    }
                });
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_profile) {
                    if (type == isCurrentUserProfile.type.yes) {
                        setObjectId(ParseUser.getCurrentUser().getObjectId());
                    }
                }

                if (tabId == R.id.tab_camera) {
                    selected = isReselected.reselected.yes;
                    alreadyPressed = true;
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static void setObjectId(String objectId) {
         tempObjectId = objectId;
    }

    public static String getObjectId() {
        return tempObjectId;
    }

    public static StreetStyleFragment newInstance(int imageNum, ViewPager viewPager1) {
        final StreetStyleFragment f = new StreetStyleFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        if (viewPager1 != null) {
            viewPager = viewPager1;
        }
        f.setArguments(args);
        return f;
    }
}
