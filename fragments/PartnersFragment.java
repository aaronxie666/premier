package icn.premierandroid.fragments;

import android.os.Bundle;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterHomeLatestVideos;
import icn.premierandroid.models.HomeLatestVideosDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Partners Button press in AboutPremierFragment
 */

public class PartnersFragment extends Fragment {

    protected RecyclerView recyclerView;
    private ArrayList<HomeLatestVideosDataModel> partnersList;

    public PartnersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about_premier_partners, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateRecyclerView();
    }

    private void populateRecyclerView() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Partners");
        Log.e("get query", "gets query");
        query.orderByAscending("createdAt");
        Log.e("get order", "ordered");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if(e == null) {
                    Log.e("does it get here", "it got here");
                    partnersList = new ArrayList<>();
                    for (ParseObject j : objects) {
                        ParseFile image = (ParseFile) j.get("image");
                        String website_link = (String) j.get("websiteLink");
                        partnersList.add(new HomeLatestVideosDataModel(image.getUrl(), website_link));
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

                RecyclerViewAdapterHomeLatestVideos adapter = new RecyclerViewAdapterHomeLatestVideos(getActivity(), partnersList, "Partners");
                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });
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
                    trans.replace(R.id.root_frame, new AboutPremierFragment());
                    trans.addToBackStack(null);
                    trans.commit();
                    return true;
                }
                return false;
            }
        });
    }


}
