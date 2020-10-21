package icn.premierandroid.misc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.R;
import icn.premierandroid.interfaces.UserType;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Renders profile details for all login methods in HomeFragment, StreetStyleHomeFragment, StreetStyleNotificationsFragment,
 *              StreetStyleProfileFragment and LikesFragment
 */

public class RenderProfileDetails {
    private static ArrayList<String> blockedUsersList = new ArrayList<>();
    public static void renderProfileDetails(View rootView, Context context, boolean profile, TextView username_placeholder) {
        ImageView profile_picture = (ImageView) rootView.findViewById(R.id.profile_picture);
        TextView user_name = (TextView) rootView.findViewById(R.id.user_full_name);
        TextView user_likes = (TextView) rootView.findViewById(R.id.user_likes);
        EditText user_description = (EditText) rootView.findViewById(R.id.profile_description);
        LinearLayout user_description_container = (LinearLayout) rootView.findViewById(R.id.user_description_container);
        LinearLayout user_likes_container = (LinearLayout) rootView.findViewById(R.id.user_likes_container);
        ParseUser user = ParseUser.getCurrentUser();
        String profile_pic_url = null;
        try {
            profile_pic_url = user.fetch().getString("profilePicture");
            String temp_user_name = user.fetch().getString("name");
            int temp_user_likes = user.fetch().getInt("points");

        String user_points = Integer.toString(temp_user_likes);
        if (username_placeholder != null) {
            username_placeholder.setText(user.fetch().getUsername());
        }
        if (!profile && user_description == null) {
            user_likes_container.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DecimalFormat formatter = new DecimalFormat("#,###,###");
                String finalNumber = formatter.format(temp_user_likes);
                user_likes.setText(finalNumber);
            } else {
                user_likes.setText(user_points);
            }
        } else {
            user_description_container.setVisibility(View.VISIBLE);
            user_description_container.bringToFront();
            String desc = ParseUser.getCurrentUser().fetch().getString("description");
            if (desc == null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String finalNumber = formatter.format(temp_user_likes);
                    user_likes.setText(finalNumber);
                } else {
                    user_likes.setText(user_points);
                }
            } else {
                user_description.setText(desc);
            }
        }
        if (temp_user_name == null) {
            user_name.setText(user.fetch().getUsername());
        } else {
            user_name.setText(temp_user_name);
        }

        if (profile_pic_url.isEmpty()) {
            Log.e("profile_pic_url", "is null");
        } else {
            Log.e("url", profile_pic_url);
            if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                profile_pic_url = profile_pic_url.replace("http", "https");
                Log.e("url2", profile_pic_url);
                Picasso.with(context).load(profile_pic_url).into(profile_picture);
            }

            if (profile_pic_url.startsWith("httpss")) {
                profile_pic_url = profile_pic_url.replace("httpss", "https");
                Log.e("url3", profile_pic_url);
                Picasso.with(context).load(profile_pic_url).into(profile_picture);
            }
        }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void renderSelectedUserProfileDetails(View rootView, final String objectId, final Context context, boolean profile, TextView username_placeholder) {
        ImageView profile_picture = (ImageView) rootView.findViewById(R.id.profile_picture);
        TextView user_name = (TextView) rootView.findViewById(R.id.user_full_name);
        final TextView user_likes = (TextView) rootView.findViewById(R.id.user_likes);
        final EditText user_description = (EditText) rootView.findViewById(R.id.profile_description);
        final LinearLayout user_description_container = (LinearLayout) rootView.findViewById(R.id.user_description_container);
        final LinearLayout user_likes_container = (LinearLayout) rootView.findViewById(R.id.user_likes_container);
        Log.e("objectId--", "" + objectId);
        ParseObject user_details = ParseObject.createWithoutData("_User", objectId);
        try {
            String temp_user_name = user_details.fetchIfNeeded().getString("name");
            final int temp_user_likes = user_details.fetchIfNeeded().getInt("points");
            final String user_points = Integer.toString(temp_user_likes);
            if (temp_user_name == null) {
                user_name.setText(user_details.getString("username"));
            } else {
                user_name.setText(temp_user_name);
            }

            if (username_placeholder != null) {
                username_placeholder.setText(user_details.fetchIfNeeded().getString("username"));
            }
            if (profile) {
                profile_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog diaBox = BlockDialog(context, objectId);
                        diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                            }
                        });
                        diaBox.show();
                    }
                });
            }
            if (!profile && user_description == null) {
                user_likes_container.setVisibility(View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    String finalNumber = formatter.format(temp_user_likes);
                    user_likes.setText(finalNumber);
                } else {
                    user_likes.setText(user_points);
                }
            } else {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", objectId);
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            for (ParseObject j : objects) {
                                String desc = j.getString("description");
                                user_description.setEnabled(false);
                                if (desc == null) {
                                    user_likes_container.setVisibility(View.VISIBLE);
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                                        String finalNumber = formatter.format(temp_user_likes);
                                        user_likes.setText(finalNumber);
                                    } else {
                                        user_likes.setText(user_points);
                                    }
                                } else {
                                    user_description_container.setVisibility(View.VISIBLE);
                                    user_description_container.bringToFront();
                                    user_description.setText(desc);
                                }
                            }
                        } else {
                            Log.e("failed", "failed" + e.getMessage());
                        }
                    }
                });

            }
            String profile_pic_url = user_details.fetchIfNeeded().getString("profilePicture");
            if (profile_pic_url == null) {
                Log.e("profile_pic_url", "is null");
            } else {
                Log.e("url", profile_pic_url);
                if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                    profile_pic_url = profile_pic_url.replace("http", "https");
                    Log.e("url2", profile_pic_url);
                    Picasso.with(context).load(profile_pic_url).into(profile_picture);
                }

                if (profile_pic_url.startsWith("httpss")) {
                    profile_pic_url = profile_pic_url.replace("httpss", "https");
                    Log.e("url3", profile_pic_url);
                    Picasso.with(context).load(profile_pic_url).into(profile_picture);
                }
            }
        } catch (ParseException e) {
            Log.v("Failed_profile_pic", e.toString());
            e.printStackTrace();
        }
    }

    private static AlertDialog BlockDialog(Context context, final String user_id) {
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Block User")
                .setMessage("Would you like to block this user and no longer view their content?")
                .setPositiveButton("Block", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                        userParseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                        userParseQuery.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                JSONArray blockedUsers = objects.get(0).getJSONArray("BlockedUsers");
                                if (blockedUsers != null) {
                                    for (int i = 0; i < blockedUsers.length(); i++) {
                                        blockedUsersList.add(blockedUsers.optString(i));
                                    }
                                    try {
                                        addUserToBlockedList(user_id, blockedUsersList);
                                    } catch (JSONException d) {
                                        d.printStackTrace();
                                    }
                                } else {
                                    final JSONArray newArray = new JSONArray();
                                    ParseUser.getCurrentUser().put("BlockedUsers", newArray);
                                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                try {
                                                    addUserToBlockedList(user_id, blockedUsersList);
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            } else {
                                                Log.e("failed", "failed " + e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private static void addUserToBlockedList(String user_id, ArrayList<String> blockedUsers) throws JSONException {
        blockedUsers.add(user_id);
        JSONArray blocked_temp = new JSONArray(blockedUsers);
        ParseUser.getCurrentUser().put("BlockedUsers", blocked_temp);
        ParseUser.getCurrentUser().saveEventually();
    }
}
