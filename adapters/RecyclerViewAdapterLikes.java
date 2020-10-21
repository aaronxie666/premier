package icn.premierandroid.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import icn.premierandroid.MainActivity;
import icn.premierandroid.R;
import icn.premierandroid.misc.SendMailTask;
import icn.premierandroid.models.CustomRelativeLayout;
import icn.premierandroid.models.LikesDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside LikesFragment
 */

public class RecyclerViewAdapterLikes extends
        RecyclerView.Adapter<RecyclerViewAdapterLikes.RecyclerViewHolderLikes> {

    // recyclerview adapter
    private ArrayList<LikesDataModel> arrayList;
    private Context context;
    private LikesDataModel model;
    private ArrayList<String> redeem_urls = new ArrayList<>();
    private ArrayList<String> codeView_urls = new ArrayList<>();
    private ArrayList<String> moreInfo_urls = new ArrayList<>();
    private ArrayList<String> finalImage_urls = new ArrayList<>();
    private ArrayList<String> object_ids = new ArrayList<>();
    private ArrayList<String> web_urls = new ArrayList<>();
    private ArrayList<String> codes = new ArrayList<>();
    private ArrayList<Integer> points = new ArrayList<>();
    private final String normalComp = "normal", specialComp = "special", discounted = "discount", multiCodes = "multi";
    private String randomCode;
    private ArrayList<Boolean> multipleCodes = new ArrayList<>();


    public RecyclerViewAdapterLikes(Context context,
                                    ArrayList<LikesDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount () {
        return (null != arrayList ? arrayList.size() : 0);
    }

    @Override
    public void onBindViewHolder (RecyclerViewHolderLikes holder, final int position){
        model = arrayList.get(position);
        RecyclerViewHolderLikes mainHolder = (RecyclerViewHolderLikes) holder;
            Picasso.with(context).load(model.getImageFileUrl()).into(mainHolder.imageview);
                redeem_urls.add(model.getRedeemFileUrl());
                codeView_urls.add(model.getCodeFileUrl());
                moreInfo_urls.add(model.getMoreInfoFileUrl());
                finalImage_urls.add(model.getFinalImageFileUrl());
                object_ids.add(model.getObjectid());
                web_urls.add(model.getWebsite_url());
                if (!model.isMultiCodes()) {
                    codes.add(model.getCode());
                }
                points.add(model.getPoints());
                Log.e("redeem", redeem_urls.toString());
                Log.e("order", "" + model.getOrder());
                multipleCodes.add(model.isMultiCodes());

                if (model.isCompetition() && !model.isSpecialCompetition()) {
                    mainHolder.comp_container.setVisibility(View.VISIBLE);
                }

                if (model.isCompetition() && model.isSpecialCompetition()) {
                    mainHolder.special_comp_container.setVisibility(View.VISIBLE);
                }

                if (model.isUrl()) {
                    mainHolder.discount_container.setVisibility(View.VISIBLE);
                }

                if (model.isCode() || model.isMultiCodes()) {
                    mainHolder.redeem_container.setVisibility(View.VISIBLE);
                }
    }

    @Override
    public RecyclerViewHolderLikes onCreateViewHolder (ViewGroup viewGroup, int viewType){
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.likes_item_row, viewGroup, false);
        return new RecyclerViewHolderLikes(mainGroup);
    }

    class RecyclerViewHolderLikes  extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageview, d_more_info, r_more_info, redeem, discount, enter_comp, enter_special_comp;
        private RecyclerViewHolderImageOnly.onRecyclerViewItemClickListener mItemClickListener;
        LinearLayout special_comp_container, comp_container, redeem_container, discount_container;
        private CustomRelativeLayout redeem_comp_container, code_comp_container;

        RecyclerViewHolderLikes(View view) {
            super(view);
            this.special_comp_container = (LinearLayout) view
                    .findViewById(R.id.enter_special_comp_container);
            this.comp_container = (LinearLayout) view
                    .findViewById(R.id.enter_comp_container);
            this.redeem_container = (LinearLayout) view
                    .findViewById(R.id.redeem_container);
            this.discount_container = (LinearLayout) view
                    .findViewById(R.id.discount_container);
            this.imageview = (ImageView) view
                    .findViewById(R.id.imageLikes);
            this.d_more_info = (ImageView) view
                    .findViewById(R.id.d_more_info_button);
            d_more_info.setOnClickListener(this);

            this.r_more_info = (ImageView) view
                    .findViewById(R.id.r_more_info_button);
            r_more_info.setOnClickListener(this);

            this.redeem = (ImageView) view
                    .findViewById(R.id.reedem_button);
            redeem.setOnClickListener(this);

            this.discount = (ImageView) view
                    .findViewById(R.id.discount_button);
            discount.setOnClickListener(this);

            this.enter_comp = (ImageView) view
                    .findViewById(R.id.enter_competition_button);
            enter_comp.setOnClickListener(this);

            this.enter_special_comp = (ImageView) view
                    .findViewById(R.id.enter_special_competition_button);
            enter_special_comp.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.enter_competition_button:
                    if (model.isAnon()) {
                        Toast.makeText(context, "You have to be logged in to enter a competition.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("clicked", "clicked");
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("CompetitionSubmissions");
                        ParseObject object = ParseObject.createWithoutData("Likes", object_ids.get(getAdapterPosition()));
                        query.whereEqualTo("competition", object);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (!objects.isEmpty()) {
                                        if (objects.get(0).get("user") != ParseUser.getCurrentUser()) {
                                            showRedeemDialog(redeem_urls.get(getAdapterPosition()), getAdapterPosition(), normalComp);
                                        } else {
                                            showFinalImage(finalImage_urls.get(getAdapterPosition()));
                                        }
                                    } else {
                                        showRedeemDialog(redeem_urls.get(getAdapterPosition()), getAdapterPosition(), normalComp);
                                    }
                                } else {
                                    Log.e("failed", e.getMessage());
                                }
                            }
                        });
                    }
                    break;
                case R.id.enter_special_competition_button:
                    if (model.isAnon()) {
                        Toast.makeText(context, "You have to be logged in to enter a competition.", Toast.LENGTH_SHORT).show();
                    } else {
                        final ParseQuery<ParseObject> special_query = ParseQuery.getQuery("CompetitionSubmissions");
                        ParseObject object1 = ParseObject.createWithoutData("Likes", object_ids.get(getAdapterPosition()));
                        special_query.whereEqualTo("competition", object1);
                        special_query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (!objects.isEmpty()) {
                                        if (objects.get(0).get("user") != ParseUser.getCurrentUser()) {
                                            showRedeemDialog(redeem_urls.get(getAdapterPosition()), getAdapterPosition(), specialComp);
                                        } else {
                                            showFinalImage(finalImage_urls.get(getAdapterPosition()));
                                        }
                                    } else {
                                        showRedeemDialog(redeem_urls.get(getAdapterPosition()), getAdapterPosition(), specialComp);
                                    }
                                } else {
                                    Log.e("failed", e.getMessage());
                                }
                            }
                        });
                    }
                    break;
                case R.id.d_more_info_button:
                        showMoreInfoImage(moreInfo_urls.get(getAdapterPosition()), true);
                    break;
                case R.id.r_more_info_button:
                        showMoreInfoImage(moreInfo_urls.get(getAdapterPosition()), false);
                    break;
                case R.id.reedem_button:
                    if (model.isAnon()) {
                    Toast.makeText(context, "You have to be logged in to enter a competition.", Toast.LENGTH_SHORT).show();
                    } else {
                        ParseQuery<ParseUser> points_query = ParseUser.getQuery();
                        points_query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                        points_query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject j : objects) {
                                        int totalPoints = j.getInt("points");
                                        if (totalPoints >= points.get(getAdapterPosition())) {
//                                        int newTotalPoints = totalPoints - points.get(getAdapterPosition());
//                                        j.put("points", newTotalPoints);
                                            showRedeemDialog(redeem_urls.get(getAdapterPosition()), getAdapterPosition(), discounted);
//                                        j.saveInBackground(new SaveCallback() {
//                                            @Override
//                                            public void done(ParseException e) {
//                                                if (e == null) {
//                                                } else {
//                                                    Log.e("failed", e.getMessage());
//                                                }
//                                            }
//                                        });
                                        } else {
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                            builder1.setMessage("You don't have enough points to redeem this offer");
                                            builder1.setCancelable(true);
                                            builder1.setPositiveButton(
                                                    "Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            builder1.setNegativeButton(
                                                    "Cancel",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                            alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                            alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                        }
                                    }
                                } else {
                                    Log.e("failed", e.getMessage());
                                }
                            }
                        });
                    }
                    break;
                case R.id.discount_button:
                    if (model.isAnon()) {
                        Toast.makeText(context, "You have to be logged in to enter a competition.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(web_urls.get(getAdapterPosition())));
                        context.startActivity(browserIntent);
                    }
                    break;
            }
        }

        private void showMoreInfoImage(String url, boolean discount) {
            // custom dialog
            if (discount) {
                final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_competition_dialog);
                final CustomRelativeLayout comp_container = (CustomRelativeLayout) dialog.findViewById(R.id.redeem_comp_container);
                Picasso.with(context).load(url).into(comp_container);
                ImageView dialogButtonCancel = (ImageView) dialog.findViewById(R.id.close_button);
                dialogButtonCancel.setVisibility(View.VISIBLE);
                dialogButtonCancel.bringToFront();
                ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.button_next_comp_redeem);
                dialogButtonOk.setVisibility(View.INVISIBLE);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        comp_container.setBackgroundResource(0);
                    }
                });

                dialog.show();
            } else {
                final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_competition_dialog);
                final CustomRelativeLayout comp_container = (CustomRelativeLayout) dialog.findViewById(R.id.redeem_comp_container);
                Picasso.with(context).load(url).into(comp_container);
                ImageView dialogButtonCancel = (ImageView) dialog.findViewById(R.id.close_button);
                dialogButtonCancel.setVisibility(View.VISIBLE);
                dialogButtonCancel.bringToFront();
                ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.button_next_comp_redeem);
                dialogButtonOk.setVisibility(View.INVISIBLE);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        comp_container.setBackgroundResource(0);
                    }
                });

                dialog.show();
            }
        }

        private void showRedeemDialog(String imageUrl, final int i, final String type) {
            // custom dialog
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_competition_dialog);

            final CustomRelativeLayout comp_container = (CustomRelativeLayout) dialog.findViewById(R.id.redeem_comp_container);
            LinearLayout container = (LinearLayout) dialog.findViewById(R.id.next_container);
            LinearLayout premier_container = (LinearLayout) dialog.findViewById(R.id.premier_next_container);
            switch(type) {
                case specialComp:
                    container.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(imageUrl).into(comp_container);
                    ImageView dialogButtonCancel = (ImageView) dialog.findViewById(R.id.button_cancel_comp_redeem);
                    ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.button_next_comp_redeem);
                    // Click cancel to dismiss android custom dialog box
                    dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            comp_container.setBackgroundResource(0);
                        }
                    });

                    // Your android custom dialog ok action
                    // Action for custom dialog ok button click
                    dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCodeViewComp(codeView_urls.get(i), type);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                case normalComp:
                    container.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(imageUrl).into(comp_container);
                    ImageView dialogButtonNormalCancel = (ImageView) dialog.findViewById(R.id.button_cancel_comp_redeem);
                    ImageView dialogButtonNormalOk = (ImageView) dialog.findViewById(R.id.button_next_comp_redeem);
                    // Click cancel to dismiss android custom dialog box
                    dialogButtonNormalCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            comp_container.setBackgroundResource(0);
                        }
                    });

                    // Your android custom dialog ok action
                    // Action for custom dialog ok button click
                    dialogButtonNormalOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCodeViewComp(codeView_urls.get(i), type);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    break;
                case discounted:
                    premier_container.setVisibility(View.VISIBLE);
                    container.setVisibility(View.INVISIBLE);
                    Picasso.with(context).load(imageUrl).into(comp_container);
                    ImageView dialogButtonPremierCancel = (ImageView) dialog.findViewById(R.id.button_premier_cancel_comp_redeem);
                    ImageView dialogButtonPremierOk = (ImageView) dialog.findViewById(R.id.button_premier_next_comp_redeem);
                    // Click cancel to dismiss android custom dialog box
                    dialogButtonPremierCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            comp_container.setBackgroundResource(0);
                        }
                    });

                    // Your android custom dialog ok action
                    // Action for custom dialog ok button click
                    dialogButtonPremierOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParseQuery<ParseUser> points_query = ParseUser.getQuery();
                            points_query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                            points_query.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject j : objects) {
                                            int totalPoints = j.getInt("points");
                                            int newTotalPoints = totalPoints - points.get(getAdapterPosition());
                                            j.put("points", newTotalPoints);
                                            j.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        showCodeViewComp(codeView_urls.get(i), type);
                                                        dialog.dismiss();
                                                    } else {
                                                        Log.e("failed", e.getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Log.e("failed", e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                    break;
            }
        }

        private void showCodeViewComp(String imageUrl, final String type) {
            if (type.equals(specialComp) || type.equals(normalComp)) {
                final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_competition_dialog);

                code_comp_container = (CustomRelativeLayout) dialog.findViewById(R.id.redeem_comp_container);
                final RadioGroup toggle = (RadioGroup) dialog.findViewById(R.id.toggle_jackets);
                final EditText enter_instagram = (EditText) dialog.findViewById(R.id.instagram_codeview);
                enter_instagram.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(enter_instagram.getWindowToken(), 0);
                        }
                        return false;
                    }
                });
                enter_instagram.setVisibility(View.VISIBLE);
                if (type.equals(specialComp)) {
                    toggle.setVisibility(View.VISIBLE);
                }
                Picasso.with(context).load(imageUrl).into(code_comp_container);
                LinearLayout next_container = (LinearLayout) dialog.findViewById(R.id.next_container);
                next_container.setVisibility(View.VISIBLE);
                ImageView dialogButtonCancel = (ImageView) dialog.findViewById(R.id.button_cancel_comp_redeem);
                ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.button_next_comp_redeem);
                final RadioButton female = (RadioButton) dialog.findViewById(R.id.female);
                final RadioButton male = (RadioButton) dialog.findViewById(R.id.male);
                female.setTextColor(Color.BLACK);
                male.setTextColor(Color.WHITE);
                female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            female.setTextColor(Color.WHITE);
                        } else {
                            female.setTextColor(Color.BLACK);
                        }
                    }
                });

                male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            male.setTextColor(Color.WHITE);
                        } else {
                            male.setTextColor(Color.BLACK);
                        }
                    }
                });
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
                        switch (type) {
                            case specialComp:
                                if (enter_instagram.getText().toString().isEmpty()) {
                                    // enter_instagram.setError("Please enter your username");
                                    enter_instagram.setError(Html.fromHtml("<font color='black'>Please enter your instagram</font>"));
                                } else {
                                    String instagram_user = enter_instagram.getText().toString();
                                    int selectedId = toggle.getCheckedRadioButtonId();
                                    final RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
                                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if (isChecked) {
                                                radioButton.setTextColor(Color.WHITE);
                                            }
                                        }
                                    });
                                    String radioValue = radioButton.getText().toString();
                                    ParseObject competition = ParseObject.createWithoutData("Likes", object_ids.get(getAdapterPosition()));
                                    ParseObject competition_entry = new ParseObject("CompetitionSubmissions");
                                    competition_entry.put("gender", radioValue);
                                    competition_entry.put("instagramUsername", instagram_user);
                                    competition_entry.put("user", ParseUser.getCurrentUser());
                                    competition_entry.put("competition", competition);
                                    competition_entry.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(context, "Competition Submitted", Toast.LENGTH_SHORT).show();
                                                showFinalImage(finalImage_urls.get(getAdapterPosition()));
                                                dialog.dismiss();
                                                Map<String, String> articleParams = new HashMap<String, String>();
                                                //param keys and values have to be of String type
                                                articleParams.put("Competition Submitted", "User submitted a competition");
                                                //up to 10 params can be logged with each event
                                                AppEventsLogger logger = AppEventsLogger.newLogger(context);
                                                logger.logEvent("Comepetition Submitted");
                                                FlurryAgent.logEvent("Competition Submitted", articleParams);
                                            } else {
                                                Toast.makeText(context, "Competition Submission Failed", Toast.LENGTH_SHORT).show();
                                                Log.e("failed", e.getMessage());
                                            }
                                        }
                                    });
                                }
                                break;
                            case normalComp:
                                if (enter_instagram.getText().toString().isEmpty()) {
                                    enter_instagram.setError("Please enter your username");
                                } else {
                                    String instagram_user = enter_instagram.getText().toString();
                                    ParseObject competition = ParseObject.createWithoutData("Likes", object_ids.get(getAdapterPosition()));
                                    ParseObject competition_entry = new ParseObject("CompetitionSubmissions");
                                    competition_entry.put("instagramUsername", instagram_user);
                                    competition_entry.put("user", ParseUser.getCurrentUser());
                                    competition_entry.put("competition", competition);
                                    competition_entry.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(context, "Competition Submitted", Toast.LENGTH_SHORT).show();
                                                showFinalImage(finalImage_urls.get(getAdapterPosition()));
                                                dialog.dismiss();
                                                Map<String, String> articleParams = new HashMap<String, String>();
                                                //param keys and values have to be of String type
                                                articleParams.put("Competition Submitted", "User submitted a competition");
                                                //up to 10 params can be logged with each event
                                                AppEventsLogger logger = AppEventsLogger.newLogger(context);
                                                logger.logEvent("Competition Submitted");
                                                FlurryAgent.logEvent("Competition Submitted", articleParams);
                                            } else {
                                                Toast.makeText(context, "Competition Submission Failed", Toast.LENGTH_SHORT).show();
                                                Log.e("failed", e.getMessage());
                                            }
                                        }
                                    });
                                }
                                break;
                        }
                    }
                });

                dialog.show();
            } else {
                final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_competition_dialog);
                code_comp_container = (CustomRelativeLayout) dialog.findViewById(R.id.redeem_comp_container);
                final TextView enter_instagram = (TextView) dialog.findViewById(R.id.instagram_copy);
                enter_instagram.setVisibility(View.VISIBLE);
                enter_instagram.bringToFront();
                if (multipleCodes.get(getAdapterPosition())) {
                    generateRandomCode(getAdapterPosition(), enter_instagram);
                } else {
                    String code;
                    if (getAdapterPosition() == 2) {
                        code = "premier50";
                    } else {
                        code = "premier25";
                    }
                    enter_instagram.setText(code);
                }
                enter_instagram.setTextSize(30);
                enter_instagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = enter_instagram.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("CODE", text);
                        clipboard.setPrimaryClip(clip);

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setMessage("Copied To Clipboard");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    enter_instagram.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    enter_instagram.setGravity(Gravity.CENTER);
                }
                Picasso.with(context).load(imageUrl).into(code_comp_container);
                LinearLayout next_premier_code_container = (LinearLayout) dialog.findViewById(R.id.premier_code_next_container);
                next_premier_code_container.setVisibility(View.VISIBLE);
                ImageView dialogButtonCancel = (ImageView) dialog.findViewById(R.id.button_premier_cancel_comp_code);
                ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.button_premier_next_comp_code);
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
                        if (multipleCodes.get(getAdapterPosition())) {
                            addUserToCode(randomCode);
                        } else {
                            String code;
                            if (getAdapterPosition() == 2) {
                                code = "premier50";
                            } else {
                                code = "premier25";
                            }
                            addUserToSubmission(code);
                        }

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(web_urls.get(getAdapterPosition())));
                        context.startActivity(browserIntent);
                        Map<String, String> articleParams = new HashMap<String, String>();
                        //param keys and values have to be of String type
                        articleParams.put("Code Redeemed", "User submitted a competition");
                        //up to 10 params can be logged with each event
                        AppEventsLogger logger = AppEventsLogger.newLogger(context);
                        logger.logEvent("Code Redeemed");
                        FlurryAgent.logEvent("Code Redeemed", articleParams);
                    }
                });

                dialog.show();
            }
        }

        private void showFinalImage(String s) {
            // custom dialog
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_competition_dialog);
            final CustomRelativeLayout comp_container = (CustomRelativeLayout) dialog.findViewById(R.id.redeem_comp_container);
            LinearLayout container = (LinearLayout) dialog.findViewById(R.id.finish_container);
            container.setVisibility(View.VISIBLE);
            Picasso.with(context).load(s).into(comp_container);
            ImageView dialogButtonOk = (ImageView) dialog.findViewById(R.id.button_finish);
            // Your android custom dialog ok action
            // Action for custom dialog ok button click
            dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    private void addUserToSubmission(final String code) {
        ParseObject newSubmission = new ParseObject("submissions");
        newSubmission.put("code", code);
        newSubmission.put("type", code);
        newSubmission.put("multipleCodes", false);
        newSubmission.put("user", ParseUser.getCurrentUser());
        newSubmission.put("username", ParseUser.getCurrentUser().getUsername());
        newSubmission.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendEmail(ParseUser.getCurrentUser().getEmail(), code, false, code, "Elle");
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void addUserToCode(final String randomCode) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("discountCodes");
        query.whereEqualTo("discountCodes", randomCode);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    objects.get(0).put("userReedemed", ParseUser.getCurrentUser().getObjectId());
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject newSubmission = new ParseObject("submissions");
                                newSubmission.put("code", randomCode);
                                newSubmission.put("type", "Elle" + objects.get(0).getString("description"));
                                newSubmission.put("multipleCodes", true);
                                newSubmission.put("user", ParseUser.getCurrentUser());
                                newSubmission.put("username", ParseUser.getCurrentUser().getUsername());
                                newSubmission.saveInBackground();
                                sendEmail(ParseUser.getCurrentUser().getEmail(), objects.get(0).getString("description"), true, randomCode, "Elle");
                            } else {
                                Log.e("failed", "failed" + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });

    }

    private void generateRandomCode(final int position, final TextView enter_instagram) {
        final String[] newCode = {null};
        final ArrayList<String> availableCodes = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("discountCodes");
        ParseObject obj = ParseObject.createWithoutData("Likes", object_ids.get(position));
        query.whereEqualTo("Offer", obj);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("size", "objectsize" + objects.size());
                    for (ParseObject j : objects) {
                        if (j.getString("userRedeemed") == null) {
                            availableCodes.add(j.getString("discountCodes"));
                        }
                    }
                    if (availableCodes.size() > 0) {
                        Random r = new Random();
                        int position = r.nextInt(availableCodes.size());
                        newCode[0] = availableCodes.get(position);
                        randomCode = availableCodes.get(position);
                        enter_instagram.setText(newCode[0]);
                    } else {
                        Toast.makeText(context, "There are no more codes available. We have refunded you.", Toast.LENGTH_SHORT).show();
                        int uPoints = ParseUser.getCurrentUser().getInt("points");
                        int newPoints = uPoints + points.get(position);
                        ParseUser.getCurrentUser().put("points", newPoints);
                        ParseUser.getCurrentUser().saveEventually();
                    }
                } else {
                    Log.e("Failed", "failed " + e.getMessage());
                }
            }
        });
    }

    private void sendEmail(String email, String description, boolean b, String randomCode, String company) {
        String fromEmail = "becomeamodelpremier@gmail.com";
        String fromPassword = "premier12";
        String toEmails = "gb@icncorporate.com";
        String emailSubject = "Discount Redeemed for " + company + " with the code " + randomCode + ".";
        String emailBody = "<html><body><strong>Details:</strong> \n <br/>" +
                "<table> <tr = style='background-color:#e6e6e6; border: 1px solid black'> <td> Email: </td>" + "<td>" + email + "</td> \n </tr>" +
                "<tr style='background-color:#ffffff; border: 1px solid black'> <td> IsMultiCode: </td>" + "<td>" + b + "\n </td> </tr>" +
                "<tr style='background-color:#e6e6e6; border: 1px solid black'> <td> Description: </td>" + "<td>" + description + "</td> \n </tr>" +
                "<tr style='background-color:#ffffff; border: 1px solid black'> <td> Company: </td>" + "<td>" + company + "</td> \n </tr>" + "</table> </html>";
        new SendMailTask((Activity) context, true).execute(fromEmail,
                fromPassword, toEmails, emailSubject, emailBody, "", "", randomCode);
    }

}
