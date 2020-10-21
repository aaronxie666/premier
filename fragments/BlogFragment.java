package icn.premierandroid.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.misc.CustomFontTextView;

import static org.jsoup.Jsoup.connect;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Blog Tab
 */
public class BlogFragment extends Fragment{
    private ArrayList<LinearLayout> columns = new ArrayList<>();
    private ArrayList<ImageView> images = new ArrayList<>();
    private ArrayList<CustomFontTextView> categories = new ArrayList<>();
    private ArrayList<CustomFontTextView> bodyTexts = new ArrayList<>();
    private ArrayList<CustomFontTextView> dates = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<String> blogUrls = new ArrayList<>();
    private ArrayList<String> catStrs = new ArrayList<>();
    private ArrayList<String> textStrs = new ArrayList<>();
    private ArrayList<String> dateStrs = new ArrayList<>();
    private int colSize;

    public BlogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blog, container, false);
        TableLayout bContainer = (TableLayout) rootView.findViewById(R.id.blog_container);
        initColumns(rootView);
        colSize = columns.size();

        for (int i = 0; i < colSize; i++) {
            final int finalI = i;
            columns.get(i).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Fragment udf = new BlogContentFragment();
                    Bundle args = new Bundle();
                    args.putString("blog_url", blogUrls.get(finalI));
                    args.putInt("page_entered", 2);
                    udf.setArguments(args);

                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();
                    trans.replace(R.id.root_blog_frame, udf);
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);
                    trans.commit();
                }
            });
        }
        new Title(bContainer).execute();
        return rootView;
    }

    private void initColumns(View rootView) {
        LinearLayout column1 = (LinearLayout) rootView.findViewById(R.id.column1);
        columns.add(column1);
        LinearLayout column2 = (LinearLayout) rootView.findViewById(R.id.column2);
        columns.add(column2);
        LinearLayout column3 = (LinearLayout) rootView.findViewById(R.id.column3);
        columns.add(column3);
        LinearLayout column4 = (LinearLayout) rootView.findViewById(R.id.column4);
        columns.add(column4);
        LinearLayout column5 = (LinearLayout) rootView.findViewById(R.id.column5);
        columns.add(column5);
        LinearLayout column6 = (LinearLayout) rootView.findViewById(R.id.column6);
        columns.add(column6);
        LinearLayout column7 = (LinearLayout) rootView.findViewById(R.id.column7);
        columns.add(column7);
        LinearLayout column8 = (LinearLayout) rootView.findViewById(R.id.column8);
        columns.add(column8);
        LinearLayout column9 = (LinearLayout) rootView.findViewById(R.id.column9);
        columns.add(column9);
    }

    public static BlogFragment newInstance(int imageNum) {
        final BlogFragment f = new BlogFragment();
        final Bundle args = new Bundle();
        args.putInt("Position", imageNum);
        f.setArguments(args);
        return f;
    }

    // Title AsyncTask
    private class Title extends AsyncTask<Void, Void, Void> {
        ImageView image = new ImageView(getActivity());
        TableLayout container;
        Bitmap bitmap;

        public Title(TableLayout container) {
            this.container = container;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            for (int i = 0; i < colSize; i++) {
                images.add(new ImageView(getActivity()));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
                params.gravity = Gravity.CENTER_HORIZONTAL;
                images.get(i).setLayoutParams(params);
                categories.add(new CustomFontTextView(getActivity()));
                categories.get(i).setTextSize(12);
                categories.get(i).setTextColor(Color.BLACK);
                bodyTexts.add(new CustomFontTextView(getActivity()));
                bodyTexts.get(i).setSingleLine(true);
                bodyTexts.get(i).setEllipsize(TextUtils.TruncateAt.END);
                bodyTexts.get(i).setTextColor(Color.BLACK);
                bodyTexts.get(i).setTextSize(20);
                LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                bodyTexts.get(i).setLayoutParams(bodyParams);
                dates.add(new CustomFontTextView(getActivity()));
                dates.get(i).setTextColor(Color.BLACK);
                dates.get(i).setTextSize(10);
                dates.get(i).setTypeface(null, Typeface.ITALIC);

                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) columns.get(i).getLayoutParams();
                params3.bottomMargin = 50;
                params3.leftMargin = 25;
                params3.rightMargin = 25;

            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = "http://www.premiermodelmanagement.com/blog/";
                Document doc = connect(url).get();

                for (Element m : doc.select(".blog-item > a")) {
                    String link = m.attr("abs:href");
                    blogUrls.add(link);
                }

                for (Element e : doc.select("img")) {
                        bitmap = getImageBitmap(e.attr("data-original"));
                        Log.e("data-original", e.attr("data-original"));
                        bitmaps.add(bitmap);
                }

                for (Element j : doc.select(".blog-item-category > p")) {
                    String text = j.text();
                    catStrs.add(text);
                }

                for (Element l : doc.select(".blog-item-text > p")) {
                    String text = l.text();
                    textStrs.add(text);
                }

                for (Element k : doc.select(".blog-item-date > p")) {
                    String text = k.text();
                    dateStrs.add(text);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap getImageBitmap(String url) {
            Bitmap myBitmap;
            try {
                URL src = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) src.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

            @Override
            protected void onPostExecute (Void result){
                for (int i = 0; i < colSize; i++) {
                    if (columns.get(i).getChildCount() != 0) {
                        columns.get(i).removeAllViews();
                    }
                }
                BuildTable();
            }

            private void BuildTable() {
                try {
                    for (int i = 0; i < colSize; i++) {
                        images.get(i).setImageBitmap(bitmaps.get(i));
                        categories.get(i).setText(catStrs.get(i));
                        bodyTexts.get(i).setText(textStrs.get(i));
                        dates.get(i).setText(dateStrs.get(i));
                        columns.get(i).addView(images.get(i));
                        columns.get(i).addView(categories.get(i));
                        columns.get(i).addView(bodyTexts.get(i));
                        columns.get(i).addView(dates.get(i));
                        Log.e("link", "" + blogUrls.get(i));
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
