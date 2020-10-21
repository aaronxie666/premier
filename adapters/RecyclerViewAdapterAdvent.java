package icn.premierandroid.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import icn.premierandroid.R;
import icn.premierandroid.VideoActivity;
import icn.premierandroid.misc.CustomFontTextView;
import icn.premierandroid.models.AdventModel;

public class RecyclerViewAdapterAdvent extends RecyclerView.Adapter<RecyclerViewHolderAdvent> {

    private ArrayList<AdventModel> adventListings;
    private Context context;
    private final String VIDEO = "Video", CODE = "Code", POINTS = "Points;";
    private ViewPager viewPager;

    public RecyclerViewAdapterAdvent(Context context, ArrayList<AdventModel> adventListings, ViewPager viewPager) {
        this.adventListings = adventListings;
        this.context = context;
        this.viewPager = viewPager;
    }

    @Override
    public RecyclerViewHolderAdvent onCreateViewHolder(ViewGroup parent, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.advent_calendar_item_row, parent, false);
        return new RecyclerViewHolderAdvent(mainGroup);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolderAdvent holder, int position) {

       final AdventModel model = adventListings.get(position);
       holder.day_number.setText(String.valueOf(model.getPosition() + 1));

        if (position == 0 && ParseUser.getCurrentUser().getBoolean("hasUserRedeemedAdvent")) {
            holder.background.setImageResource(R.drawable.btn_day_grey);
        } else if (position == 0 && !ParseUser.getCurrentUser().getBoolean("hasUserRedeemedAdvent")){
                holder.background.setImageResource(R.drawable.btn_day_white);
                holder.day_number.setTextColor(Color.BLACK);
                holder.day_text.setTextColor(Color.BLACK);
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = "";
                        if (model.getPoints()) {
                            type = POINTS;
                        } else if (model.getCode()) {
                            type = CODE;
                        } else if (model.getVideo()) {
                            type = VIDEO;
                        }
                        showDialog(type, model);
                    }
                });
        }

        if (position != 0) {
            holder.background.setImageResource(R.drawable.btn_day_black);
            holder.day_number.setTextColor(Color.WHITE);
            holder.day_text.setTextColor(Color.WHITE);
        }
    }

    private void showDialog(final String type, final AdventModel model) {
        // custom dialog
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_advent_dialog);
        switch(type) {
            case POINTS:
                LinearLayout likes_container = (LinearLayout) dialog.findViewById(R.id.likes_container_advent);
                LinearLayout likes_button_container = (LinearLayout) dialog.findViewById(R.id.likes_button_container);
                CustomFontTextView likes_amount = (CustomFontTextView) dialog.findViewById(R.id.likes_amount_advent);
                CustomFontTextView likes_description = (CustomFontTextView) dialog.findViewById(R.id.likes_advent_description);
                likes_container.setVisibility(View.VISIBLE);
                likes_button_container.setVisibility(View.VISIBLE);
                likes_amount.setText(String.valueOf(model.getAwardPoints()));
                likes_description.setText(model.getLikes_description());
                ImageView dialogButtonCancel = (ImageView) dialog.findViewById(R.id.likes_return_button);
                ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.likes_likes_button);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(5);
                        int points = ParseUser.getCurrentUser().getInt("points");
                        int totalPoints = model.getAwardPoints() + points;
                        ParseUser.getCurrentUser().put("points", totalPoints);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseUser.getCurrentUser().put("hasUserRedeemedAdvent", true);
                                    ParseUser.getCurrentUser().saveEventually();
                                } else {
                                    Log.e("failed", "failed" + e.getMessage());
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case CODE:
                LinearLayout discount_container = (LinearLayout) dialog.findViewById(R.id.discount_container);
                LinearLayout discount_button_container = (LinearLayout) dialog.findViewById(R.id.discount_button_container);
                CustomFontTextView discount_code = (CustomFontTextView) dialog.findViewById(R.id.discount_code_advent);
                CustomFontTextView code_description = (CustomFontTextView) dialog.findViewById(R.id.discount_description_advent);
                ImageView company_logo = (ImageView) dialog.findViewById(R.id.company_logo);
                discount_container.setVisibility(View.VISIBLE);
                discount_button_container.setVisibility(View.VISIBLE);
                discount_code.setText(model.getDiscountCode());
                code_description.setText(model.getCodeDescription());
                Picasso.with(context).load(model.getCompanyLogoUrl()).into(company_logo);
                ImageView dialogButtonNormalCancel = (ImageView) dialog.findViewById(R.id.discount_return);
                ImageView dialogButtonNormalOk = (ImageView) dialog.findViewById(R.id.discount_visit_website);
                // Click cancel to dismiss android custom dialog box
                dialogButtonNormalCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonNormalOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getWebsiteUrl()));
                        context.startActivity(browserIntent);
                    }
                });

                dialog.show();
                break;
            case VIDEO:
                LinearLayout video_container = (LinearLayout) dialog.findViewById(R.id.video_container_advent);
                LinearLayout video_button_container = (LinearLayout) dialog.findViewById(R.id.video_button_container);
                ImageView video_thumbnail = (ImageView) dialog.findViewById(R.id.video_thumbnail_advent);
                video_container.setVisibility(View.VISIBLE);
                video_button_container.setVisibility(View.VISIBLE);
                Picasso.with(context).load(model.getVideoThumbnailUrl()).into(video_thumbnail);
                ImageView dialogButtonPremierCancel = (ImageView) dialog.findViewById(R.id.video_return_button);
                ImageView dialogButtonPremierOk = (ImageView) dialog.findViewById(R.id.video_watch_video_button);
                // Click cancel to dismiss android custom dialog box
                dialogButtonPremierCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonPremierOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(context, VideoActivity.class);
                        myIntent.putExtra("VIDEO_URL", model.getVideoUrl());
                        context.startActivity(myIntent);
                    }
                });

                dialog.show();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != adventListings ? adventListings.size() : 0);
    }
}
