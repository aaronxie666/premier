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
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterModelWorld;
import icn.premierandroid.models.ModelWorldDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Model World Tab
 */

public class ModelWorldFragment extends Fragment{

    private ArrayList<ModelWorldDataModel> models;
    private RecyclerView recyclerView;
    ImageView first_image, second_image, third_image;

    public ModelWorldFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_model_world, container, false);

        first_image = (ImageView) rootView.findViewById(R.id.model_image_one);
        second_image = (ImageView) rootView.findViewById(R.id.model_image_two);
        third_image = (ImageView) rootView.findViewById(R.id.model_image_three);

        populateRecyclerView();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }


    private void populateRecyclerView() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("instaAccounts");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.e("gets done", "gets into done");
                if (e == null) {
                    Log.e("does it get here", "it got here");
                    models = new ArrayList<>();
                    for (ParseObject j : objects) {
                        ParseFile profile_image = (ParseFile) j.get("profilePictureImage");
                        String model_name = (String) j.get("name");
                        String model_username = (String) j.get("username");
                        ParseFile first_image = (ParseFile) j.get("image1");
                        ParseFile second_image = (ParseFile) j.get("image2");
                        ParseFile third_image = (ParseFile) j.get("image3");
                        ArrayList<String> imageUrls = new ArrayList<>();
                        imageUrls.add(first_image.getUrl());
                        imageUrls.add(second_image.getUrl());
                        imageUrls.add(third_image.getUrl());
                        models.add(new ModelWorldDataModel(profile_image.getUrl(), model_name, model_username, imageUrls));
                    }
                }
                else {
                    Log.e("failed", "failed" + e.getMessage());
                }

                RecyclerViewAdapterModelWorld adapter = new RecyclerViewAdapterModelWorld(getActivity(), models);
                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        });
    }

    public static ModelWorldFragment newInstance(int imageNum) {
        final ModelWorldFragment f = new ModelWorldFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }
}
