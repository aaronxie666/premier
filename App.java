package icn.premierandroid;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kochava.android.tracker.Feature;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.HashMap;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Application Parent where initilization of libraries occur
 */

public class App extends Application {
    public Context mContext;
    public static final String CLIENT_ID = "61479d4e471547a8acb7ac035b8fb390";
    public static final String CLIENT_SECRET = "0f31b2ea3cb3451b9ae138a5a2c45007";
    public static final String CALLBACK_URL = "http://localhost";
    private GoogleApiClient mGoogleApiClient;

    public void onCreate() {
        super.onCreate();

        mContext = this;
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(new Parse.Configuration.Builder(getContext())
                .applicationId("Yc5S9IGsXNqmED3GilTQF4yPgsdbLSw5SnJGeECx")
                .clientKey("ur3RCYpho9nIifEeKJGb4a3NmXc9cto7b5mwL6nh")
                .server("https://pg-app-bb7xwwox3h9lw5qeqxdkegebkbhs2b.scalabl.cloud/1/")
        .build()
        );

        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
        // Save the current Installation to Parse.
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        if (installation == null) {
            installation = new ParseInstallation();
        } else {
            installation = ParseInstallation.getCurrentInstallation();
        }
        installation.saveInBackground();
        JodaTimeAndroid.init(this);
        HashMap<String, Object> datamap = new HashMap<>();
        datamap.put(Feature.INPUTITEMS.KOCHAVA_APP_ID ,"kopremier-model-style-536" );
        datamap.put(Feature.INPUTITEMS.CURRENCY , "USD");
        new Feature(getApplicationContext(), datamap);

        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

        HashMap<String, String> identityLinkMap = new HashMap<>();
        identityLinkMap.put("yourKey", "yourValue");
        datamap.put(Feature.INPUTITEMS.IDENTITY_LINK , identityLinkMap);

        String FLURRY_KEY = "FK688HS63KGKJS6TQ4XB";
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withListener(new FlurryAgentListener() {
                    @Override
                    public void onSessionStarted() {
                        // Capture author info & user status
                    }
                })
                .build(this, FLURRY_KEY);

        AppEventsLogger.activateApp(this);
    }

    public Context getContext(){
        return mContext;
    }
}
