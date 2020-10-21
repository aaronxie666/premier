package icn.premierandroid.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.squareup.picasso.Picasso;

import icn.premierandroid.MainActivity;
import icn.premierandroid.R;

import static icn.premierandroid.misc.CONSTANTS.CAM_SHARED_PREFS;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: About Premier Tab
 */

public class AboutPremierFragment extends Fragment{

    /** Init variables **/
    private RelativeLayout relativeLayout;
    private ImageView background_anim;
    private Animation animSlide;
    private ScrollView scrollView;

    public AboutPremierFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /** Animates Layout when activity has loaded all components **/
        new AboutAnimation().execute();
    }

    public static AboutPremierFragment newInstance(int imageNum) {
        final AboutPremierFragment f = new AboutPremierFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_premier, container, false);
            relativeLayout = (RelativeLayout) rootView.findViewById(R.id.layout_image);
            background_anim = (ImageView) rootView.findViewById(R.id.background_about_image);

        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        params.height = height;
        params.width = width;
        relativeLayout.setLayoutParams(params);

            ImageButton scrollButton = (ImageButton) rootView.findViewById(R.id.scroll_button);
            scrollButton.setOnClickListener(customListener);

            ImageButton terms_and_conditions_button = (ImageButton) rootView.findViewById(R.id.terms_and_conditions_button);
            terms_and_conditions_button.setOnClickListener(customListener);

            ImageButton faqs_button = (ImageButton) rootView.findViewById(R.id.FAQs_button);
            faqs_button.setOnClickListener(customListener);

            ImageButton partners_button = (ImageButton) rootView.findViewById(R.id.partners_button);
            partners_button.setOnClickListener(customListener);

            ImageButton icn_button = (ImageButton) rootView.findViewById(R.id.ICN_button);
            icn_button.setOnClickListener(customListener);

        ImageView imageViewOne = (ImageView) rootView.findViewById(R.id.about_image1);
        ImageView imageViewTwo = (ImageView) rootView.findViewById(R.id.about_image2);
        ImageView imageViewThree = (ImageView) rootView.findViewById(R.id.about_image3);
        Picasso.with(getActivity()).load(R.drawable.image1).fit().centerCrop().into(imageViewOne);
        Picasso.with(getActivity()).load(R.drawable.img2).fit().centerCrop().into(imageViewTwo);
        Picasso.with(getActivity()).load(R.drawable.img3).into(imageViewThree);
        imageViewThree.setOnClickListener(customListener);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

        return rootView;
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        public void onClick(View v) {
            FragmentTransaction trans = getFragmentManager()
                    .beginTransaction();
            switch (v.getId()) {
                case R.id.terms_and_conditions_button:
                    trans.replace(R.id.root_frame, new TermsAndConditionsFragment());
                    break;
                case R.id.FAQs_button:
                    trans.replace(R.id.root_frame, new FAQFragment());
                    break;
                case R.id.partners_button:
                    trans.replace(R.id.root_frame, new PartnersFragment());
                    break;
                case R.id.ICN_button:
                    trans.replace(R.id.root_frame, new ICNFragment());
                    break;
                case R.id.scroll_button:
                    ObjectAnimator anim = ObjectAnimator.ofInt(scrollView, "scrollY", relativeLayout.getBottom());
                    anim.setDuration(800);
                    anim.start();
                    break;
                case R.id.about_image3:
                    Uri uriUrl = Uri.parse("https://www.amazon.co.uk/Have-Said-Too-Much-Agency/dp/1780890729");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
            }

            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commit();
        }
    };

    private class AboutAnimation extends AsyncTask<Void, Void, Void> {

        AboutAnimation() {}

        @Override
        protected Void doInBackground(Void... params) {
            // Load the animation like this
            animSlide = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.about_slide);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Start the animation like this
            Picasso.with(getContext()).load(R.drawable.about_75).fit().into(background_anim);
        }

        @Override
        protected void onPostExecute (Void result) {
            relativeLayout.startAnimation(animSlide);
        }

    }
}
