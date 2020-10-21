package icn.premierandroid.misc;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import icn.premierandroid.App;
import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.fragments.HomeFragment;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Gets details of email and sends in the background
 */

@SuppressWarnings("rawtypes")
public class SendMailTask extends AsyncTask {
    private ProgressDialog statusDialog;
    private Activity sendMailActivity;
    private boolean isDiscount;

    public SendMailTask(Activity activity, boolean isDiscount) {
        sendMailActivity = activity;
        this.isDiscount = isDiscount;
    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        if (!isDiscount) {
            statusDialog.show();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Void doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
            publishProgress("Processing input....");
            GMail androidEmail = new GMail(args[0].toString(),
                    args[1].toString(),  args[2].toString(), args[3].toString(),
                    args[4].toString(), args[5].toString(), args[6].toString(), isDiscount);
            publishProgress("Sending Email....");
            androidEmail.createEmailMessage();
            publishProgress("Email Sent.");
            androidEmail.sendEmail();
            Log.i("SendMailTask", "Mail Sent.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage("Sending Email...");

    }

    @Override
    public void onPostExecute(Object result) {
        statusDialog.dismiss();
    }


}