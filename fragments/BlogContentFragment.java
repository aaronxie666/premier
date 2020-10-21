package icn.premierandroid.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.adapters.RecyclerViewAdapterBlogImages;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.LikesDialog;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_BLOG_FIVE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_BLOG_ONE;
import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_BLOG_TWO;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Detailed View of a blog that has been clicked
 */

public class BlogContentFragment extends Fragment {

    private TextView category, date, title, bodyText;
    private String url;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private String blog_date, blog_category, blog_title, blog_body;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private CallbackManager callbackManager;
    private final int BLOG_ID_ONE = 0, BLOG_ID_TWO = 1, BLOG_ID_THREE = 4;
    private int page_from;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blog_content, container, false);

        category = (TextView) rootView.findViewById(R.id.category_tv);
        date = (TextView) rootView.findViewById(R.id.date_tv);
        title = (TextView) rootView.findViewById(R.id.title_tv);
        bodyText = (TextView) rootView.findViewById(R.id.body_tv);

        url = getArguments().getString("blog_url", null);
        Log.e("url", url);

        page_from = getArguments().getInt("page_entered", 0);
        Log.e("page_entered", "" + page_from);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false );
        recyclerView.setLayoutManager(mLayoutManager);

        ImageButton emailShare = (ImageButton) rootView.findViewById(R.id.share_button_email);
        emailShare.setOnClickListener(customListener);

        ImageButton facebookShare = (ImageButton) rootView.findViewById(R.id.share_button_facebook);
        facebookShare.setOnClickListener(customListener);

        ImageButton twitterShare = (ImageButton) rootView.findViewById(R.id.share_button_twitter);
        twitterShare.setOnClickListener(customListener);
        return rootView;
    }

    private void updateBlogCount(final int length) {
        switch(length) {
            case BLOG_ID_ONE:
                showLikesDialog(0);
                break;
            case BLOG_ID_TWO:
                showLikesDialog(1);
                break;
            case BLOG_ID_THREE:
                showLikesDialog(4);
                break;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Last_Visited");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        try {
                            j.put("blogCount", length);
                            j.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void showLikesDialog(int i) {
        switch (i) {
            case BLOG_ID_ONE:
                LikesDialog likesDialogBlogOne = new LikesDialog(getActivity(),PARSE_ID_BLOG_ONE);
                likesDialogBlogOne.display();
                break;
            case BLOG_ID_TWO:
                LikesDialog likesDialogBlogTwo = new LikesDialog(getActivity(), PARSE_ID_BLOG_TWO);
                likesDialogBlogTwo.display();
                break;
            case BLOG_ID_THREE:
                LikesDialog likesDialogBlogThree = new LikesDialog(getActivity(), PARSE_ID_BLOG_FIVE);
                likesDialogBlogThree.display();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Category().execute();
        new Title().execute();
        new Date().execute();
        new Images().execute();
        new BodyText().execute();
    }

    // Title AsyncTask
    private class Category extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                blog_category = document.select(".post-text > h3").text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
           category.setText(blog_category);
        }
    }

    // Title AsyncTask
    private class Title extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                blog_title = document.select(".post-text > h2 > i").text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            title.setText(blog_title);
            if (AllLoginActivity.userType != UserType.userType.unsignedUser) {
                updateBlogsRead();
            }
        }
    }

    private void updateBlogsRead() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    final JSONArray blogs = objects.get(0).getJSONArray("blogsRead");
                    if (blogs != null) {
                        if (blogs.toString().contains(blog_title)) {
                            Log.e("blog", "has already been read");
                        } else {
                            objects.get(0).add("blogsRead", blog_title);
                            objects.get(0).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.e("success", "blogs read added");
                                        updateBlogCount(blogs.length());
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("blogs is null", "");
                    }
                } else {
                    Log.e("failed", "failed " + e.getMessage());
                }
            }
        });
    }

    // Title AsyncTask
    private class Date extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                blog_date = document.select(".post-date > p").text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            date.setText(blog_date);
        }
    }

    // Title AsyncTask
    private class Images extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            getImages();
            return null;
        }

        private void getImages() {
            // Connect to the web site
                try {
                    Document document = Jsoup.connect(url).get();
//                    for (Element e : document.select("img[src~=(?i)\\.(jpe?g)]")) {
                    for (Element e : document.select(".post .fade-on-ready-3 > img")) {
                        if(!e.attr("data-original").isEmpty()){
                            imageUrls.add(e.absUrl("data-original"));
                            Log.e("imageurls", imageUrls.toString());
                        }
                    }

                    for (Element e : document.select(".post .fade-on-ready-2 > p > img")) {
                        if(!e.attr("src").isEmpty()){
                            imageUrls.add(e.absUrl("src"));
                            Log.e("imageurls", imageUrls.toString());
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

//                for (Element e : document.select("img[src]")) {
//                    Log.e("normal", e.attr("abs:src"));
//                    imageUrls.add(e.absUrl("src"));
//                    Log.e("imageurls", imageUrls.toString());
//                }

//                for (Element e : document.select(".lazy")) {
//                    for (Element j : e.getElementsByTag("img")) {
//                        imageUrls.add(j.absUrl("src"));
//                        Log.e("imageurls", imageUrls.toString());
//                    }
//                }
        }

        @Override
        protected void onPostExecute(Void result) {
            int count = 0;
            for (int i = 0; i < imageUrls.size() + count; i++) {
                if (imageUrls.get(i).contains(".gif")) {
                    imageUrls.remove(i);
                    i = i - 1;
                }
            }
            Log.e("imageurls", imageUrls.toString());
            RecyclerViewAdapterBlogImages adapter = new RecyclerViewAdapterBlogImages(getActivity(), imageUrls);
            recyclerView.setAdapter(adapter);// set adapter on recyclerview
            adapter.notifyDataSetChanged();// Notify the adapter
            adapter.setOnItemClickListener(new RecyclerViewAdapterBlogImages.onRecyclerViewItemClickListener() {
                @Override
                public void onItemClickListener(View view, int position, String s) {showLikesDialog();
                }
            });
        }

        private void showLikesDialog() {
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fragment_blog_preview_image);

            new DisplayImages().execute();
            recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false );
            recyclerView.setLayoutManager(mLayoutManager);

            ImageButton dialogButtonCancel = (ImageButton) dialog.findViewById(R.id.close_button);
            // Click cancel to dismiss android custom dialog box
            dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }

        // Title AsyncTask
        private class DisplayImages extends AsyncTask<Void, Void, Void> {
            ArrayList<String> recyclerImageUrls = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Document document = Jsoup.connect(url).get();
//                    for (Element e : document.select("img[src~=(?i)\\.(jpe?g)]")) {
                    for (Element e : document.select(".post .fade-on-ready-3 > img")) {
                        if(!e.attr("data-original").isEmpty()){
                            recyclerImageUrls.add(e.absUrl("data-original"));
                            Log.e("imageurls", imageUrls.toString());
                        }
                    }

                    for (Element e : document.select(".post-wrap .fade-on-ready-2 > p > img")) {
                        if(!e.attr("src").isEmpty()){
                            recyclerImageUrls.add(e.absUrl("src"));
                            Log.e("imageurls", imageUrls.toString());
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                int count = 0;
                for (int i = 0; i < recyclerImageUrls.size() + count; i++) {
                    if (recyclerImageUrls.get(i).contains(".gif")) {
                        recyclerImageUrls.remove(i);
                        i = i - 1;
                    }
                }
                RecyclerViewAdapterBlogImages adapter = new RecyclerViewAdapterBlogImages(getActivity(), recyclerImageUrls);
                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        }
    }

    // Title AsyncTask
    private class BodyText extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(url).get();
                // Get the html document title
                blog_body = document.select(".post-text > p").text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            bodyText.setText(blog_body);
        }
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.share_button_email:
                    String newline = System.getProperty("line.separator");
                    String body = "Hi," + newline + newline + "I thought you would be interested in checking out this article from Premier Model Management's blog: " + url;
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
                    emailIntent.setType("text/html");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, blog_title);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    break;
                case R.id.share_button_facebook:
                    callbackManager = CallbackManager.Factory.create();
                    ShareDialog shareDialog = new ShareDialog(getActivity());
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(url))
                                .build();
                        shareDialog.show(linkContent);
                    }
                    break;
                case R.id.share_button_twitter:
                    try {
                        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                                urlEncode("Check out this article from the Premier Blog "),
                                urlEncode(url));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

                        List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                        for (ResolveInfo info : matches) {
                            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                                intent.setPackage(info.activityInfo.packageName);
                            }
                        }
                        startActivity(intent);
                    } catch (final ActivityNotFoundException e) {
                        Log.i("twitter", "no twitter native", e);
                    }
                    break;
            }
        }
    };

    private String urlEncode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Log.wtf("TAG", "UTF-8 should always be supported", e);
                throw new RuntimeException("URLEncoder.encode() failed for " + s);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        final Fragment f = this;

        if(getView() == null){
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if (page_from == 2) {
                        FragmentTransaction trans = getFragmentManager().beginTransaction();
                        trans.replace(R.id.root_blog_frame, new BlogFragment());
                        trans.addToBackStack(null);
                        trans.commit();
                        return true;
                    } else {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
