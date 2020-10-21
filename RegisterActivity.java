package icn.premierandroid;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import icn.premierandroid.misc.LikesDialog;
import icn.premierandroid.misc.TextValidator;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_EMAIL;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: When user presses register button in AllLoginActivity
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, confirmEmail, password, confirmPassword, username;
    private Switch ageSwitch;
    boolean validated = true;
    final Context context = this;
    private Boolean validAge = true;
    private ArrayList<JSONObject> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        AssetManager am = context. getApplicationContext(). getAssets();
//        Typeface font = Typeface.createFromAsset(
//                am,
//                String.format(Locale.US, "fonts/%s", "portrait-light.ttf"));

        name = (EditText) findViewById(R.id.et_register_name);
//        name.setTypeface(font);
        email = (EditText) findViewById(R.id.et_register_email);
//        email.setTypeface(font);
        username = (EditText) findViewById(R.id.et_register_username);
//        username.setTypeface(font);
        confirmEmail = (EditText) findViewById(R.id.et_register_confirm_email);
//        confirmEmail.setTypeface(font);
        password = (EditText) findViewById(R.id.et_register_password);
//        password.setTypeface(font);
        confirmPassword = (EditText) findViewById(R.id.et_register_confirm_password);
//        confirmPassword.setTypeface(font);
        ageSwitch = (Switch) findViewById(R.id.mySwitch);
        TextView switch_description = (TextView) findViewById(R.id.switch_description);
//        switch_description.setTypeface(font);

        /*
        *  Real time name validation
        *  Will allow all alphabetical letters + white space
        */

        name.addTextChangedListener(new TextValidator(name) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidName(name.getText().toString())) {
                    name.setError("Invalid Name");
                }
            }
        });

        username.addTextChangedListener(new TextValidator(username) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidUsername(username.getText().toString())) {
                    username.setError("Invalid Username");
                }
            }
        });

        /*
        *  Real time email validation
        *  Will allow all alphanumerical letters, '.' and '@'
        */

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidEmail(email.getText().toString())) {
                        email.setError("Invalid Email");
                    }
                }
            }
        });

        confirmEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidEmail(confirmEmail.getText().toString())) {
                        confirmEmail.setError("Invalid Email");
                    }
                }
            }
        });

        /*
        *  Real time password validation
        *  Will allow only a password with 1 uppercase letter, 1 lowercase letter,
        *  1 numeric and must be longer than 5 characters
        */

        password.addTextChangedListener(new TextValidator(password) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidPassword(password.getText().toString())) {
                    password.setError("Password must contain atleast 1 Uppercase letter, 1 Lowercase letter, 1 Number and longer than 6 characters.");
                }
            }
        });

        confirmPassword.addTextChangedListener(new TextValidator(confirmPassword) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidPassword(confirmPassword.getText().toString())) {
                    confirmPassword.setError("Password must contain atleast 1 Uppercase letter, 1 Lowercase letter, 1 Number and longer than 6 characters.");
                }
            }
        });

        /*
         * Listens for user interaction on the registration button
         */
        ImageButton register_button = (ImageButton) findViewById(R.id.register_button);
        register_button.setOnClickListener(customListener);

        /*
         * Listens for user interaction on the back_button
         * Brings the view forward to be on top of the Premier Logo drawable
         */
        ImageButton back_button = (ImageButton) findViewById(R.id.back_btn);
        back_button.bringToFront();
        back_button.setOnClickListener(customListener);
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_btn:
                    Intent l = new Intent(getApplicationContext(), AllLoginActivity.class);
                    RegisterActivity.this.startActivity(l);
                    finish(); // Always call the superclass
                    break;
                case R.id.register_button:
                    validateRegistration(validated);
                    if (validated) {
                        completeRegistration();
                    }

                    break;
            }
        }
    };

    private boolean validateRegistration(boolean validCount) {
        EditText emailAddress = (EditText) findViewById(R.id.et_register_email);
        EditText confirmEmailAddress = (EditText) findViewById(R.id.et_register_confirm_email);
        EditText passwordEditText = (EditText) findViewById(R.id.et_register_password);
        EditText confirmPasswordEditText = (EditText) findViewById(R.id.et_register_confirm_password);

        final String email = emailAddress.getText().toString();
        final String confirmEmail = confirmEmailAddress.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String confirmPassword =  confirmPasswordEditText.getText().toString();

        if (!email.equalsIgnoreCase(confirmEmail)) {
            confirmEmailAddress.setError("Emails provided do not match each other");
            validCount = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords provided do not match each other");
            validCount = false;
        }

        return validCount;
    }

    public void completeRegistration() {
        /** Adds a new user / gets current user **/
        final ParseUser user = new ParseUser();
        /**Adds a new username**/
        EditText confirmEmailAddress = (EditText) findViewById(R.id.et_register_confirm_email);
        final String emailAddress = confirmEmailAddress.getText().toString();
        final String name_string = name.getText().toString();
        final String user_name = username.getText().toString();

        user.setUsername(user_name);
        user.put("userEmail", emailAddress);
        user.put("email", emailAddress);
        user.put("posts", 0);
        user.put("name", name_string);
        user.put("profilePicture", "");
        user.put("uniqueID", emailAddress);
        user.put("blogsRead", list);
        user.put("isAndroid", true);
        user.put("description", "Welcome to my Street Style profile, check out the pictures I have uploaded.");

        /** Adds a new password **/
        EditText confirmPasswordEditText = (EditText) findViewById(R.id.et_register_confirm_password);
        String password =  confirmPasswordEditText.getText().toString();
        user.setPassword(password);

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
            user.put("over18", true);
        }

        else {
            user.put("over18", false);
        }
        /** Add login method **/
        user.put("loginMethod", "Email");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    LikesDialog likesDialog = new LikesDialog(context, PARSE_ID_EMAIL);
                    likesDialog.display();
                } else {
                    switch(e.getCode()) {
                        case ParseException.USERNAME_TAKEN:
                            Toast.makeText(context,"Sorry, this username has already been taken.", Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.USERNAME_MISSING:
                            Toast.makeText(context,"Sorry, a username is needed", Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.PASSWORD_MISSING:
                            Toast.makeText(context,"Sorry, a password is needed.", Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.OBJECT_NOT_FOUND:
                            Toast.makeText(context,"invalid credentials",  Toast.LENGTH_SHORT).show();
                            break;
                        case ParseException.CONNECTION_FAILED:
                            Toast.makeText(context, "Sorry, internet is needed.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(RegisterActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
        });


    }

    private boolean isValidName(String name) {
        String NAME_PATTERN = "^[a-zA-Z- ]+$";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidUsername(String username) {
        String USERNAME_PATTERN = "^[a-zA-Z0-9_]*$";

        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{6,45}$";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent l = new Intent(getApplicationContext(), AllLoginActivity.class);
        RegisterActivity.this.startActivity(l);
        finish(); // Always call the superclass
    }
}
