package icn.premierandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.List;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: When user presses login button in AllLoginActivity
 */

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = (EditText) findViewById(R.id.et_login_email);
        et_password = (EditText) findViewById(R.id.et_login_password);

        ImageButton back_button = (ImageButton) findViewById(R.id.back_btn);
        back_button.setOnClickListener(customListener);

        ImageView loginButton = (ImageView) findViewById(R.id.login_login_btn);
        loginButton.setOnClickListener(customListener);

        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(customListener);

    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.back_btn:
                    Intent l = new Intent(getApplicationContext(), AllLoginActivity.class);
                    LoginActivity.this.startActivity(l);
                    finish(); // Always call the superclass
                    break;
                case R.id.login_login_btn:
                    // Retrieve the text entered from the EditText
                    final String[] username = {et_email.getText().toString()};
                    final String password = et_password.getText().toString();
                    System.out.println("--------------------------------------@"+et_email.getText().toString()+"@---------------------------------" );

                    if(username[0].trim().length() != 0){
                        if(password.trim().length() !=0){
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("email", username[0]);
                            query.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.isEmpty()) {
                                            Toast.makeText(LoginActivity.this, "Invalid Email or Password. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                        if (objects.size() == 1) {
                                            username[0] = objects.get(0).getUsername();
                                            // Send data to Parse.com for verification
                                            ParseUser.logInInBackground(username[0], password,
                                                    new LogInCallback() {
                                                        public void done(ParseUser user, com.parse.ParseException e) {
                                                            if(e==null){
                                                                if (user != null) {
                                                                    // If user exist and authenticated, send user to Welcome.class
                                                                    Intent intent = new Intent(
                                                                            LoginActivity.this,
                                                                            MainActivity.class);
                                                                    startActivity(intent);
                                                                    Toast.makeText(getApplicationContext(),
                                                                            "Successfully Logged in",
                                                                            Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(
                                                                            getApplicationContext(),
                                                                            "No such user exist, please signup",
                                                                            Toast.LENGTH_LONG).show();
                                                                }
                                                            }else {
                                                                Toast.makeText(LoginActivity.this, "Worng password!", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Email doesn't exist. Please enter a valid email.", Toast.LENGTH_SHORT).show();
                                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                        }
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(LoginActivity.this, "Please input Password", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(LoginActivity.this, "Please input Username", Toast.LENGTH_SHORT).show();
                    }




                    break;
                case R.id.forgot_password:
                    displayLostPasswordDialog();
                    break;
            }
        }
    };

    private void displayLostPasswordDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setTitle("Reset Password");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgot_password, null);
        dialogBuilder.setView(view);
        final EditText change_password_input = (EditText) view.findViewById(R.id.reset_pass_email_input);
        change_password_input.setTextColor(Color.BLACK);
        dialogBuilder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String sentEmail = change_password_input.getText().toString();
                ParseUser.requestPasswordResetInBackground(sentEmail, new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "An email has been sent to your account, \n" +
                                            "please follow as instructed to reset your password",
                                    Toast.LENGTH_LONG).show();
                            // An email was successfully sent with reset instructions.
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Email: " + sentEmail + " does not exist\n" +
                                            "please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog forgotPassword = dialogBuilder.create();
        forgotPassword.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                forgotPassword.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                forgotPassword.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        forgotPassword.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent l = new Intent(getApplicationContext(), AllLoginActivity.class);
        LoginActivity.this.startActivity(l);
        finish(); // Always call the superclass
    }

}
