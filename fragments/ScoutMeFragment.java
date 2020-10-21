package icn.premierandroid.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import icn.premierandroid.R;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Scout Me Tab
 */

public class ScoutMeFragment extends Fragment {

    private SharedPreferences.Editor editor;

    public ScoutMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scout_me_home, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        ImageButton male_button = (ImageButton) rootView.findViewById(R.id.male_button);
        male_button.setOnClickListener(customListener);

        ImageButton female_button = (ImageButton) rootView.findViewById(R.id.female_button);
        female_button.setOnClickListener(customListener);

        return rootView;
    }

    public static ScoutMeFragment newInstance(int imageNum) {
        final ScoutMeFragment f = new ScoutMeFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.male_button:
                    Log.e("got into male button", "got here");
                    editor.putString("Gender", "Male");
                    editor.apply();
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();
                    trans.replace(R.id.root_scout_frame, new ScoutMeGenderFragment());
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);
                    trans.commit();
                    break;
                case R.id.female_button:
                    editor.putString("Gender", "Female");
                    editor.apply();
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction trans1 = getFragmentManager()
                            .beginTransaction();
                    trans1.replace(R.id.root_scout_frame, new ScoutMeGenderFragment());
                    trans1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans1.addToBackStack(null);
                    trans1.commit();
                    break;
            }
        }
    };

}
