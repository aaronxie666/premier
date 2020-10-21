package icn.premierandroid.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import icn.premierandroid.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Next button press in ScoutMeGenderFragment
 */

public class ScoutMeGenderUploadImageFragment extends Fragment {

    protected static final int REQUEST_HEAD_CAPTURE = 2, REQUEST_FULL_CAPTURE = 3;
    private String mCurrentPhotoPath, fCurrentPhotoPath;
    private ImageView headshotUpload, fullImageUpload;
    private Boolean headshot = false, fullLength = false, nextFrag = false;
    private Fragment udf;
    private String headFilePath, fullFilePath;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences prefs = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        String gender = prefs.getString("Gender", null);
        String height = prefs.getString("Height", null);

        assert gender != null;
        switch (gender) {
            case "Male":
                rootView = inflater.inflate(R.layout.fragment_scout_me_male_image_upload, container, false);
                break;
            case "Female":
                rootView = inflater.inflate(R.layout.fragment_scout_me_female_image_upload, container, false);
                break;
        }

        Log.e("User Details", "Gender is: " + gender + " Height is : " + height);
        ImageButton next_btn = (ImageButton) rootView.findViewById(R.id.btn_next);
        next_btn.setOnClickListener(customListener);

        headshotUpload = (ImageView) rootView.findViewById(R.id.headshot_upload);
        headshotUpload.setOnClickListener(customListener);

        fullImageUpload = (ImageView) rootView.findViewById(R.id.fulllength_upload);
        fullImageUpload.setOnClickListener(customListener);
        return rootView;
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_next:
                    if (headshot && fullLength) {
                        udf = new ScoutMeUserDetailsFragment();
                        Bundle args = new Bundle();
                        args.putString("Head_Image", headFilePath);
                        args.putString("Full_Image", fullFilePath);
                        udf.setArguments(args);
                        nextFrag = true;
                    }
                    else {
                        Toast.makeText(getActivity(), "You need to upload a both images", Toast.LENGTH_SHORT).show();
                    }

                    if (nextFrag) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        FragmentTransaction trans = getFragmentManager()
                                .beginTransaction();
                        trans.replace(R.id.root_scout_frame, udf);
                        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        trans.addToBackStack(null);
                        trans.commit();
                    }
                    break;
                case R.id.headshot_upload:
                    cameraHeadIntent();
                    break;
                case R.id.fulllength_upload:
                    cameraFullIntent();
                    break;
            }
        }
    };

    private void cameraFullIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = setUpFullImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "icn.premierandroid.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_FULL_CAPTURE);
            }
        }
    }

    private void cameraHeadIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = setUpHeadImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "icn.premierandroid.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_HEAD_CAPTURE);
            }
        }

    }

    private File setUpHeadImageFile() throws IOException {
        File f = createHeadImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createHeadImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "HEADSHOT_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.e("path", "" + storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        headFilePath = image.getAbsolutePath();
        return image;
    }

    private File setUpFullImageFile() throws IOException {
        File f = createFullImageFile();
        fCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createFullImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "FullShot_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.e("path", "" + storageDir);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        fCurrentPhotoPath = "file:" + image.getAbsolutePath();
        fullFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_HEAD_CAPTURE){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    handleHeadCamaeraPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_FULL_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    handleFullCameraPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleHeadCamaeraPhoto() throws IOException {
        if (mCurrentPhotoPath != null) {
            galleryHeadAddPic();
            mCurrentPhotoPath = null;
        }

    }
    private void handleFullCameraPhoto() throws IOException {
        if (fCurrentPhotoPath != null) {
            galleryFullAddPic();
            fCurrentPhotoPath = null;
        }

    }

    private void galleryHeadAddPic() throws IOException {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        setHeadPic(contentUri);
    }

    private void galleryFullAddPic() throws IOException {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(fCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);

        getActivity().sendBroadcast(mediaScanIntent);
        setFullPic(contentUri);
    }

    private void setHeadPic(Uri contentUri) throws IOException {
        // Get the dimensions of the View
        int targetW = headshotUpload.getWidth();
        int targetH = headshotUpload.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        bitmap = rotateImageIfRequired(bitmap, contentUri);
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//
//        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
//                matrix, true);

        headshotUpload.setImageBitmap(bitmap);
        headshot = true;
    }

    private void setFullPic(Uri contentUri) throws IOException {
        // Get the dimensions of the View
        int targetW = fullImageUpload.getWidth();
        int targetH = fullImageUpload.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fCurrentPhotoPath, bmOptions);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, targetW, targetH);

        Bitmap bitmap = BitmapFactory.decodeFile(fCurrentPhotoPath, bmOptions);

        bitmap = rotateImageIfRequired(bitmap, contentUri);
        fullImageUpload.setImageBitmap(bitmap);
        fullLength = true;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
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

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
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
                    trans.replace(R.id.street_style_container, new ScoutMeGenderFragment());
                    trans.addToBackStack(null);
                    trans.commit();
                    return true;
                }
                return false;
            }
        });
    }
}
