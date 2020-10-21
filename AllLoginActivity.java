package icn.premierandroid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.CustomFontEditText;
import icn.premierandroid.misc.InstagramApp;
import icn.premierandroid.misc.LikesDialog;
import icn.premierandroid.misc.TextValidator;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_FB;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_GOOGLE;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: All Login options are provided in this class (Facebook, Instagram, Register, Login)
 */

public class AllLoginActivity extends AppCompatActivity {
    final List<String> pPermissions = Arrays.asList("public_profile", "email", "user_birthday", "user_friends");
    private String email, name, age, profilePicURL;
    private Context context = this;
    public static UserType.userType userType;
    private int RC_SIGN_IN = 1000;
    private InstagramApp mApp;
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                HashMap<String, String> userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(AllLoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });
    private Switch ageSwitch;
    private Boolean validAge = true;
    private String email_insta;
    private EditText email_address;
    private ArrayList<JSONObject> list = new ArrayList<>();
    private EditText input_instagram, input_facebook;
    private ArrayList<String> uniqueIds = new ArrayList<>();
    private ParseUser currentUser;
    private Dialog anonSkipDialog;
    private GoogleApiClient mGoogleApiClient;
    private boolean google = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_logins);

        ImageButton facebookButton = (ImageButton) findViewById(R.id.fb_login);
        facebookButton.setOnClickListener(customListener);

        ImageButton instagramButton = (ImageButton) findViewById(R.id.insta_login1);
        instagramButton.setOnClickListener(customListener);

        ImageButton loginButton = (ImageButton) findViewById(R.id.normal_login);
        loginButton.setOnClickListener(customListener);

        ImageButton registerButton = (ImageButton) findViewById(R.id.register_button);
        registerButton.setOnClickListener(customListener);

        ImageButton googleButton = (ImageButton) findViewById(R.id.btn_google);
        googleButton.setOnClickListener(customListener);

        ImageButton skipButton = (ImageButton) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(customListener);

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("Failed", "failed" + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mApp = new InstagramApp(this, App.CLIENT_ID,
                App.CLIENT_SECRET, App.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                userType = UserType.userType.instagramUser;
                mApp.fetchUserName(handler);
                currentUser = new ParseUser();
                if (currentUser != null) {
                    final String[] name = new String[1];
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("uniqueID", mApp.getId());
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() == 1) {
                                name[0] = objects.get(0).getString("username");
                                ParseUser.logInInBackground(name[0], mApp.getId(), new LogInCallback() {
                                    @Override
                                    public void done(ParseUser user, ParseException e) {
                                        if (e == null) {
                                            Intent u = new Intent(AllLoginActivity.this, MainActivity.class);
                                            context.startActivity(u);
                                            finish();
                                        } else {
                                            Log.e("login failed", "login failed");
                                        }
                                    }
                                });
                            } else {
                                showLikesDialog(true);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFail(String error) {
                Log.e("failed", error);
            }
        });
    }

    public void saveUserDetailsToParse(final int i, final String instagram_username) {
//            final ParseUser currentUser = new ParseUser();
//            if (currentUser != null) {
//                if (email_insta != "" || email_insta != null) {
//                    currentUser.setUsername(mApp.getName());
//                    currentUser.setEmail(email_address.getText().toString());
//                    currentUser.put("userEmail", email_address.getText().toString());
//                }
//                currentUser.put("name", mApp.getName());
//                currentUser.put("loginMethod", "Instagram");
//                currentUser.put("profilePicture", mApp.getProfilePicture());
//                currentUser.put("points", i);
//                currentUser.put("posts", 0);
//                currentUser.put("followers", 0);
//                currentUser.put("following", 0);
//                currentUser.put("uniqueID", mApp.getId());
//                currentUser.put("blogsRead", list);
//                currentUser.put("description", "Welcome to my Street Style profile, check out the pictures I have uploaded.");
//                currentUser.setPassword(mApp.getId());
//
                /** Validate age **/
                ageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            validAge = false;
                        }
                    }
                });

                /** If over 18 valid age is true, if under valid age is false **/
                if (validAge) {
                    currentUser.put("over18", true);
                } else if (!validAge) {
                    currentUser.put("over18", false);
                }

//                ParseQuery<ParseUser> query = ParseUser.getQuery();
//                query.whereEqualTo("uniqueID", mApp.getId());
//                query.findInBackground(new FindCallback<ParseUser>() {
//                    @Override
//                    public void done(List<ParseUser> objects, ParseException e) {
//
//                        for (ParseUser j : objects) {
//
//                        }
//                        if (objects.size() > 0) {
//                            Log.e("objects.size()", objects.size() + "");
//                            ParseUser.logInInBackground(instagram_username, mApp.getId(), new LogInCallback() {
//                                @Override
//                                public void done(ParseUser user, ParseException e) {
//                                    if (e == null) {
//                                        Intent u = new Intent(AllLoginActivity.this, MainActivity.class);
//                                        context.startActivity(u);
//                                        finish();
//                                    } else {
//                                        Log.e("login failed", "login failed");
//                                    }
//                                }
//                            });
//                        } else {
                             if (email_insta != "" || email_insta != null) {
                                 currentUser.setUsername(instagram_username);
                                 currentUser.setEmail(email_address.getText().toString());
                                 currentUser.put("userEmail", email_address.getText().toString());
                             }
                            currentUser.setEmail(email_address.getText().toString());
                            currentUser.put("userEmail", email_address.getText().toString());
                            currentUser.put("points", i);
                            currentUser.put("name", mApp.getName());
                            currentUser.put("loginMethod", "Instagram");
                            currentUser.put("profilePicture", mApp.getProfilePicture());
                            currentUser.put("posts", 0);
                            currentUser.put("followers", 0);
                            currentUser.put("following", 0);
                            currentUser.put("uniqueID", mApp.getId());
                            currentUser.put("blogsRead", list);
                            currentUser.put("isAndroid", true);
                            currentUser.put("description", "Welcome to my Street Style profile, check out the pictures I have uploaded.");
                            currentUser.setPassword(mApp.getId());
                            currentUser.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.e("SaveTest", "Successful");
                                        Log.d("first time", "your name is" + name + "Your email is" + email + "your age range is" + age);
                                        try {
                                            final ParseObject follow = new ParseObject("Follow");
                                            ArrayList<JSONObject> list = new ArrayList<>();
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
                                            final String currentUserIDChannel = "user_" + ParseUser.getCurrentUser().getObjectId();
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
                                    } else {
                                        switch (e.getCode()) {
                                            case ParseException.USERNAME_TAKEN:
                                                Toast.makeText(context, "Sorry, this username has already been taken.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case ParseException.USERNAME_MISSING:
                                                Toast.makeText(context, "Sorry, a username is needed", Toast.LENGTH_SHORT).show();
                                                break;
                                            case ParseException.PASSWORD_MISSING:
                                                Toast.makeText(context, "Sorry, a password is needed.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case ParseException.OBJECT_NOT_FOUND:
                                                Toast.makeText(context, "invalid credentials", Toast.LENGTH_SHORT).show();
                                                break;
                                            case ParseException.CONNECTION_FAILED:
                                                Toast.makeText(context, "Sorry, internet is needed.", Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                Log.d("Testing", e.getLocalizedMessage());
                                                break;
                                        }
                                    }
                                }
                            });
//                        }
//                    }
//                });

//            } else {
//                Log.e("user", "is null");
//            }
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.fb_login:
                    Log.d("before utils call", "before utils calls");
                    ParseFacebookUtils.logInWithReadPermissionsInBackground((Activity) context, pPermissions, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if(err == null){
                                Log.d("before if call", "before if calls");
                                if (user == null) {
                                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                } else if (user.isNew()) {
                                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                                    getUserDetailFromFB();
                                } else {
                                    System.out.println("------------------------------------------------------------------------------------------------------------------------------------"+ user.getUsername());
                                    Log.d("MyApp", "User logged in through Facebook!");
                                    getUserDetailFromParse();
                                }
                            }else{
                                Log.e("MyApp", "Error: " + err.getLocalizedMessage());
                            }

                        }
                    });
                    break;
                case R.id.insta_login1:
                    connectOrDisconnectUser();
                    break;
                case R.id.normal_login:
                    Intent u = new Intent(AllLoginActivity.this, LoginActivity.class);
                    AllLoginActivity.this.startActivity(u);
                    finish(); // Always call the superclass
                    break;
                case R.id.register_button:
                    Intent r = new Intent(AllLoginActivity.this, RegisterActivity.class);
                    AllLoginActivity.this.startActivity(r);
                    finish(); // Always call the superclass
                    break;
                case R.id.skip_button:
                    userType = UserType.userType.unsignedUser;
                    showSkipDialog();
                    break;
                case R.id.btn_google:
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    break;
            }
        }
    };

    private void showSkipDialog() {
        anonSkipDialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog);
        anonSkipDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        anonSkipDialog.setContentView(R.layout.activity_login_skip_button_dialog);

        ImageButton facebookButton = (ImageButton) anonSkipDialog.findViewById(R.id.fb_login);
        facebookButton.setOnClickListener(customListener);

        ImageButton instagramButton = (ImageButton) anonSkipDialog.findViewById(R.id.insta_login1);
        instagramButton.setOnClickListener(customListener);

        ImageButton loginButton = (ImageButton) anonSkipDialog.findViewById(R.id.normal_login);
        loginButton.setOnClickListener(customListener);

        ImageButton registerButton = (ImageButton) anonSkipDialog.findViewById(R.id.register_button);
        registerButton.setOnClickListener(customListener);

        ImageButton btnGoogle = (ImageButton) anonSkipDialog.findViewById(R.id.btn_google);
        btnGoogle.setOnClickListener(customListener);

        ImageButton skipButton = (ImageButton) anonSkipDialog.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l = new Intent(AllLoginActivity.this, MainActivity.class);
                AllLoginActivity.this.startActivity(l);
                finish(); // Always call the superclass
            }
        });
        
        anonSkipDialog.show();
    }

    private void connectOrDisconnectUser() {
                mApp.authorize();
    }


    private void showLikesDialog(final boolean instagram) {
        if (!instagram) {
            if (!google) {
                LikesDialog facebookLikesDialog = new LikesDialog(context, PARSE_ID_FB);
                facebookLikesDialog.display();
            } else {
                LikesDialog googleLikesDialog = new LikesDialog(context, PARSE_ID_GOOGLE);
                googleLikesDialog.display();
            }
        } else {
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);
            LinearLayout instagram_likes_container = (LinearLayout) dialog.findViewById(R.id.five_hundred_likes_layout);
            Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOkLogin);

            final String desc;
            instagram_likes_container.setVisibility(View.VISIBLE);
            desc = "You have registered via Instagram and earned:";

            TextView description = (TextView) dialog.findViewById(R.id.tvDescription);
            description.setText(desc);
            // Click cancel to dismiss android custom dialog box
            dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        showInstagramDialog();
                        dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void showInstagramDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.instagram_dialog);
        Button dialogButtonSubmit = (Button) dialog.findViewById(R.id.customDialogSubmit);
        Button dialogButtonSkip = (Button) dialog.findViewById(R.id.customDialogSkip);
        email_address = (EditText) dialog.findViewById(R.id.email_edit_insta);
        ageSwitch = (Switch) dialog.findViewById(R.id.insta_switch);

//        email_address.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if(s.toString().length() > 0) {
//                    email_address.setBackgroundColor(Color.WHITE);
//                } else {
//                    //Assign your image again to the view, otherwise it will always be gone even if the text is 0 again.
//                    email_address.setBackgroundResource(R.drawable.textbox_enter_email);
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                    if(s.toString().length() > 0) {
//                        email_address.setBackgroundColor(Color.WHITE);
//                    } else {
//                        //Assign your image again to the view, otherwise it will always be gone even if the text is 0 again.
//                        email_address.setBackgroundResource(R.drawable.textbox_enter_email);
//                    }
//            }
//        });
        // Click cancel to dismiss android custom dialog box

        input_instagram = new CustomFontEditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(20, 0, 20, 0);
        input_instagram.setLayoutParams(lp);
        input_instagram.setBackgroundResource(R.drawable.edit_text_border);
        dialogButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_address.addTextChangedListener(new TextValidator(email_address) {
                    @Override
                    public void validate(TextView textView, String text) {
                        if (!isValidEmail(email_address.getText().toString())) {
                            email_address.setError("Invalid Email");
                        }
                    }
                });
                final AlertDialog diaBox = AskOption("submit");
                diaBox.setCancelable(false);
                diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    }
                });
                diaBox.setView(input_instagram);
                diaBox.show();
            }
        });

        // Your android custom dialog ok action
        // Action for custom dialog ok button click
        dialogButtonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog diaBox = AskOption("skip");
                diaBox.setCancelable(false);
                diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    }
                });
                diaBox.setView(input_instagram);
                diaBox.show();
            }
        });
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anonSkipDialog != null) {
            anonSkipDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (anonSkipDialog != null) {
            anonSkipDialog.dismiss();
        }
    }

    private AlertDialog AskOption(final String type)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Display Name")
                .setMessage("Enter your desired username")
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        switch(type) {
                            case "skip":
                                String instagram_username_skip = input_instagram.getText().toString();
                                Intent intent = new Intent(AllLoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                saveUserDetailsToParse(500, instagram_username_skip);
                                break;
                            case "submit":
                                String instagram_username_submit = input_instagram.getText().toString();
                                Intent intent1 = new Intent(AllLoginActivity.this, MainActivity.class);
                                startActivity(intent1);
                                finish();
                                saveUserDetailsToParse(1000, instagram_username_submit);
                                break;
                        }
                        dialog.dismiss();
                    }

                })
                .create();
        return myQuittingDialogBox;

    }

    private void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    name = object.getString("name");
                    Log.d("name", object.getString("name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    email = object.getString("email");
                    Log.d("email",object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                } finally {
                    final AlertDialog diaBox = invalidEmailFacebook();
                    diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                            diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        }
                    });
                    diaBox.show();
                }
                try{
                    age = object.getString("age_range");
                    Log.d("age_range", object.getString("age_range"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    String facebookID = object.getString("id");
                    profilePicURL = "http://graph.facebook.com/"+facebookID+"/picture?type=large";
                    Log.d("profile_url", object.getJSONObject("picture").getString("url"));
                }catch(JSONException e){
                    e.printStackTrace();
                }

                input_facebook= new CustomFontEditText(AllLoginActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(20, 0, 20, 0);
                input_facebook.setLayoutParams(lp);
//                final AlertDialog diaBox = AskOption("facebook");
//                diaBox.setCancelable(false);
//                diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface dialog) {
//                        diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
//                        diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
//                    }
//                });
//                diaBox.setView(input_facebook);
//                diaBox.show();
                if (email != null) {
                    saveNewUser();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,age_range,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void saveNewUser(){
        final ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(email);
        user.setEmail(email);
        user.put("userEmail", email);
        user.put("uniqueID", email);
        user.put("name", name);
        user.put("loginMethod", "Facebook");
        user.put("profilePicture", profilePicURL);
        user.put("posts", 0);
        user.put("blogsRead", list);
        user.put("isAndroid", true);
        user.put("description", "Welcome to my Street Style profile, check out the pictures I have uploaded.");
        Log.d("AGE VERIFICATION", "the age is" + age);
        if (age.equals("{\"max\":17,\"min\":13}")){
            user.put("over18", false);
        }else{
            user.put("over18", true);
        }
        if (email != null) {
            user.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.e("SaveTest", "Successful");
                        showLikesDialog(false);
                        try {
                            final ParseObject follow = new ParseObject("Follow");
                            ArrayList<JSONObject> list = new ArrayList<>();
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

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.remove("channels");
                            final String currentUserIDChannel = "user_" + ParseUser.getCurrentUser().getObjectId();
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
                    } else {
                        switch (e.getCode()) {
                            case ParseException.USERNAME_TAKEN:
                                Toast.makeText(context, "Sorry, this username has already been taken.", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.USERNAME_MISSING:
                                Toast.makeText(context, "Sorry, a username is needed", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.PASSWORD_MISSING:
                                Toast.makeText(context, "Sorry, a password is needed.", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.OBJECT_NOT_FOUND:
                                Toast.makeText(context, "invalid credentials", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.CONNECTION_FAILED:
                                Toast.makeText(context, "Sorry, internet is needed.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.d("Testing", e.getLocalizedMessage());
                                break;
                        }
                    }
                }
            });
        }
    }

    void getUserDetailFromParse(){
        ParseUser user = ParseUser.getCurrentUser();
        userType = UserType.userType.facebookUser;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        Log.d("username", "" + user.getUsername());
        Log.d("email", "" +  user.getUsername());
        Log.d("profile_pic", "" + user.getString("profilePicture"));
        Log.d("loginMethod", "" + user.getString("loginMethod"));
        Log.d("age", "" + String.valueOf(user.getBoolean("over18")));
        Log.d("Welcome Back","User:"+ user.getUsername() + "email:"+ user.getEmail());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("handleSignIn", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                query.setLimit(10000);
                query.whereEqualTo("email", acct.getEmail());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() == 0) {
                                saveNewUserGoogle(acct);
                            } else {
                                loginGoogleUser(objects.get(0), acct);
                            }
                        } else {
                            saveNewUserGoogle(acct);
                        }
                    }
                });
            }
        } else {
            Log.e("failed", "failed to sign in");
            // Signed out, show unauthenticated UI.
        }
    }

    private void loginGoogleUser(ParseObject j, GoogleSignInAccount acct) {
       ParseUser.logInInBackground(j.getString("username"), String.valueOf(acct.getId()), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Intent i = new Intent(AllLoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.e("failed", "could not be validated");
                }
            }
        });
    }

    private void saveNewUserGoogle(GoogleSignInAccount acct) {
        google = true;
        final ParseUser user = new ParseUser();
        String mFullName = acct.getDisplayName();
        String mEmail = acct.getEmail();
        String mProfilePic = String.valueOf(acct.getPhotoUrl());
        String mUsername = acct.getId();
        String password = acct.getId();
        user.setUsername(mUsername);
        user.setEmail(mEmail);
        user.setPassword(password);
        user.put("userEmail", mEmail);
        user.put("uniqueID", mUsername);
        user.put("name", mFullName);
        user.put("loginMethod", "Google");
        user.put("profilePicture", mProfilePic);
        user.put("posts", 0);
        user.put("blogsRead", list);
        user.put("isAndroid", true);
        user.put("description", "Welcome to my Street Style profile, check out the pictures I have uploaded.");
        user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.e("SaveTest", "Successful");
                        showLikesDialog(false);
                        try {
                            final ParseObject follow = new ParseObject("Follow");
                            ArrayList<JSONObject> list = new ArrayList<>();
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

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", ParseUser.getCurrentUser());
                            installation.remove("channels");
                            final String currentUserIDChannel = "user_" + ParseUser.getCurrentUser().getObjectId();
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
                    } else {
                        switch (e.getCode()) {
                            case ParseException.USERNAME_TAKEN:
                                Toast.makeText(context, "Sorry, this username has already been taken.", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.USERNAME_MISSING:
                                Toast.makeText(context, "Sorry, a username is needed", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.PASSWORD_MISSING:
                                Toast.makeText(context, "Sorry, a password is needed.", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.OBJECT_NOT_FOUND:
                                Toast.makeText(context, "invalid credentials", Toast.LENGTH_SHORT).show();
                                break;
                            case ParseException.CONNECTION_FAILED:
                                Toast.makeText(context, "Sorry, internet is needed.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.d("Testing", e.getLocalizedMessage());
                                break;
                        }
                    }
                }
            });

    }


    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void setEmail_insta(String email_insta) {
        this.email_insta = email_insta;
    }

    private AlertDialog invalidEmailFacebook()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(AllLoginActivity.this)
                //set message, title, and icon
                .setTitle("Email")
                .setMessage("Please assign an Email to your Facebook Account. You will need to register via Instagram or Email for the time being. Contact Brad@icn-apps.com if the error persists.")
                .setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.exit(0);
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }
}
