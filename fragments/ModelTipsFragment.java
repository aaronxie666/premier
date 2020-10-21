package icn.premierandroid.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterHomeLatestVideos;
import icn.premierandroid.models.HomeLatestVideosDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Model Tips Tab
 */

public class ModelTipsFragment extends Fragment{

    protected RecyclerView recyclerView;
    private TextView tips_title, empty_message;

    public ModelTipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_model_tips_home, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        tips_title = (TextView) rootView.findViewById(R.id.model_tips_title);
        empty_message = (TextView) rootView.findViewById(R.id.empty_message);
        empty_message.setVisibility(View.GONE);

        tips_title.setText(R.string.latest_videos);
        populateListModelTipsHome();

        BottomBar model_tips_nav = (BottomBar) rootView.findViewById(R.id.bottomBar);
        model_tips_nav.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
               public void onTabSelected(@IdRes int tabId) {
                switch(tabId) {
                    case R.id.tab_hair:
                        tips_title.setText(R.string.hair_title);
                        populateListModelTipsHair();
                        break;
                    case R.id.tab_health:
                        tips_title.setText(R.string.Health_title);
                        populateListModelTipsHealth();
                        break;
                    case R.id.tab_wardrobe:
                        tips_title.setText(R.string.Wardrobe_title);
                        populateListModelTipsWardrobe();
                        break;
                    case R.id.tab_life:
                        tips_title.setText(R.string.life_title);
                        populateListModelTipsLife();
                        break;
                    case R.id.tab_makeup:
                        tips_title.setText(R.string.makeup_title);
                        populateListModelTipsMakeup();
                        break;
                }
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void populateListModelTipsHealth() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tips_Nutrition");
        finishQuery(query);
    }

    private void populateListModelTipsHair() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tips_Hair");
        finishQuery(query);
    }

    private void populateListModelTipsWardrobe() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tips_Wardrobe");
        finishQuery(query);
    }

    private void populateListModelTipsLife() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tips_Life");
        finishQuery(query);
    }

    private void populateListModelTipsMakeup() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tips_Latest");
        finishQuery(query);
    }

    private void populateListModelTipsHome() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tips_Latest");
        finishQuery(query);
    }

    private void finishQuery(ParseQuery<ParseObject> query) {
        Log.e("get query", "gets query");
        query.orderByAscending("createdAt");
        Log.e("get order", "ordered");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (objects == null || objects.size() == 0) {
                    empty_message.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else if (objects.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_message.setVisibility(View.GONE);
                    if (e == null) {
                        Log.e("does it get here", "it got here");
                        ArrayList<HomeLatestVideosDataModel> arrayList = new ArrayList<>();
                        for (ParseObject j : objects) {
                            ParseFile image = (ParseFile) j.get("image");
                            String video_link = (String) j.get("link");
                            arrayList.add(new HomeLatestVideosDataModel(image.getUrl(), video_link));
                        }
                        RecyclerViewAdapterHomeLatestVideos adapter = new RecyclerViewAdapterHomeLatestVideos(getActivity(), arrayList, "Model");
                        recyclerView.setAdapter(adapter);// set adapter on recyclerview
                        adapter.notifyDataSetChanged();// Notify the adapter
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            }
        });
    }

    public static ModelTipsFragment newInstance(int imageNum) {
        final ModelTipsFragment f = new ModelTipsFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }
}
