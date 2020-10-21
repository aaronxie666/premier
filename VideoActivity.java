package icn.premierandroid;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.LikesDialog;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_VIDEO_FIVE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_VIDEO_ONE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_VIDEO_TWO;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: When user presses a video thumbnail in HomeFragment
 */

public class VideoActivity extends Activity {

    // Declare variables
    private VideoView videoView;
    private ProgressBar progressBar;
    private int videoCount;
    private final int FIRST_ID = 0, SECOND_ID = 1, THIRD_ID = 4;

    // Insert your Video URL
    String VideoURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        addtoVideoCount();
        videoView = (VideoView) findViewById(R.id.videoView);
        Intent intent = getIntent();
        VideoURL = intent.getStringExtra("VIDEO_URL");
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        try {

            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoActivity.this);
            mediacontroller.setAnchorView(videoView);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (AllLoginActivity.userType != UserType.userType.unsignedUser) {
                        switch (videoCount) {
                            case FIRST_ID:
                                LikesDialog likesDialogVideoOne = new LikesDialog(VideoActivity.this, PARSE_ID_VIDEO_ONE);
                                likesDialogVideoOne.display();
                            case SECOND_ID:
                                LikesDialog likesDialogVideoTwo = new LikesDialog(VideoActivity.this, PARSE_ID_VIDEO_TWO);
                                likesDialogVideoTwo.display();
                                break;
                            case THIRD_ID:
                                LikesDialog likesDialogVideoThree = new LikesDialog(VideoActivity.this, PARSE_ID_VIDEO_FIVE);
                                likesDialogVideoThree.display();
                                break;
                        }
                    } else {
                        finish();
                    }
                }
            });
    }

    private void addtoVideoCount() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Last_Visited");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        videoCount = j.getInt("videoCount");
                        videoCount++;
                        j.put("videoCount", videoCount);
                        j.saveInBackground();
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
