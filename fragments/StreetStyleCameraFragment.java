package icn.premierandroid.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewAnimator;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterSearch;
import icn.premierandroid.misc.CustomFontTextView;
import icn.premierandroid.misc.LikesDialog;
import icn.premierandroid.models.SearchDataModel;

import static icn.premierandroid.misc.CONSTANTS.CAM_SHARED_PREFS;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_STREET_STYLE_UPLOAD;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Camera Icon press in StreetStyleFragment Bottom Bar
 */

public class StreetStyleCameraFragment extends Fragment {

    protected static final int REQUEST_IMAGE_CAPTURE = 2, PICK_PHOTO_CODE = 1046;
    private ImageView uploadedImage;
    private EditText image_upload_caption;
    private ViewAnimator listViewAnimator;
    public Dialog dialog;
    protected RecyclerView.LayoutManager uLayoutManager, tLayoutManager, cLayoutManager;
    public LinearLayout list_container, search_layout, item_list_container;
    private RelativeLayout root_layout, root_layout_items, containerLayout;
    private RecyclerView retailerSearchList, itemSearchList, customSearchlist;
    private EditText search_bar;
    private String searchQuery, picturePath, mCurrentPhotoPath;
    public int screenWidth, screenHeight;
    private ArrayList<SearchDataModel> matchingRetailers;
    private List<String> itemsList;
    private Boolean onlyTag = true;
    private CustomFontTextView tag;
    int count = 0;
    private static float xPosition, yPosition;
    private String type, item;
    private ArrayList<String> tagNamesList = new ArrayList<>();
    private ArrayList<String> tagClothingList = new ArrayList<>();
    private ArrayList<Float> tagXList = new ArrayList<>(), tagYList = new ArrayList<>();
    private ArrayList<JSONObject> list = new ArrayList<>();
    private boolean imageSelected = false;
    private ArrayList<CustomFontTextView> tagsViewList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            showUploadDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getSharedPreferences(CAM_SHARED_PREFS, 0).edit().clear().apply();
        View rootView = inflater.inflate(R.layout.fragment_ss_camera, container, false);
        search_layout = (LinearLayout) rootView.findViewById(R.id.search_container_child);
        search_layout.setVisibility(View.GONE);
        uploadedImage = (ImageView) rootView.findViewById(R.id.uploaded_image);
        uploadedImage.setOnTouchListener(touchListener);
        image_upload_caption = (EditText) rootView.findViewById(R.id.image_upload_caption);
        containerLayout = (RelativeLayout) rootView.findViewById(R.id.uploaded_image_container);
        ImageButton post_photo_button = (ImageButton) rootView.findViewById(R.id.post_photo);
        post_photo_button.setOnClickListener(customListener);
        search_bar = (EditText) rootView.findViewById(R.id.search_edittext);
        root_layout = (RelativeLayout) rootView.findViewById(R.id.scroll_child_retailers);
        root_layout_items = (RelativeLayout) rootView.findViewById(R.id.scroll_child_items);
        list_container = new LinearLayout(getActivity());
        item_list_container = new LinearLayout(getActivity());
        tagsViewList = new ArrayList<>();
        tagsViewList.clear();
//        container_layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    if (uploadedImage.getDrawable() != null) {
//                        Log.e("image", "image exists");
//                    } else {
//                        Log.e("no image", "image doesn't exist");
//                    }
//                } else {
//                    Log.e("container", "has focus");
//                }
//            }
//        });
        retailerSearchList = new RecyclerView(getActivity());
        customSearchlist = new RecyclerView((getActivity()));
        itemSearchList = new RecyclerView(getActivity());
        itemsList = Arrays.asList("Coat", "Gloves", "Glasses", "Hat", "Scarf", "Shirt", "Shoes", "Socks", "Skirts", "Sunglasses", "T-shirt", "Trainers", "Trousers", "Vest", "Jumper", "Jacket");
        setupViewAnimation(rootView);

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (list_container.getParent() != null) {
                    LinearLayout.LayoutParams list_params = (new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    list_params.setMargins(3, 0, 3, 0);
                    customSearchlist.setLayoutParams(list_params);
                    customSearchlist.setHasFixedSize(true);
                    cLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    customSearchlist.setLayoutManager(cLayoutManager);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchQueryResult();
                    searchQuery = search_bar.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                    searchQueryResult();
                    searchQuery = search_bar.getText().toString();

            }
        });

        return rootView;
    }


    private void searchQueryResult() {
        ParseQuery<ParseObject> tagQuery = ParseQuery.getQuery("OfficialRetailers");
        tagQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (e == null) {
                    String objectid = null, tagName = null;
                    matchingRetailers = new ArrayList<>();
                    for (ParseObject j : objects) {
                        if (!j.getString("retailer").equals(searchQuery)) {
                            if (searchQuery.length() > 1) {
                                objectid = j.getObjectId();
                                tagName = searchQuery.substring(0, 1).toUpperCase() + searchQuery.substring(1);
                            }
                        }
                    }
                    type = "Retailer";
                    matchingRetailers.add(new SearchDataModel(tagName, type, objectid, tagsViewList));
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
                RecyclerViewAdapterSearch adapter = new RecyclerViewAdapterSearch(getActivity(), matchingRetailers, listViewAnimator, tag, root_layout_items, tagXList, tagYList, onlyTag, tagNamesList, tagClothingList, count);
                customSearchlist.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });

    }

    private void addTagView(float x, float y) {
        tag = new CustomFontTextView(getActivity());
        setXPosition(x);
        setYPosition(y);
        tag.setX(x);
        tag.setY(y);
        tag.setHint(R.string.tag_hint);
        tag.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tag.setMaxLines(1);
        tag.setTextSize(11);
        tag.setClickable(true);
        tag.setHintTextColor(Color.WHITE);
        tag.setTextColor(Color.WHITE);
        tag.setBackgroundResource(R.drawable.tags_rounded_corners);
        containerLayout.addView(tag);
        SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
        int id = sp.getInt("counter", -1);
        tagsViewList.add(tag);
        onlyTag = false;
        tag.setOnTouchListener(customTouchListener);
        setupSearchRecyclerView(count);
        setupItemRecyclerView();
    }

    public void showUploadDialog() {
        // custom dialog
        dialog = new Dialog(getActivity(), android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_image_dialog);
        dialog.show();
        ImageButton dialogButtonGallery = (ImageButton) dialog.findViewById(R.id.button_gallery);
        ImageButton dialogButtonUpload = (ImageButton) dialog.findViewById(R.id.button_take_photo);
        // Click cancel to dismiss android custom dialog box
        dialogButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();
                dialog.dismiss();
            }
        });
        dialogButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
                dialog.dismiss();
            }
        });

    }

    private void showLikesDialog() {
        LikesDialog likesDialog = new LikesDialog(getActivity(), PARSE_ID_STREET_STYLE_UPLOAD);
        likesDialog.display();
    }

    private void setupViewAnimation(View rootView) {
        listViewAnimator = (ViewAnimator) rootView.findViewById(R.id.MainActivityViewAnimator);
        Animation retailers = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
        Animation items = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        listViewAnimator.setInAnimation(items);
        listViewAnimator.setOutAnimation(retailers);
        listViewAnimator.setBackgroundColor(getColorWithAlpha(Color.WHITE, 0.2f));
        getScreenWidthAndHeight();

    }

    public static int getColorWithAlpha(int color, float ratio) {
        int newColor;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    private void getScreenWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    private void cameraIntent() {
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

    private void galleryIntent() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onactivityresult", "it's here right now");
            if (requestCode == PICK_PHOTO_CODE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.e("inside gallery", "it's here");
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                    if (cursor != null) {
                        Log.e("cursor!null", "");
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        cursor.close();
                    }
                    // Load the selected image into a preview
                    uploadedImage.setImageBitmap(getScaledBitmap(picturePath, uploadedImage.getWidth(), uploadedImage.getHeight()));
                }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    handleBigCameraPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
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

    public View.OnTouchListener customTouchListener = (new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("id", "" + tag.getId());
            Log.d("onlyTag", "" + onlyTag);
            Log.d("root visibility","" + root_layout.getVisibility());
            final int DRAWABLE_LEFT = 0;
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if (tagsViewList != null) {
                    for (int i = 0; i < tagsViewList.size(); i++) {
                        if (event.getRawX() >= (tagsViewList.get(i).getRight() - tagsViewList.get(i).getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                            ViewGroup vg = (ViewGroup) v.getParent();
                            vg.removeView(v);
                            if (i == 1) {
                                tagsViewList.remove(tagsViewList.get(i));
                                tagNamesList.remove(i);
                                tagYList.remove(i);
                                tagXList.remove(i);
                                tagClothingList.remove(i);
                                SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                                count = sp.getInt("counter", -1);
                                Log.e("count", "" + count);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("counter", count = count - 1);
                                editor.apply();
                            } else {
                                tagsViewList.remove(tagsViewList.get(i));
                                tagNamesList.remove(i);
                                tagYList.remove(i);
                                tagXList.remove(i);
                                tagClothingList.remove(i);
                                SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                                count = sp.getInt("counter", -1);
                                Log.e("count", "" + count);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("counter", count = count);
                                editor.apply();
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    });

    public View.OnTouchListener touchListener = (new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.uploaded_image:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setPressed(true);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setPressed(false);
                         if (onlyTag && count == 0) {
                             float x = 100 - (uploadedImage.getWidth() - event.getX())/uploadedImage.getWidth() * 100;
                             float y = 100 - (uploadedImage.getHeight() - event.getY())/uploadedImage.getHeight() * 100;
                             Log.e("width/height", "" + uploadedImage.getWidth() + " " + uploadedImage.getHeight());
                             Log.e("percentage x is", "" + x);
                             Log.e("percentage y is ", "" + y);
                             SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                             SharedPreferences.Editor editor = sp.edit();
                             editor.putInt("counter", count);
                             editor.apply();
                             count++;
                             addTagView(event.getX(), event.getY());
                        } else if (!onlyTag) {
                            Log.d("onlytag is true", "count != 0");
                            Log.d("onlyTag", "" + onlyTag);
                            Log.d("root visibility", "" + root_layout.getVisibility());
                             SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                             SharedPreferences.Editor editor = sp.edit();
                             editor.putInt("counter", count);
                             editor.apply();
                             count++;
                            addTagView(event.getX(), event.getY());
                            listViewAnimator.showPrevious();
                        }
                    }
                        break;
                }
            return true;
        }
    });

    public View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.post_photo:
                    showLikesDialog();
                    Bitmap bitmap = ((BitmapDrawable) uploadedImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    // Create the ParseFile
                    ParseFile file = new ParseFile("image.png", image);
                    // Upload the image into Parse Cloud
                    try {
                        file.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ParseUser user = ParseUser.getCurrentUser();
                    String caption = String.valueOf(image_upload_caption.getText());
                    ParseObject imageUpload = new ParseObject("FashionFeed");
                    imageUpload.put("uploader", user);
                    imageUpload.put("caption", caption);
                    imageUpload.put("image", file);
                    imageUpload.put("peopleVoted", list);
                    int tagAmount = tagNamesList.size();
                    imageUpload.put("tags", "" + tagAmount);
                    imageUpload.put("likes", "" + 0);
                    imageUpload.addAll("tagName", tagNamesList);
                    imageUpload.addAll("clothingArray", tagClothingList);
                    imageUpload.addAll("tagPointX", tagXList);
                    imageUpload.addAll("tagPointY", tagYList);
                    imageUpload.put("comments", "" + 0);
                    imageUpload.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());
                                logger.logEvent("StreetStyle Upload");
                                StreetStyleFragment.bottomBar.selectTabAtPosition(3);
                                Map<String, String> articleParams = new HashMap<String, String>();
                                //param keys and values have to be of String type
                                articleParams.put("Street Style Upload", "User uploaded an image to Street Style.");
                                //up to 10 params can be logged with each event
                                FlurryAgent.logEvent("Street Style Upload", articleParams);
                            }
                            else {
                                Log.e("failed", "failed" + e.getMessage());
                            }
                        }
                    });
                    SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("counter", 0);
                    editor.apply();
                    tagsViewList.clear();
                    break;
            }
        }

    };

    private void setupItemRecyclerView() {
        root_layout_items.setBackgroundColor(getColorWithAlpha(Color.WHITE, 0.2f));
        if (item_list_container.getParent() != null) {
            Log.d("item_list_container", "is null");
            Log.d("onlytag is true", "count != 0");
            Log.d("onlyTag", "" + onlyTag);
            Log.d("root visibility","" + root_layout.getVisibility());
            itemSearchList.setHasFixedSize(true);
            itemSearchList.setLayoutManager(tLayoutManager);
            root_layout_items.bringToFront();
            search_layout.bringToFront();
        }
        else{
            Log.d("item_list_container", "not null");
            Log.d("onlytag is true", "count != 0");
            Log.d("onlyTag", "" + onlyTag);
            Log.d("root visibility","" + root_layout.getVisibility());
            LinearLayout.LayoutParams root_container_params = (new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            item_list_container.setLayoutParams(root_container_params);
            item_list_container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams list_params = (new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            list_params.setMargins(3, 0, 3, 0);
            itemSearchList.setLayoutParams(list_params);
            itemSearchList.setBackgroundColor(Color.TRANSPARENT);
            root_layout_items.addView(item_list_container);
            item_list_container.addView(itemSearchList);
            itemSearchList.setHasFixedSize(true);
            tLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            itemSearchList.setLayoutManager(tLayoutManager);
            getItems();
            root_layout_items.bringToFront();
            search_layout.bringToFront();
        }

    }

    private void getItems() {
        ArrayList<SearchDataModel> clothingList = new ArrayList<>();
        for (int i = 0; i < itemsList.size(); i ++) {
            String itemName = itemsList.get(i);
            type = "Item";
            clothingList.add(new SearchDataModel(itemName, type, "", tagsViewList));
        }
        RecyclerViewAdapterSearch adapter = new RecyclerViewAdapterSearch(getActivity(), clothingList, listViewAnimator, tagsViewList.get(count - 1), root_layout_items, tagXList, tagYList, onlyTag, tagNamesList, tagClothingList, count);
        itemSearchList.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter
    }

    private void setupSearchRecyclerView(int count) {
        listViewAnimator.setVisibility(View.VISIBLE);
        root_layout.setBackgroundColor(getColorWithAlpha(Color.WHITE, 0.2f));
        search_layout.setVisibility(View.VISIBLE);
        listViewAnimator.bringToFront();
        if (list_container.getParent() != null) {
            searchQuery = search_bar.getText().toString();
            retailerSearchList.setHasFixedSize(true);
            retailerSearchList.setLayoutManager(uLayoutManager);
            root_layout.bringToFront();
            search_layout.bringToFront();
        }
        else {
            Log.d("list_container", "is null");
            Log.d("onlytag is true", "count != 0");
            Log.d("onlyTag", "" + onlyTag);
            Log.d("root visibility","" + root_layout.getVisibility());
            searchQuery = search_bar.getText().toString();
            LinearLayout.LayoutParams root_container_params = (new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            list_container.setLayoutParams(root_container_params);
            list_container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams list_params = (new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            list_params.setMargins(3, 0, 3, 0);
            retailerSearchList.setLayoutParams(list_params);
            root_layout.addView(list_container);
            root_layout.setBackgroundColor(Color.BLACK);
            list_container.addView(customSearchlist);
            list_container.addView(retailerSearchList);
            retailerSearchList.setHasFixedSize(true);
            uLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            retailerSearchList.setLayoutManager(uLayoutManager);
            searchRetailers(count);
            root_layout.bringToFront();
            search_layout.bringToFront();
        }
    }


    private void searchRetailers(final int count) {
        ParseQuery<ParseObject> tagQuery = ParseQuery.getQuery("OfficialRetailers");
        tagQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (e == null) {
                    Log.e("does it get here", "it got here");
                    matchingRetailers = new ArrayList<>();
                    for (ParseObject j : objects) {
                        String tagName = (String) j.get("retailer");
                        Log.e("retailer", tagName);
                        String objectId = j.getObjectId();
                        type = "Retailer";
                        matchingRetailers.add(new SearchDataModel(tagName, type, objectId, tagsViewList));
                        Log.e("tags", "" + matchingRetailers.size());
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
                RecyclerViewAdapterSearch adapter = new RecyclerViewAdapterSearch(getActivity(), matchingRetailers, listViewAnimator, tagsViewList.get(count - 1), root_layout_items, tagXList, tagYList, onlyTag, tagNamesList, tagClothingList, count);
                retailerSearchList.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });
    }


    private void setPic(Uri contentUri) throws IOException {
        // Get the dimensions of the View
        int targetW = uploadedImage.getWidth();
        int targetH = uploadedImage.getHeight();

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
        uploadedImage.setImageBitmap(bitmap);
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

    public void setXPosition(float xPosition) {
       StreetStyleCameraFragment.xPosition = 100 - (uploadedImage.getWidth() - xPosition)/uploadedImage.getWidth() * 100;
    }

    public static float getXPosition() {
        return xPosition;
    }

    public void setYPosition(float yPosition) {
        StreetStyleCameraFragment.yPosition =  100 - (uploadedImage.getHeight() - yPosition)/uploadedImage.getHeight() * 100;
    }

    public static float getYPosition() {
        return yPosition;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
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
                    trans.replace(R.id.street_style_container, new StreetStyleHomeFragment());
                    trans.addToBackStack(null);
                    trans.commit();
                    SharedPreferences sp = getActivity().getSharedPreferences(CAM_SHARED_PREFS, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("counter", 0);
                    editor.apply();
                    tagsViewList.clear();
                    return true;
                }
                return false;
            }
        });
    }


}
