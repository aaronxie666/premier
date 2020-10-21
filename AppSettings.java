package icn.premierandroid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import icn.premierandroid.adapters.RecyclerViewAdapterSuggestedUsers;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.CircleTransform;
import icn.premierandroid.misc.TextValidator;
import icn.premierandroid.models.SuggestedUsersDataModel;

import static com.parse.ParseUser.getCurrentUser;

public class AppSettings extends Activity {

    private ImageView profile_picture_settings;
    private EditText username, name, description;
    private Context context = this;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String mCurrentPhotoPath;
    private ImageView view_blocked_users;
    private ParseObject user_details = getCurrentUser();
    private RecyclerView dialog_recycler;
    private int count3;
    private JSONArray peopleBlocked;
    private ArrayList<String> allBlocked;
    private ArrayList<ParseObject> blockedListUsers;
    private ArrayList<SuggestedUsersDataModel> userBlocked;
    private Dialog blockedDialog;
    private TextView no_blocked_users;
    private RelativeLayout root_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        username = (EditText) findViewById(R.id.settings_username);
        name = (EditText) findViewById(R.id.settings_name);
        description = (EditText) findViewById(R.id.settings_description);
        view_blocked_users = (ImageView) findViewById(R.id.view_blocked);
        root_layout = (RelativeLayout) findViewById(R.id.root_layout);
        if (AllLoginActivity.userType == UserType.userType.normalUser || AllLoginActivity.userType == UserType.userType.instagramUser) {
            profile_picture_settings = (ImageView) findViewById(R.id.profile_picture_settings);
            profile_picture_settings.setOnClickListener(customListener);
        } else {
            profile_picture_settings = (ImageView) findViewById(R.id.profile_picture_settings);
        }
        setupUI(root_layout);

        String profile_pic_url = getCurrentUser().getString("profilePicture");
        if (profile_pic_url != null) {
            if (profile_pic_url.isEmpty()) {
                Log.e("profile_pic_url", "is null");
            } else {
                Log.e("url", profile_pic_url);
                if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                    profile_pic_url = profile_pic_url.replace("http", "https");
                    Log.e("url2", profile_pic_url);
                }

                if (profile_pic_url.startsWith("httpss")) {
                    profile_pic_url = profile_pic_url.replace("httpss", "https");
                    Log.e("url3", profile_pic_url);
                }
                Picasso.with(this).load(profile_pic_url).transform(new CircleTransform()).into(profile_picture_settings);
            }
        } else {
            Picasso.with(this).load(R.drawable.profile_nopic).transform(new CircleTransform()).into(profile_picture_settings);
        }
        try {
            username.setText(ParseUser.getCurrentUser().fetch().getUsername());
            name.setText(getCurrentUser().fetch().getString("name"));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(v);
                }
            }
        });

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(v);
                }
            }
        });

        name.addTextChangedListener(new TextValidator(name) {
            @Override
            public void validate(TextView textView, String text) {
                if (!isValidName(name.getText().toString())) {
                    name.setError("Invalid Name");
                }
            }
        });

        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideSoftKeyboard(v);
                }
            }
        });

        try {
            description.setText(getCurrentUser().fetch().getString("description"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ImageView save_button = (ImageView) findViewById(R.id.save_settings);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", getCurrentUser().getObjectId());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            for (ParseObject j : objects) {
                                String username_string = username.getText().toString();
                                String name_string = name.getText().toString();
                                String description_string = description.getText().toString();

                                if (username_string != j.getString("username")) {
                                    j.put("username", username_string);
                                }

                                if (name_string != j.getString("name")) {
                                    j.put("name", name_string);
                                }

                                if (description_string != j.getString("description")) {
                                    j.put("description", description_string);
                                }

                                j.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
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
                                                    Log.d("Testing",e.getLocalizedMessage());
                                                    break;
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.e("failed", "failed" + e.getMessage());
                        }
                    }
                });
            }
        });

        view_blocked_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        blockedDialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                        blockedDialog.setContentView(R.layout.recycler_view);
                        blockedDialog.setCancelable(true);
                        dialog_recycler = (RecyclerView) blockedDialog.findViewById(R.id.dialog_recyclerview);
                        no_blocked_users = (TextView) blockedDialog.findViewById(R.id.no_follow_message);
                        dialog_recycler.setHasFixedSize(true);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        dialog_recycler.setLayoutManager(mLayoutManager);
                        dialog_recycler.setBackgroundColor(Color.WHITE);
                        blockedDialog.show();
                        populateBlockedUsers();
                    }
        });
    }

    private void populateBlockedUsers() {
        count3 = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        allBlocked = new ArrayList<>();
                        blockedListUsers = new ArrayList<>();
                        userBlocked = new ArrayList<>();
                        peopleBlocked = j.getJSONArray("BlockedUsers");
                        if (peopleBlocked != null) {
                            if (peopleBlocked.length() != 0) {
                                if (allBlocked != null) {
                                    for (int i = 0; i < peopleBlocked.length(); i++) {
                                        allBlocked.add(peopleBlocked.optString(i));
                                        Log.e("alllikes", allBlocked.toString());
                                    }
                                }
                            } else {
                                Log.e("no blocked Users", "no blocked users");
                                no_blocked_users.setVisibility(View.VISIBLE);
                                no_blocked_users.setText("You don't have any users blacklisted at the minute.");
                                dialog_recycler.setVisibility(View.INVISIBLE);
                            }
                            getUserDetails();
                        } else {
                            Log.e("No Likes", "No likes");
                        }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void getUserDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", allBlocked.get(count3));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            Log.d("user_id", "" + j.get("name"));
                            blockedListUsers.add(j);
                            if (allBlocked.size() == 1) {
                                count3 = 1;
                            } else {
                                count3++;
                            }
                            if (count3 < peopleBlocked.length()) {
                                getUserDetails();
                            } else {
                                int z = 0;
                                for (ParseObject ignored : blockedListUsers) {
                                    if (z < peopleBlocked.length()) {
                                        ParseUser theUser = (ParseUser) blockedListUsers.get(z);
                                        String user_name = theUser.getUsername();
                                        String objectId = theUser.getObjectId();
                                        Log.e("user_name", user_name);
                                        String profile_picture = (String) theUser.get("profilePicture");
                                        if (profile_picture != null) {
                                            if (profile_picture.isEmpty()) {
                                                Log.e("profile_pic_url", "is null");
                                            } else {
                                                if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                                                    profile_picture = profile_picture.replace("http", "https");
                                                }

                                                if (profile_picture.startsWith("httpss")) {
                                                    profile_picture = profile_picture.replace("httpss", "https");
                                                }
                                            }
                                        } else {
                                            profile_picture = "R.drawable.profile_nopic";
                                        }
                                        Log.e("profile", "profilepic" + profile_picture);
                                        userBlocked.add(new SuggestedUsersDataModel(user_name, profile_picture, objectId));
                                        z++;
                                    }
                                }
                                final RecyclerViewAdapterSuggestedUsers adapter = new RecyclerViewAdapterSuggestedUsers(context, null, userBlocked);
                                dialog_recycler.setAdapter(adapter);// set adapter on recyclerview
                                adapter.notifyDataSetChanged();// Notify the adapter
                            }
                        }
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            });
        } catch (IndexOutOfBoundsException e) {
            Log.e("failed", "failed " + e.getMessage());
        }
    }

    private boolean isValidName(String name) {
        String NAME_PATTERN = "^[a-zA-Z- ]+$";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.profile_picture_settings:
                    uploadPhoto();
                    break;

            }
        }
    };

    private void uploadPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = setUpPhotoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "icn.premierandroid.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.e("path", "" + storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    handleBigCameraPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadPhotoToParse() {
        Bitmap bitmap = ((BitmapDrawable) profile_picture_settings.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] image = stream.toByteArray();
        // Create the ParseFile
        final ParseFile file = new ParseFile("image.png", image);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    String fileUrl = file.getUrl();
                    Log.e("fileUrl", fileUrl);
                    user_details.put("profilePicture", fileUrl);
                    user_details.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void handleBigCameraPhoto() throws IOException {
        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void galleryAddPic() throws IOException {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        setPic(contentUri);
    }

    private void setPic(Uri contentUri) throws IOException {
        // Get the dimensions of the View
        int targetW = profile_picture_settings.getWidth();
        int targetH = profile_picture_settings.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmap = rotateImageIfRequired(bitmap, contentUri);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 750, 750);
        profile_picture_settings.setImageBitmap(bitmap);
        uploadPhotoToParse();
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri contentUri) throws IOException {
        ExifInterface ei = new ExifInterface(contentUri.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public void setupUI(View view) {
            // Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        hideSoftKeyboard(view);
                        return false;
                    }
                });
            }

            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupUI(innerView);
                }
            }
    }
}
