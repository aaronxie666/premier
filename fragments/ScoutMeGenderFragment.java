package icn.premierandroid.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import icn.premierandroid.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Male or Female Button press in ScoutMeFragment
 */

public class ScoutMeGenderFragment extends Fragment {

    private SharedPreferences.Editor editor;
    private TextView seekBarValue;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SharedPreferences prefs = getContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        String gender = prefs.getString("Gender", null);
        Log.e("Gender is", gender);
        editor = prefs.edit();

        assert gender != null;
        switch (gender) {
            case "Male":
            rootView = inflater.inflate(R.layout.fragment_scout_me_male, container, false);

            SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seekBar1);
            seekBar.setProgress(0);
            seekBar.incrementProgressBy(15);
            seekBar.setMax(105);
            seekBarValue = (TextView) rootView.findViewById(R.id.male_height);
            seekBarValue.setText(seekBarValue.getText().toString());

            seekBar.setOnTouchListener(new SeekBar.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle Seekbar touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progress = progress / 15;
                    progress = progress * 15;
                    switch (progress) {
                        case 0:
                            seekBarValue.setText(R.string.height511);
                            break;
                        case 15:
                            seekBarValue.setText(R.string.height512);
                            break;
                        case 30:
                            seekBarValue.setText(R.string.height60);
                            break;
                        case 45:
                            seekBarValue.setText(R.string.height61);
                            break;
                        case 60:
                            seekBarValue.setText(R.string.height62);
                            break;
                        case 75:
                            seekBarValue.setText(R.string.height63);
                            break;
                        case 90:
                            seekBarValue.setText(R.string.height64);
                            break;
                        case 105:
                            seekBarValue.setText(R.string.height65);
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            ImageButton next_btn = (ImageButton) rootView.findViewById(R.id.btn_next_male);
            next_btn.setOnClickListener(customListener);
            break;

            case "Female":
                // Inflate the layout for this fragment
                rootView = inflater.inflate(R.layout.fragment_scout_me_female,  container, false);

                ImageButton female_next_btn = (ImageButton) rootView.findViewById(R.id.btn_next_female);
                female_next_btn.setOnClickListener(customListener);

                SeekBar female_seekBar = (SeekBar) rootView.findViewById(R.id.seekBar2);
                female_seekBar.setProgress(0);
                female_seekBar.incrementProgressBy(15);
                female_seekBar.setMax(105);
                seekBarValue = (TextView) rootView.findViewById(R.id.female_height);
                seekBarValue.setText(seekBarValue.getText().toString());

                female_seekBar.setOnTouchListener(new SeekBar.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        int action = event.getAction();
                        switch (action)
                        {
                            case MotionEvent.ACTION_DOWN:
                                // Disallow ScrollView to intercept touch events.
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                                break;

                            case MotionEvent.ACTION_UP:
                                // Allow ScrollView to intercept touch events.
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }

                        // Handle Seekbar touch events.
                        v.onTouchEvent(event);
                        return true;
                    }
                });

                female_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress = progress / 15;
                        progress = progress * 15;
                        switch (progress) {
                            case 0:
                                seekBarValue.setText(R.string.height58);
                                break;
                            case 15:
                                seekBarValue.setText(R.string.height59);
                                break;
                            case 30:
                                seekBarValue.setText(R.string.height510);
                                break;
                            case 45:
                                seekBarValue.setText(R.string.height511);
                                break;
                            case 60:
                                seekBarValue.setText(R.string.height512);
                                break;
                            case 75:
                                seekBarValue.setText(R.string.height60);
                                break;
                            case 90:
                                seekBarValue.setText(R.string.height61);
                                break;
                            case 105:
                                seekBarValue.setText(R.string.height61);
                                break;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });
                break;
        }
        return rootView;
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_next_male:
                    editor.putString("Height", seekBarValue.getText().toString());
                    editor.apply();
                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();
                    trans.replace(R.id.root_scout_frame, new ScoutMeGenderUploadImageFragment());
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);
                    trans.commit();
                    break;
                case R.id.btn_next_female:
                    editor.putString("Height", seekBarValue.getText().toString());
                    editor.apply();
                    FragmentTransaction female_trans = getFragmentManager()
                            .beginTransaction();
                    female_trans.replace(R.id.root_scout_frame, new ScoutMeGenderUploadImageFragment());
                    female_trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    female_trans.addToBackStack(null);
                    female_trans.commit();
            }
        }
    };

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
                    trans.replace(R.id.street_style_container, new ScoutMeFragment());
                    trans.addToBackStack(null);
                    trans.commit();
                    return true;
                }
                return false;
            }
        });
    }
}
