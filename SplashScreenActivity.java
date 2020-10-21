package icn.premierandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.parse.ParseUser;
import com.tapjoy.TJConnectListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Launcher Activity
 */

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
//        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        Tapjoy.connect(getApplicationContext(), "kt6AAIJ7T3CqxF3HGEBNBQECDCrOitwN0AgECCHJVgXXDf8Z1K10B6jPXy0m", null, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                Log.e("Hello", "succees");
//              SplashScreenActivity.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
//                SplashScreenActivity.this.onConnectFail();
            }
        });

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo("icn.premierandroid", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("failed", e.getMessage());
        }

        if (isNetworkAvailable(SplashScreenActivity.this)) {
            int SPLASH_DISPLAY_LENGTH = 2000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ParseUser user = ParseUser.getCurrentUser();
                    if (user != null) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, AllLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);

        } else if (!isNetworkAvailable(SplashScreenActivity.this)){
            final Dialog d = new Dialog(SplashScreenActivity.this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.custom_alert);// custom layour for dialog.
            d.setCancelable(false);
            Button closeApp = (Button) d.findViewById(R.id.customAlertDialogCancel);
            Button settings = (Button) d.findViewById(R.id.customAlertDialogOk);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            closeApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    System.exit(0);
                }
            });
            d.show();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    Log.i("Class", anInfo.getState().toString());
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}