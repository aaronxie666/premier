package icn.premierandroid.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.TextValidator;
import icn.premierandroid.misc.SendMailTask;
import icn.premierandroid.models.ViewPagerModel;

import static android.content.Context.MODE_PRIVATE;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Next button press in ScoutMeGenderUploadImageFragment
 */

public class ScoutMeUserDetailsFragment extends Fragment {
    private SharedPreferences prefs;
    private EditText name, hometown, age, dob, email, phone, twitter, instagram;
    private String fileHeadPath, fileFullPath;
    public ViewPagerModel model;
    private ViewPager viewPager;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scout_me_user_details, container, false);
        prefs = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String gender = prefs.getString("Gender", null);
        String height = prefs.getString("Height", null);
        fileHeadPath = getArguments().getString("Head_Image", null);
        fileFullPath = getArguments().getString("Full_Image", null);
        Log.e("User Details", "Gender is: " + gender + " Height is : " + height + "Head File Path is: " + fileHeadPath + "Full File Path is: " + fileFullPath);
        ImageButton next_btn = (ImageButton) rootView.findViewById(R.id.btn_next);
        next_btn.setOnClickListener(customListener);

        name = (EditText) rootView.findViewById(R.id.name_et);
        age = (EditText) rootView.findViewById(R.id.age_et);
        hometown = (EditText) rootView.findViewById(R.id.hometown_et);
        dob = (EditText) rootView.findViewById(R.id.age_dob);
        email = (EditText) rootView.findViewById(R.id.email_et);
        phone = (EditText) rootView.findViewById(R.id.phone_et);
        twitter = (EditText) rootView.findViewById(R.id.twitter_et);
        instagram = (EditText) rootView.findViewById(R.id.instagram_et);

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

        /*
        *  Real time age validation
        *  Will allow only Numerical values
        */

        age.addTextChangedListener(new TextValidator(age) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidAge(age.getText().toString())) {
                    age.setError("Invalid Age");
                }
            }
        });

        /*
        *  Real time Hometown validation
        *  Will allow all alphabetical letters + white space
        */

        hometown.addTextChangedListener(new TextValidator(hometown) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidName(hometown.getText().toString())) {
                    hometown.setError("Invalid Hometown");
                }
            }
        });

        /*
        * Real time date of birth validation
        * Will allow only D.O.B Syntax representing DD/MM/YYYY
        */

        dob.addTextChangedListener(new TextValidator(dob) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidDateOfBirth(dob.getText().toString())) {
                    dob.setError("Invalid DOB");
                }
            }
        });

        /*
        *  Real time email validation
        *  Will allow all alphanumerical letters, '.' and '@'
        */

        email.addTextChangedListener(new TextValidator(email) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidEmail(email.getText().toString())) {
                    email.setError("Invalid Email");
                }
            }
        });

        /*
        * Real Time phone validation
        * Will all all types of phone numbers
        */

//        phone.addTextChangedListener(new TextValidator(phone) {
//            @Override
//            public void validate(TextView textView, String text) {
//                if (!isValidPhoneNumber(phone.getText().toString())) {
//                    phone.setError("Invalid Phone Number");
//                }
//            }
//        });

        /*
        *  Real time twitter
        *  will allow all variations of numbers and letters
        *  aslong as the username starts with an @
        */

        twitter.addTextChangedListener(new TextValidator(twitter) {

            @Override
            public void validate(TextView textView, String text) {
                String s = "@" + twitter.getText().toString();
                if (!isValidTwitter(s)) {
                    twitter.setError("@ Required");
                }
            }
        });

         /*
        *  Real time instagram
        *  will allow all variations of numbers and letters
        *  aslong as the username starts with an @
        */

        instagram.addTextChangedListener(new TextValidator(instagram) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidTwitter(instagram.getText().toString())) {
                    instagram.setError("@ Required");
                }
            }
        });


        return rootView;
    }

    private boolean emailSent = false;
    public View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            FragmentTransaction trans = getFragmentManager()
                    .beginTransaction();
            switch (v.getId()) {
                case R.id.btn_next:
                    boolean validCount = true;
                    validateDetails(validCount);
                    if (validCount) {
                        sendEmail();
                        if (emailSent) {
                            if (AllLoginActivity.userType != UserType.userType.unsignedUser) {
                                final String desc = "Your Application was successfully received";
                                // custom dialog
                                final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Light);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.custom_dialog);
                                TextView description = (TextView) dialog.findViewById(R.id.tvDescription);
                                description.setText(desc);
                                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOkLogin);
                                // Your android custom dialog ok action
                                // Action for custom dialog ok button click
                                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((MainActivity) getActivity()).setCurrentPagerItem(0);
                                        dialog.dismiss();
                                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                                        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                                        query.findInBackground(new FindCallback<ParseUser>() {
                                            @Override
                                            public void done(List<ParseUser> objects, ParseException e) {
                                                if (e == null) {
                                                    int points = objects.get(0).getInt("points");
                                                    points = points + 1000;
                                                    objects.get(0).put("points", points);
                                                    objects.get(0).saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.e("points added", "success");
                                                            } else {
                                                                Log.e("failed", e.getMessage());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Log.e("failed", e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                });
                                dialog.show();
                            }
                        }
                    }
                    break;
            }
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commit();
        }
    };

    @SuppressWarnings("unchecked")
    private void sendEmail() {
        String fromEmail = "becomeamodelpremier@gmail.com";
        String fromPassword = "premier12";
        String toEmails = "becomeamodelpremier@gmail.com";
        String emailSubject = "Premier Model Application From " + name.getText().toString() + ".";
        String emailBody = "<html><body><strong>Details:</strong> \n <br/>" +
                "<table> <tr = style='background-color:#e6e6e6; border: 1px solid black'> <td> Gender: </td>" + "<td>" + prefs.getString("Gender", null) + "</td> \n </tr>" +
                "<tr style='background-color:#ffffff; border: 1px solid black'> <td> Height: </td>" + "<td>" + prefs.getString("Height", null) + "\n </td> </tr>" +
                "<tr style='background-color:#e6e6e6; border: 1px solid black'> <td> Date of birth: </td>" + "<td>" + dob.getText().toString() + "</td> \n </tr>" +
                "<tr style='background-color:#ffffff; border: 1px solid black'> <td> Email: </td>" + "<td>" + email.getText().toString() + "</td> \n </tr>" +
                "<tr style='background-color:#e6e6e6; border: 1px solid black'> <td> Phone number: </td>" + "<td>" + phone.getText().toString() + "</td> \n </tr>" +
                "<tr style='background-color:#ffffff; border: 1px solid black'> <td> Hometown: </td>" + "<td>" + hometown.getText().toString() + "<td> \n </tr>" +
                "<tr style='background-color:#e6e6e6; border: 1px solid black'> <td> Twitter: </td>" + "<td>" + twitter.getText().toString() + "<td> \n </tr> " +
                "<tr style='background-color:#ffffff; border: 1px solid black'> <td> Instagram: </td>" + "<td>" + instagram.getText().toString() + "<td> </tr> </table> </html>";
        new SendMailTask(getActivity(), false).execute(fromEmail,
                fromPassword, toEmails, emailSubject, emailBody, fileHeadPath, fileFullPath);
        emailSent = true;
    }

    private boolean validateDetails(boolean validCount) {
        if (name.getText().toString().isEmpty()) {
            name.setError("Please enter your name");
            validCount = false;
        } if(hometown.getText().toString().isEmpty()) {
            hometown.setError("Please enter your hometown");
            validCount = false;
        } if(age.getText().toString().isEmpty()) {
            age.setError("Please enter your age");
            validCount = false;
        } if(dob.getText().toString().isEmpty()) {
            dob.setError("Please enter your date of birth");
            validCount = false;
        } if(email.getText().toString().isEmpty()) {
            email.setError("Please enter your email");
            validCount = false;
        } if(phone.getText().toString().isEmpty()) {
            phone.setError("Please enter your phone number");
            validCount = false;
        }
        return validCount;
    }

    private boolean isValidName(String name) {
        String NAME_PATTERN = "^[a-zA-Z- ]+$";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidAge(String age) {
        String AGE_PATTERN = "[0-9]+";

        Pattern pattern = Pattern.compile(AGE_PATTERN);
        Matcher matcher = pattern.matcher(age);
        return matcher.matches();
    }

    private boolean isValidDateOfBirth(String dob) {
        String DOB_PATTERN = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        Pattern pattern = Pattern.compile(DOB_PATTERN);
        Matcher matcher = pattern.matcher(dob);
        return matcher.matches();
    }

    private boolean isValidTwitter(String twitter) {
        String TWITTER_PATTERN = "@\\w+";
        Pattern pattern = Pattern.compile(TWITTER_PATTERN);
        Matcher matcher = pattern.matcher(twitter);
        return matcher.matches();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    trans.replace(R.id.street_style_container, new ScoutMeGenderUploadImageFragment());
                    trans.addToBackStack(null);
                    trans.commit();
                    return true;
                }
                return false;
            }
        });
    }
}
