package icn.premierandroid.misc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.RegisterActivity;
import icn.premierandroid.fragments.StreetStyleFragment;
import icn.premierandroid.fragments.StreetStyleHomeFragment;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_BLOG_FIVE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_BLOG_ONE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_BLOG_TWO;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_DAILY_LOGIN;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_EMAIL;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_FB;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_GOOGLE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_STREET_STYLE_UPLOAD;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_TUTORIAL;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_VIDEO_FIVE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_VIDEO_ONE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_VIDEO_TWO;

public class LikesDialog {
    private String description;
    private Context context;
    private String dialogID;
    private ArrayList<JSONObject> list = new ArrayList<>();
    private AllLoginActivity logins = new AllLoginActivity();
    private String email_insta;
    private Switch ageSwitch;
    private EditText email_address;
    private CustomFontEditText input_facebook;

    public LikesDialog(Context context, String dialogID) {
        this.context = context;
        this.dialogID = dialogID;
    }

    public void display() {
        final int[] pointsAmount = new int[1];
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Points");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    pointsAmount[0] = objects.get(0).getInt(dialogID);
                    getDescription();
                    final Dialog likesDialog = new Dialog(context, android.R.style.Theme_Light);
                    likesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    likesDialog.setContentView(R.layout.custom_dialog);
                    TextView desc_tv = (TextView) likesDialog.findViewById(R.id.tvDescription);
                    switch (pointsAmount[0]) {
                        case 50:
                            LinearLayout fifty_likes = (LinearLayout) likesDialog.findViewById(R.id.fifty_likes_layout);
                            fifty_likes.setVisibility(View.VISIBLE);
                            fifty_likes.bringToFront();
                            break;
                        case 100:
                            LinearLayout one_hundred_likes = (LinearLayout) likesDialog.findViewById(R.id.one_hundred_likes_layout);
                            one_hundred_likes.setVisibility(View.VISIBLE);
                            break;
                        case 150:
                            LinearLayout one_hundred_and_fifty = (LinearLayout) likesDialog.findViewById(R.id.one_hundred_and_fifty_likes_layout);
                            one_hundred_and_fifty.setVisibility(View.VISIBLE);
                            break;
                        case 200:
                            LinearLayout two_hundred_likes = (LinearLayout) likesDialog.findViewById(R.id.two_hundred_likes_layout);
                            two_hundred_likes.setVisibility(View.VISIBLE);
                            break;
                        case 500:
                            LinearLayout five_hundred_likes = (LinearLayout) likesDialog.findViewById(R.id.five_hundred_likes_layout);
                            five_hundred_likes.setVisibility(View.VISIBLE);
                            break;
                        case 1000:
                            LinearLayout one_thousand_likes = (LinearLayout) likesDialog.findViewById(R.id.one_thousand_likes_layout);
                            one_thousand_likes.setVisibility(View.VISIBLE);
                            break;
                    }
                    desc_tv.setText(description);
                    CustomFontButton dialogButtonOk = (CustomFontButton) likesDialog.findViewById(R.id.customDialogOkLogin);
                    // Your android custom dialog ok action
                    // Action for custom dialog ok button click
                    dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            likesDialog.dismiss();
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                            query.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject j : objects) {
                                            int points = j.getInt("points");
                                            points = points + pointsAmount[0];
                                            j.put("points", points);
                                            j.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        switch (dialogID) {
                                                            case PARSE_ID_DAILY_LOGIN:
                                                                MainActivity activity = new MainActivity();
                                                                activity.updateUserLikes();
                                                                break;
                                                            case PARSE_ID_FB:
                                                                input_facebook = new CustomFontEditText(context);
                                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                                                lp.setMargins(20, 0, 20, 0);
                                                                input_facebook.setLayoutParams(lp);
                                                                input_facebook.setBackgroundResource(R.drawable.edit_text_border);
                                                                final AlertDialog diaBox = AskOption();
                                                                diaBox.setCancelable(false);
                                                                diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                                                                    @Override
                                                                    public void onShow(DialogInterface dialog) {
                                                                        diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                                                        diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                                                    }
                                                                });
                                                                diaBox.setView(input_facebook);
                                                                diaBox.show();
                                                                Map<String, String> articleParams = new HashMap<String, String>();
                                                                //param keys and values have to be of String type
                                                                articleParams.put("Facebook Registration", "User registered with Facebook.");
                                                                //up to 10 params can be logged with each event

                                                                AppEventsLogger logger = AppEventsLogger.newLogger(context);
                                                                logger.logEvent("Facebook Registration");                          FlurryAgent.logEvent("Facebook Registration", articleParams);
                                                                break;
                                                            case PARSE_ID_GOOGLE:
                                                                input_facebook = new CustomFontEditText(context);
                                                                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                                                lp1.setMargins(20, 0, 20, 0);
                                                                input_facebook.setLayoutParams(lp1);
                                                                input_facebook.setBackgroundResource(R.drawable.edit_text_border);
                                                                final AlertDialog diaBox1 = AskOption();
                                                                diaBox1.setCancelable(false);
                                                                diaBox1.setOnShowListener(new DialogInterface.OnShowListener() {
                                                                    @Override
                                                                    public void onShow(DialogInterface dialog) {
                                                                        diaBox1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                                                        diaBox1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                                                    }
                                                                });
                                                                diaBox1.setView(input_facebook);
                                                                diaBox1.show();
                                                                Map<String, String> eParams = new HashMap<String, String>();
                                                                //param keys and values have to be of String type
                                                                eParams.put("Google+ Registration", "User registered with Google+.");
                                                                //up to 10 params can be logged with each event

                                                                AppEventsLogger logger1 = AppEventsLogger.newLogger(context);
                                                                logger1.logEvent("Google+ Reg");                      FlurryAgent.logEvent("Google+ Registration", eParams);
                                                                break;
                                                            case PARSE_ID_EMAIL:
                                                                try {
                                                                    final ParseObject follow = new ParseObject("Follow");
                                                                    follow.put("likes", 0);
                                                                    follow.put("user", ParseUser.getCurrentUser());
                                                                    follow.put("followingArray", list);
                                                                    follow.put("followersArray", list);
                                                                    follow.put("followers", 0);
                                                                    follow.put("following", 0);
                                                                    follow.put("likesRedeemed", 0);
                                                                    follow.save();

                                                                    final ParseObject last_visited = new ParseObject("Last_Visited");
                                                                    last_visited.put("user", ParseUser.getCurrentUser());
                                                                    last_visited.put("lastVisited", new Date());
                                                                    last_visited.put("videoCount", 0);
                                                                    last_visited.put("blogCount", 0);
                                                                    last_visited.save();

                                                                    //placing the user i would like to send the push notification to into the installation table in parse
                                                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                                                    installation.put("user", ParseUser.getCurrentUser());
                                                                    installation.remove("channels");
                                                                    final String currentUserIDChannel = "user_"+ ParseUser.getCurrentUser().getObjectId();
                                                                    installation.saveInBackground(new SaveCallback() {
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                Log.e("SaveTest", "Successful");
                                                                                ParsePush.subscribeInBackground(currentUserIDChannel);
                                                                            } else {
                                                                                //put a log message in here displaying e.getmessage()
                                                                                Log.e("SaveTest", e.getMessage());
                                                                            }
                                                                        }
                                                                    });

                                                                } catch (ParseException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                                Intent emailIntent = new Intent(context, MainActivity.class);
                                                                context.startActivity(emailIntent);
                                                                RegisterActivity registerActivity = new RegisterActivity();
                                                                registerActivity.finish();
                                                                Map<String, String> emailRegister = new HashMap<String, String>();
                                                                //param keys and values have to be of String type
                                                                emailRegister.put("Email Registration", "User registered with Email.");
                                                                //up to 10 params can be logged with each event
                                                                AppEventsLogger logger2 = AppEventsLogger.newLogger(context);
                                                                logger2.logEvent("Email Registration");                           FlurryAgent.logEvent("Email Registration", emailRegister);
                                                                break;
                                                            case PARSE_ID_TUTORIAL:

                                                                break;
                                                            case PARSE_ID_BLOG_ONE:
                                                                int points = ParseUser.getCurrentUser().getInt("points");
                                                                int newPoints = points + pointsAmount[0];
                                                                ParseUser.getCurrentUser().put("points", newPoints);
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                break;
                                                            case PARSE_ID_BLOG_TWO:
                                                                points = ParseUser.getCurrentUser().getInt("points");
                                                                newPoints = points + pointsAmount[0];
                                                                ParseUser.getCurrentUser().put("points", newPoints);
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                break;
                                                            case PARSE_ID_BLOG_FIVE:
                                                                points = ParseUser.getCurrentUser().getInt("points");
                                                                newPoints = points + pointsAmount[0];
                                                                ParseUser.getCurrentUser().put("points", newPoints);
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                break;
                                                            case PARSE_ID_VIDEO_ONE:
                                                                points = ParseUser.getCurrentUser().getInt("points");
                                                                newPoints = points + pointsAmount[0];
                                                                ParseUser.getCurrentUser().put("points", newPoints);
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                break;
                                                            case PARSE_ID_VIDEO_TWO:
                                                                points = ParseUser.getCurrentUser().getInt("points");
                                                                newPoints = points + pointsAmount[0];
                                                                ParseUser.getCurrentUser().put("points", newPoints);
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                break;
                                                            case PARSE_ID_VIDEO_FIVE:
                                                                points = ParseUser.getCurrentUser().getInt("points");
                                                                newPoints = points + pointsAmount[0];
                                                                ParseUser.getCurrentUser().put("points", newPoints);
                                                                ParseUser.getCurrentUser().saveInBackground();
                                                                break;

                                                        }
                                                        Log.e("points added", "success");
                                                    } else {
                                                        Log.e("failed", e.getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Log.e("failed", e.getMessage());
                                    }

                                }
                            });
                        }
                    });
                    likesDialog.show();
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private AlertDialog AskOption() {
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Display Name")
                .setMessage("Enter your desired username")
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String username = input_facebook.getText().toString();
                        if (username != "") {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                            query.whereEqualTo("username", username);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() == 0) {
                                            ParseUser.getCurrentUser().put("username", username);
                                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Intent intent = new Intent(context, MainActivity.class);
                                                        context.startActivity(intent);
                                                        logins.finish();
                                                        dialog.dismiss();
                                                    } else {
                                                        Log.e("failed", "failed" + e.getMessage());
                                                    }
                                                }
                                            });
                                        } else {
                                            input_facebook.setError("Username already exists");
                                        }
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            input_facebook.setError("Username cannot be empty.");
                        }
                    }
                })
                .create();
    }

    private void getDescription() {
        switch (dialogID) {
            case PARSE_ID_BLOG_ONE:
                description = "You read 1 Blog Article today earning you:";
                break;
            case PARSE_ID_BLOG_TWO:
                description = "You read 2 Blog Articles today earning you:";
                break;
            case PARSE_ID_BLOG_FIVE:
                description = "You read 5 Blog Articles today earning you:";
                break;
            case PARSE_ID_DAILY_LOGIN:
                description = "You have logged in today earning you:";
                break;
            case PARSE_ID_GOOGLE:
                description = "You have registered via Google and earned:";
                break;
            case PARSE_ID_EMAIL:
                description = "You have registered via Email and earned:";
                break;
            case PARSE_ID_FB:
                description = "You have registered via Facebook and earned:";
                break;
            case PARSE_ID_VIDEO_ONE:
                description = "You have watched 1 Premier video today earning you:";
                break;
            case PARSE_ID_VIDEO_TWO:
                description = "You have watched 2 Premier videos today earning you:";
                break;
            case PARSE_ID_VIDEO_FIVE:
                description = "You have watched 5 Premier videos today earning you:";
                break;
            case PARSE_ID_STREET_STYLE_UPLOAD:
                description = "You have uploaded an image to Street Style earning you:";
                break;
            case PARSE_ID_TUTORIAL:
                description = "You completed the Street Style tutorial, earning you:";
                break;
        }
    }

}
