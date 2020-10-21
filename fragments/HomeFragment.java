package icn.premierandroid.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterAdvent;
import icn.premierandroid.adapters.RecyclerViewAdapterModelTips;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.CustomFontTextView;
import icn.premierandroid.misc.RenderProfileDetails;
import icn.premierandroid.models.AdventModel;
import icn.premierandroid.models.ModelTipsDataModel;

import static org.jsoup.Jsoup.connect;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Home Tab
 */

public class HomeFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private RecyclerView recyclerView;
    private String mCurrentPhotoPath;
    private ArrayList<ModelTipsDataModel> latestVideoList;
    public static TextView userlikes;
    private ArrayList<LinearLayout>  hColumns = new ArrayList<>();
    private ArrayList<ImageView> hImages = new ArrayList<>();
    private ArrayList<CustomFontTextView> hCategories = new ArrayList<>();
    private ArrayList<CustomFontTextView> hBodyTexts = new ArrayList<>();
    private ArrayList<Bitmap> hBitmaps = new ArrayList<>();
    private ArrayList<String> hBlogUrls = new ArrayList<>();
    private ArrayList<String> hCatStrs = new ArrayList<>();
    private ArrayList<String> hTextStrs = new ArrayList<>();
    private ArrayList<String> hTextStrs1 = new ArrayList<>();
    private int colSize;
    private ImageView emailProfilePicture;
    private View rootView;
    private ViewPager viewPager;
    private ParseObject user_details = ParseUser.getCurrentUser();
    private int count = 0, count1 = 0;
    private int backpresscount = 0;
    private Boolean isEventOn, isAdvent, isAfterChristmas;
    private ImageView adventTitle;
    private RecyclerView advent_recycler_view;
    private ArrayList<AdventModel> adventListings = new ArrayList<>();
    private LinearLayout advent_container;
    private boolean addToList = false, isToday = false;
    ParseQuery<ParseObject> query;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            if (rootView == null && AllLoginActivity.userType == UserType.userType.facebookUser
                    || rootView == null && AllLoginActivity.userType == UserType.userType.normalUser || AllLoginActivity.userType == UserType.userType.instagramUser) {
                rootView = inflater.inflate(R.layout.fragment_home_social_media, container, false);
                RenderProfileDetails.renderProfileDetails(rootView, getContext(), false, null);

                userlikes = (TextView) rootView.findViewById(R.id.user_likes);
                updateUserLikes();

                ImageButton offers_button = (ImageButton) rootView.findViewById(R.id.offers_button);
                offers_button.setOnClickListener(customListener);

//                ParseQuery<ParseObject> adventQuery = ParseQuery.getQuery("Event");
//                adventQuery.whereEqualTo("objectId", "cKVkpWOt35");
//                adventQuery.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> objects, ParseException e) {
//                        if (e == null) {
//                            isEventOn = objects.get(0).getBoolean("isEventOn");
//                            isAdvent = objects.get(0).getBoolean("isAdventCalendar");
//                            isAfterChristmas = objects.get(0).getBoolean("isAfterChristmasEvent");
//                            if (isEventOn) {
//                                if (isAdvent) {
//                                    Log.e("advent", "is advent");
//                                    adventTitle.setImageResource(R.drawable.title_adventcalendar);
//                                    advent_recycler_view.setVisibility(View.VISIBLE);
//                                    populateAdventRecyclerView(true);
//                                } else if (isAfterChristmas){
//                                    adventTitle.setImageResource(R.drawable.title_christmas_days);
//                                    advent_recycler_view.setVisibility(View.VISIBLE);
//                                    populateAdventRecyclerView(false);
//                                }
//                            } else {
//                                adventTitle.setVisibility(View.GONE);
//                                advent_recycler_view.setVisibility(View.GONE);
//                            }
//                        } else {
//                            Log.e("failed", "failed" + e.getMessage());
//                        }
//                    }
//                });
            } else if (AllLoginActivity.userType == UserType.userType.unsignedUser) {
                rootView = inflater.inflate(R.layout.fragment_home_anon, container, false);

                ImageButton anon_login = (ImageButton) rootView.findViewById(R.id.anon_login_home);
                anon_login.setOnClickListener(customListener);
            }

        initColumns(rootView);
        colSize = 2;
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        adventTitle = (ImageView) rootView.findViewById(R.id.advent_title);
        advent_recycler_view = (RecyclerView) rootView.findViewById(R.id.advent_recyclerview_container);
        advent_recycler_view.setHasFixedSize(true);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager aLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        advent_recycler_view.setLayoutManager(aLayoutManager);

        if (AllLoginActivity.userType == UserType.userType.normalUser || AllLoginActivity.userType == UserType.userType.instagramUser) {
            emailProfilePicture = (ImageView) rootView.findViewById(R.id.profile_picture);
            emailProfilePicture.setOnClickListener(customListener);
        }

        TableLayout bContainer = (TableLayout) rootView.findViewById(R.id.blog_container_home);
        new Home(bContainer).execute();

        ImageButton view_more = (ImageButton) rootView.findViewById(R.id.view_more);
        view_more.setOnClickListener(customListener);

        // Assuming you are using xml layout
        ImageButton facebook_button = (ImageButton) rootView.findViewById(R.id.facebook_button);
        facebook_button.setOnClickListener(customListener);

        ImageButton instagram_button = (ImageButton) rootView.findViewById(R.id.instagram_button);
        instagram_button.setOnClickListener(customListener);

        ImageButton twitter_button = (ImageButton) rootView.findViewById(R.id.twitter_button);
        twitter_button.setOnClickListener(customListener);

        for (int i = 0; i < colSize; i++) {
            final int finalI = i;
            hColumns.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment udf = new BlogContentFragment();
                    Bundle args = new Bundle();
                    args.putString("blog_url", hBlogUrls.get(finalI));
                    udf.setArguments(args);
                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();
                    trans.replace(R.id.root_home_frame, udf);
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);
                    trans.commit();
                }
            });
        }
        return rootView;
    }

    private void populateAdventRecyclerView(boolean advent) {
        if (advent) {
            query = ParseQuery.getQuery("AdventCalendar");
        } else  {
            query = ParseQuery.getQuery("AfterChristmasEvent");
        }
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    int z = 0;
                    int position = 0;
                    for (ParseObject j : objects) {
                        Boolean isCode = j.getBoolean("isCode");
                        Boolean isPoints = j.getBoolean("isPoints");
                        Boolean isVideo = j.getBoolean("isVideo");
                        int awardPoints = j.getInt("awardPoints");
                        Date date = j.getDate("dateAndroid");
                        DateTime dateTime = new DateTime(date);
                        DateTime dateTime1 = new DateTime().withTimeAtStartOfDay();
                        if (!isToday) {
                            isToday = dateTime.toLocalDateTime().isEqual(dateTime1.toLocalDateTime());
                        }
                        if (isToday) {
                            position = z;
                            addToList = true;
                        }
                        String likes_description = j.getString("likesDescription");
                        ParseFile companyLogoFile = j.getParseFile("companyLogo");
                        ParseFile videoThumbnailFile = j.getParseFile("videoThumbnail");
                        String videoThumbnailUrl = null, companyLogoUrl = null;
                        if (videoThumbnailFile != null) {
                            videoThumbnailUrl = videoThumbnailFile.getUrl();
                        }
                        if (companyLogoFile != null) {
                            companyLogoUrl = companyLogoFile.getUrl();
                        }
                        String discountCode = j.getString("discountCode");
                        String codeDescription = j.getString("codeDescription");
                        String websiteUrl = j.getString("websiteUrl");
                        String videoUrl = j.getString("videoUrl");
                        if (addToList) {
                            adventListings.add(new AdventModel(isCode, isPoints, isVideo, awardPoints, date, likes_description, videoThumbnailUrl, companyLogoUrl, discountCode, codeDescription, websiteUrl, videoUrl, position));
                        }
                        z++;
                    }

                    RecyclerViewAdapterAdvent adapter = new RecyclerViewAdapterAdvent(getActivity(), adventListings, viewPager);
                    advent_recycler_view.setAdapter(adapter);// set adapter on recyclerview
                    adapter.notifyDataSetChanged();// Notify the adapter
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });

    }

    private void updateUserLikes() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        int points = j.getInt("points");
                        String pointsValue = String.valueOf(points);
                        userlikes.setText(pointsValue);
                    }
                } else {
                    Log.e("failed", e.getMessage());
                }
            }
        });
    }

    private void initColumns(View rootView) {
        LinearLayout column1 = (LinearLayout) rootView.findViewById(R.id.column_home_1);
        hColumns.add(column1);
        LinearLayout column2 = (LinearLayout) rootView.findViewById(R.id.column_home_2);
        hColumns.add(column2);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateRecyclerView();
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.offers_button:
                    viewPager.setCurrentItem(5, false);
                    break;
                case R.id.view_more:
                    viewPager.setCurrentItem(2, true);
                    break;
                case R.id.facebook_button:
                    startActivity(newFacebookIntent(getActivity().getPackageManager(), "https://www.facebook.com/PremierModelManagement/"));
                    break;
                case R.id.instagram_button:
                    Uri uri = Uri.parse("https://www.instagram.com/_u/premiermodels/");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("com.instagram.android");
                    if (isIntentAvailable(getActivity(), likeIng)){
                        startActivity(likeIng);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/_u/premiermodels/")));
                    }
                    break;
                case R.id.twitter_button:
                    Intent twIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://twitter.com/PremierModels"));
                    startActivity(twIntent);
                    break;
                case R.id.profile_picture:
                    uploadPhoto();
                    break;
                case R.id.anon_login_home:
                    Intent i = new Intent(getActivity(), AllLoginActivity.class);
                    getActivity().startActivity(i);
                    getActivity().finish();
                    break;

            }
        }
    };

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private void uploadPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = setUpPhotoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        Bitmap bitmap = ((BitmapDrawable) emailProfilePicture.getDrawable()).getBitmap();
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
                                Toast.makeText(getActivity(), "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Image Upload failed, please try again.", Toast.LENGTH_SHORT).show();
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
        getActivity().sendBroadcast(mediaScanIntent);
        setPic(contentUri);
    }

    private void setPic(Uri contentUri) throws IOException {
        // Get the dimensions of the View
        int targetW = emailProfilePicture.getWidth();
        int targetH = emailProfilePicture.getHeight();

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
        emailProfilePicture.setImageBitmap(bitmap);
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



    private void populateRecyclerView() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("latestVideos");
        Log.e("get query", "gets query");
        query.orderByDescending("createdAt");
        Log.e("get order", "ordered");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (e == null) {
                        Log.e("does it get here", "it got here");
                        latestVideoList = new ArrayList<>();
                        for (ParseObject j : objects) {
                            ParseFile image = (ParseFile) j.get("thumbnail");
                            String video_link = (String) j.get("videoLink");
                            latestVideoList.add(new ModelTipsDataModel(j.getString("videoTitle"), image.getUrl(), video_link));
                        }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

                RecyclerViewAdapterModelTips adapter = new RecyclerViewAdapterModelTips(getActivity(), latestVideoList);
                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });
    }

    public static HomeFragment newInstance(int imageNum) {
        final HomeFragment f = new HomeFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }

    // Title AsyncTask
    private class Home extends AsyncTask<Void, Void, Void> {
        ImageView image = new ImageView(getActivity());
        TableLayout container;
        Bitmap bitmap;

        Home(TableLayout container) {
            this.container = container;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for (int i = 0; i < colSize; i++) {
                hImages.add(new ImageView(getActivity()));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
                params.gravity = Gravity.CENTER_HORIZONTAL;
                hImages.get(i).setLayoutParams(params);
                hCategories.add(new CustomFontTextView(getActivity()));
                hCategories.get(i).setTextSize(12);
                hCategories.get(i).setTextColor(Color.BLACK);
                hBodyTexts.add(new CustomFontTextView(getActivity()));
                hBodyTexts.get(i).setMaxLines(3);
                hBodyTexts.get(i).setEllipsize(TextUtils.TruncateAt.END);
                hBodyTexts.get(i).setTextColor(Color.BLACK);
                hBodyTexts.get(i).setTextSize(18);
                LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                hBodyTexts.get(i).setLayoutParams(bodyParams);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams)  hColumns.get(i).getLayoutParams();
                params3.bottomMargin = 50;
                params3.leftMargin = 25;
                params3.rightMargin = 25;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = "http://www.premiermodelmanagement.com/blog/";
                Document doc = connect(url).get();

                for (Element m : doc.select(".blog-item > a")) {
                    if (count < colSize) {
                        String link = m.attr("abs:href");
                        hBlogUrls.add(link);
                    }
                    count++;
                }

                for (Element e : doc.select("img")) {
                     if (count1 < colSize) {
                        bitmap = getImageBitmap(e.attr("data-original"));
                        Log.e("data-original", e.attr("data-original"));
                        hBitmaps.add(bitmap);
                         if (count1 == colSize) {
                             break;
                         }
                     }
                     count1++;
                }

                for (Element j : doc.select(".blog-item-text > p")) {
                        String text = j.text();
                        hCatStrs.add(text);
                }
                    Document bodyText = connect(hBlogUrls.get(0)).get();
                    Log.e("link1", hBlogUrls.get(1));
                    for (Element l : bodyText.select(".post-text > p")) {
                        String text = l.text();
                        hTextStrs.add(text);
                    }

                    Document bodyText1 = connect(hBlogUrls.get(1)).get();
                    for (Element j : bodyText1.select(".post-text > p")) {
                        String text = j.text();
                        hTextStrs1.add(text);
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap getImageBitmap(String url) {
            Bitmap bm = null;
            try {
                // See what we are getting
                Log.i("tag", "" + url);
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("dd", "Error getting bitmap", e);
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Void result) {
            for (int i = 0; i < colSize; i++) {
                if (hColumns.get(i).getChildCount() != 0) {
                    hColumns.get(i).removeAllViews();
                }
            }
            BuildTable();
        }

        private void BuildTable() {
            try {
                String text = hTextStrs.toString()
                .replace(",", "")  //remove the commas
                        .replace("[", "")  //remove the right bracket
                        .replace("]", "")  //remove the left bracket
                        .trim();//remove trailing spaces from partially initialized arrays

                String text1 = hTextStrs1.toString()
                        .replace(",", "")  //remove the commas
                        .replace("[", "")  //remove the right bracket
                        .replace("]", "")  //remove the left bracket
                        .trim();           //remove trailing spaces from partially initialized arrays

                for (int i = 0; i < colSize; i++) {
                    hImages.get(i).setImageBitmap(hBitmaps.get(i));
                    hCategories.get(i).setText(hCatStrs.get(i));
                    if (i == 0) {
                        hBodyTexts.get(0).setText(text);
                    }
                    else if (i == 1) {
                        hBodyTexts.get(1).setText(text1);
                    }
                    hColumns.get(i).addView(hImages.get(i));
                    hColumns.get(i).addView(hCategories.get(i));
                    hColumns.get(i).addView(hBodyTexts.get(i));
                    Log.e("link", "" + hBlogUrls.get(i) + "" + i);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    backpresscount++;
                    if (backpresscount == 1) {
                        Toast.makeText(getContext(), "Press back once more to leave the app.", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (backpresscount == 2) {
                        System.exit(0);
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
