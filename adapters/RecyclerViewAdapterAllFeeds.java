package icn.premierandroid.adapters;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.chrono.GregorianChronology;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import icn.premierandroid.AllLoginActivity;
import icn.premierandroid.CommentsActivity;
import icn.premierandroid.R;
import icn.premierandroid.TagsActivity;
import icn.premierandroid.fragments.StreetStyleFragment;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.interfaces.isCurrentUserProfile;
import icn.premierandroid.misc.CustomFontTextView;
import icn.premierandroid.models.AllFeedsDataModel;
import icn.premierandroid.models.SuggestedUsersDataModel;
import icn.premierandroid.models.TagsModel;

import static icn.premierandroid.misc.CONSTANTS.LOADED_POSTS;
import static icn.premierandroid.misc.CONSTANTS.TOTAL_POSTS;
import static java.lang.String.valueOf;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside StreetstyleHomeFragment,
 *              StreetStyleSearchFragment and StreetStyleProfileFragment
 */

public class RecyclerViewAdapterAllFeeds extends RecyclerView.Adapter<RecyclerViewHolderAllFeeds> {
    // recyclerview adapter
    private ArrayList<AllFeedsDataModel> previousPostsList;
    private Context context;
    private AllFeedsDataModel model;
    private ArrayList<TextView> tags = new ArrayList<>();
    private ArrayList<View> shadows = new ArrayList<>();
    private int count = 0, count1 = 0, count2=0, count3=0;
    private String time = "less than a minute ago";
    private ArrayList<ImageView> like_icons = new ArrayList<>();
    private boolean liked = false;
    private JSONArray likingArray;
    private int transitionTime = 700;
    private TransitionDrawable transition;
    private boolean profile;
    private ArrayList<String> allLikes;
    private ArrayList<String> allTags;
    private ArrayList<ParseObject> likesListUsers;
    private ArrayList<SuggestedUsersDataModel> userLikes;
    private ArrayList<TagsModel> userTags;
    private RecyclerView dialog_recycler;
    private Dialog likesDialog;
    private ArrayList<TextView> number_of_likes_list = new ArrayList<>();
    private ArrayList<TextView> number_of_likes_image_list = new ArrayList<>();
    private ArrayList<TextView> number_of_tags_list = new ArrayList<>();
    private ArrayList<TextView> number_of_tags_image_list = new ArrayList<>();
    private ArrayList<String> imageIds = new ArrayList<>();
    private ArrayList<Integer> likesAmount = new ArrayList<>();
    private ArrayList<Integer> tagsAmount = new ArrayList<>();
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private ArrayList<String> objectIds = new ArrayList<>();
    private JSONArray peopleVoted;
    private Dialog tagsDialog;
    private RecyclerView tags_dialog_recycler;
    private JSONArray tagsArray;
    private Boolean search;

    public RecyclerViewAdapterAllFeeds(Context context,
                                       final ArrayList<AllFeedsDataModel> previousPostsList, final boolean profile, RecyclerView recyclerView, Boolean search) {
        this.context = context;
        this.previousPostsList = previousPostsList;
        this.profile = profile;
        this.search = search;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    if (getItemCount() <= TOTAL_POSTS) {
//                        if (getItemCount() > LOADED_POSTS - 2) {
//                            totalItemCount = getItemCount();
//                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                                if (onLoadMoreListener != null) {
//                                    onLoadMoreListener.onLoadMore();
//                                }
//                                loading = true;
//                            }
//                        }
//                    }
//                }
//            });
//        }

                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        totalItemCount = linearLayoutManager.getItemCount();
                        if (totalItemCount > TOTAL_POSTS) {
                            totalItemCount = TOTAL_POSTS;
                        }
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                        if (!loading && totalItemCount <= (lastVisibleItem + LOADED_POSTS)) {
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                            recyclerView.removeOnScrollListener(this);
                        }
                    }
                };
                recyclerView.addOnScrollListener(scrollListener);
            }
    }

    @Override
    public int getItemCount() {
        return (null != previousPostsList ? previousPostsList.size() : 0);

    }

    @Override
    public int getItemViewType(int position) {
        return previousPostsList.get(position) != null ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolderAllFeeds holder, int position) {
        if(holder != null) {
            model = previousPostsList.get(position);
            objectIds.add(model.getCurrentUser());
            final int positionFinal = position;
            if (model != null) {
                if (model.getFeeds()) {
                    if (holder.row_container.indexOfChild(holder.user_name) == -1) {
                        holder.row_container.addView(holder.user_name);
                    }
                    if (holder.row_container.indexOfChild(holder.profile_image) == -1) {
                        holder.row_container.addView(holder.profile_image);
                    }

                    if (model.getProfile_url() != null) {
                        if (model.getProfile_url().isEmpty()) {
                            Picasso.with(context).load(R.drawable.profile_nopic).into(holder.profile_image);
                        } else {
                            Picasso.with(context).load(model.getProfile_url()).placeholder(R.drawable.profile_nopic).into(holder.profile_image);
                        }
                    } else {
                        Picasso.with(context).load(R.drawable.profile_nopic).into(holder.profile_image);
                    }

                    Log.e("objectid", objectIds.toString());
//                    if (search && !profile) {
//                        Collections.reverse(objectIds);
//                        Log.e("objectIds_REVE", objectIds.toString());
//                    }
                    holder.user_name.setText(model.getUsername());
                    holder.user_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("model_current", "" + model.getCurrentUser());
                            if (objectIds.get(positionFinal) == ParseUser.getCurrentUser().getObjectId()) {
                                StreetStyleFragment.setObjectId(objectIds.get(positionFinal));
                                StreetStyleFragment.type = isCurrentUserProfile.type.yes;
                                StreetStyleFragment.bottomBar.selectTabAtPosition(3);
                            } else {
                                StreetStyleFragment.setObjectId(objectIds.get(positionFinal));
                                StreetStyleFragment.type = isCurrentUserProfile.type.no;
                                StreetStyleFragment.bottomBar.selectTabAtPosition(3);
                            }
                        }
                    });

                    holder.uploadedImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            final AlertDialog diaBox = ReportDialog(holder.getAdapterPosition());
                            diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                    diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                }
                            });
                            diaBox.show();
                            return false;
                        }
                    });
                } else {
                    holder.row_container.removeView(holder.user_name);
                    holder.row_container.removeView(holder.profile_image);
                }
                imageIds.add(model.getImageId());
                addShadowViews(holder);
                getTagInformation(model, holder);
                addLikeIcons(holder);
                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+liked);
                // setting title
                Log.d("created_at", "" + model.getCreated_at());
                how_long_ago(model.getCreated_at());
                holder.createdAt.setText(time);
                likesAmount.add(model.getLikes());
                tagsAmount.add(model.getTagSize());

                Log.e("tags size", "" + tagsAmount.size());
                number_of_likes_list.add(holder.number_of_likes);
                number_of_likes_image_list.add(holder.likesImage);
                number_of_tags_list.add(holder.number_of_tags);
                number_of_tags_image_list.add(holder.tagsImage);
//                if (search) {
//                    Picasso.with(context).load(model.getImage()).fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.uploadedImage);
//                } else {
                    Picasso.with(context).load(model.getImage()).into(holder.uploadedImage);
//                }
                holder.caption.setText(model.getCaption());
                holder.number_of_likes.setText(valueOf(model.getLikes()));

                if (likesAmount.get(position) > 0) {
                   number_of_likes_image_list.get(position).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showLikesUsersDialog();
                                populateRecyclerView(imageIds.get(holder.getAdapterPosition()));
                            }
                        });

                    number_of_likes_list.get(position).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showLikesUsersDialog();
                                populateRecyclerView(imageIds.get(holder.getAdapterPosition()));
                            }
                        });
                    } else {
                        Log.e("no likes", "no likes");
                    }

                if (tagsAmount.get(position) > 0) {
                    number_of_tags_image_list.get(position).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showTagsUsersDialog();
                                populateTagsRecyclerView(imageIds.get(holder.getAdapterPosition()));}
                        });
                        number_of_tags_list.get(position).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showTagsUsersDialog();
                                populateTagsRecyclerView(imageIds.get(holder.getAdapterPosition()));
                            }
                        });
                    } else {
                        Log.e("no tags", "no tags");
                    }

                holder.number_of_comments.setText(model.getComments());

                for ( int i = 0; i <= like_icons.size(); i++) {
                    final int finalI = i;
                    holder.uploadedImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeShadowLayout(shadows);
                            makeTags(tags);
                            makeLikeIcons();
                            doesUserLikeThisImage(finalI);
                        }
                    });

                    if (profile) {
                        holder.uploadedImage.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                final AlertDialog diaBox = AskOption(holder.getAdapterPosition());
                                diaBox.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        diaBox.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                        diaBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                    }
                                });
                                diaBox.show();
                                return false;
                            }
                        });
                    }
                    holder.number_of_comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoToComments(model.getImages(finalI-1), model.getCurrentUser());
                        }
                    });
                    holder.comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GoToComments(model.getImages(finalI-1), model.getCurrentUser());
                        }
                    });
                }

                holder.number_of_tags.setText(model.getTags());
                for (int i = 0; i < tags.size(); i++) {
                    final int finalI = i;
                    tags.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent tagIntent = new Intent(context, TagsActivity.class);
                            String text = getFirstWord(tags.get(finalI).getText().toString());
                            tagIntent.putExtra("tag", text);
                            context.startActivity(tagIntent);
                        }
                    });
                }

                like_icons.get(position).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
                            query.whereEqualTo("objectId", imageIds.get(holder.getAdapterPosition()));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject j : objects) {
                                            likingArray = j.getJSONArray("peopleVoted");
                                            if (likingArray != null) {
                                                Log.e("liking array", "!= null");
                                                System.out.println("000000000000000000000000000000000000000000"+liked);
                                                if (likingArray.toString().contains(ParseUser.getCurrentUser().getObjectId())) {
                                                    System.out.println("-------------------------------------------------------------remove");
                                                    String likesNew = j.getString("likes");
                                                    int likesTotal = Integer.valueOf(likesNew) - 1;
                                                    String finalLikes = valueOf(likesTotal);
                                                    ArrayList<String> user_array = new ArrayList<>();
                                                    user_array.add(ParseUser.getCurrentUser().getObjectId());
                                                    j.removeAll("peopleVoted", user_array);
                                                    j.put("likes", finalLikes);
                                                    transition = (TransitionDrawable) like_icons.get(holder.getAdapterPosition()).getBackground();
                                                    transition.reverseTransition(transitionTime);
                                                    j.saveEventually(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            removeCurrentUserLikes();
                                                        }
                                                    });
                                                } else {
                                                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++add"+ likingArray);
                                                    likingArray.put(ParseUser.getCurrentUser().getObjectId());
                                                    Log.e("liking array", "doesn't contain new user");
                                                    j.put("peopleVoted", likingArray);
                                                    String likes = j.getString("likes");
                                                    int totalLikes = Integer.valueOf(likes) + 1;
                                                    likes = valueOf(totalLikes);
                                                    j.put("likes", likes);
                                                    j.saveEventually(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                notifyUser(imageIds.get(holder.getAdapterPosition()));
                                                                addLikesToUser();
                                                                transition = (TransitionDrawable) like_icons.get(holder.getAdapterPosition()).getBackground();
                                                                transition.startTransition(transitionTime);
                                                            } else {
                                                                Log.e("failed", "failed" + e.getMessage());
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.e("liking array", "is null");
                                                JSONArray new_like_array = new JSONArray();
                                                new_like_array.put(ParseUser.getCurrentUser().getObjectId());
                                                j.put("peopleVoted", new_like_array);
                                                String likes = j.getString("likes");
                                                int totalLikes = Integer.valueOf(likes) + 1;
                                                likes = valueOf(totalLikes);
                                                j.put("likes", likes);
                                                j.saveEventually(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            notifyUser(imageIds.get(holder.getAdapterPosition()));
                                                            addLikesToUser();
                                                            transition = (TransitionDrawable) like_icons.get(holder.getAdapterPosition()).getBackground();
                                                            transition.startTransition(transitionTime);
                                                        } else {
                                                            Log.e("failed", "failed" + e.getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });

                            Log.e("imageID", model.getImageId() + " selected");
                        }
                    });
                }
        } else {
            ((ProgressViewHolder) holder).container.setVisibility(View.VISIBLE);
        }
    }

    private String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(" -")); // Extract words before dash
        } else {
            return text; // Text is the first word itself.
        }
    }

    private void populateTagsRecyclerView(String imageId) {
        Log.e("imageId", imageId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("objectId", imageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    allTags = new ArrayList<>();
                    userTags = new ArrayList<>();
                    for (ParseObject j : objects) {
                        tagsArray = j.getJSONArray("tagName");
                        if (tagsArray!= null) {
                            if (tagsArray.length() != 0) {
                                if (allTags != null) {
                                    for (int i = 0; i < tagsArray.length(); i++) {
                                        allTags.add(tagsArray.optString(i));
                                        Log.e("alltags", allTags.toString());
                                    }
                                }
                            } else {
                                Log.e("no tags", "no tags");
                            }
                            userTags.add(new TagsModel(allTags));
                        } else {
                            Log.e("No tags", "No tags");
                        }
                    }
                    RecyclerViewAdapterDialog adapter = new RecyclerViewAdapterDialog(context, null, userTags, tagsDialog);
                    tags_dialog_recycler.setAdapter(adapter);// set adapter on recyclerview
                    adapter.notifyDataSetChanged();// Notify the adapter
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void showTagsUsersDialog() {
        tagsDialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        tagsDialog.setContentView(R.layout.recycler_view);
        tagsDialog.setCancelable(true);
        tags_dialog_recycler = (RecyclerView) tagsDialog.findViewById(R.id.dialog_recyclerview);
        tags_dialog_recycler.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        tags_dialog_recycler.setLayoutManager(mLayoutManager);
        tags_dialog_recycler.setBackgroundColor(Color.WHITE);
        tagsDialog.show();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoaded() {
        loading = false;
    }

    private void showLikesUsersDialog() {
        likesDialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        likesDialog.setContentView(R.layout.recycler_view);
        likesDialog.setCancelable(true);
        dialog_recycler = (RecyclerView) likesDialog.findViewById(R.id.dialog_recyclerview);
        dialog_recycler.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        dialog_recycler.setLayoutManager(mLayoutManager);
        dialog_recycler.setBackgroundColor(Color.WHITE);
        likesDialog.show();
    }

    private void populateRecyclerView(String imageId) {
        count3 = 0;
        Log.e("imageId", imageId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("objectId", imageId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    allLikes = new ArrayList<>();
                    likesListUsers = new ArrayList<>();
                    userLikes = new ArrayList<>();
                    for (ParseObject j : objects) {
                        peopleVoted = j.getJSONArray("peopleVoted");
                        if (peopleVoted != null) {
                            if (peopleVoted.length() != 0) {
                                if (allLikes != null) {
                                    for (int i = 0; i < peopleVoted.length(); i++) {
                                        allLikes.add(peopleVoted.optString(i));
                                        Log.e("alllikes", allLikes.toString());
                                    }
                                }
                            } else {
                                Log.e("no likes", "no likes");
                            }
                            getUserDetails();
                        } else {
                            Log.e("No Likes", "No likes");
                        }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void getUserDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        try {
            query.whereEqualTo("objectId", allLikes.get(count3));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            Log.d("user_id", "" + j.get("name"));
                            likesListUsers.add(j);
                            if (allLikes.size() == 1) {
                                count3 = 1;
                            } else {
                                count3++;
                            }
                            if (count3 < peopleVoted.length()) {
                                    getUserDetails();
                            } else {
                                int z = 0;
                                for (ParseObject ignored : likesListUsers) {
                                    if (z < peopleVoted.length()) {
                                        ParseUser theUser = (ParseUser) likesListUsers.get(z);
                                        String user_name = theUser.getUsername();
                                        String objectId = theUser.getObjectId();
                                        Log.e("user_name", user_name);
                                        String profile_picture = (String) theUser.get("profilePicture");
                                        if (profile_picture != null) {
                                            if (profile_picture.isEmpty()) {
                                                Log.e("profile_pic_url", "is null");
                                            } else {
                                                if (AllLoginActivity.userType != UserType.userType.instagramUser) {
                                                    profile_picture = profile_picture.replace("http", "https");
                                                }

                                                if (profile_picture.startsWith("httpss")) {
                                                    profile_picture = profile_picture.replace("httpss", "https");
                                                }
                                            }
                                        } else {
                                            profile_picture = "R.drawable.profile_nopic";
                                        }
                                        Log.e("profile", "profilepic" + profile_picture);
                                        userLikes.add(new SuggestedUsersDataModel(user_name, profile_picture, objectId));
                                        z++;
                                    }
                                }
                                RecyclerViewAdapterDialog adapter = new RecyclerViewAdapterDialog(context, userLikes, null, likesDialog);
                                dialog_recycler.setAdapter(adapter);// set adapter on recyclerview
                                adapter.notifyDataSetChanged();// Notify the adapter
                            }
                        }
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            });
        } catch (IndexOutOfBoundsException e) {
            Log.e("failed", "failed " + e.getMessage());
        }

    }

    private void removeCurrentUserLikes() {
        ParseQuery<ParseObject> followUser = ParseQuery.getQuery("Follow");
        followUser.whereEqualTo("user", ParseUser.getCurrentUser());
        followUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        int likes = objects.get(0).getInt("likes");
                        int totalLikes = likes - 1;
                        if (likes != 0) {
                            objects.get(0).put("likes", totalLikes);
                            objects.get(0).saveEventually();
                            liked = false;
                        }
                        else {
                            objects.get(0).put("likes", 0);
                            objects.get(0).saveEventually();
                            liked = false;
                        }
                    }
                    else {
                        Log.e("failed", "no objects match");
                    }
                } else {
                    Log.e("failed", "failed " + e.getMessage());
                }

            }
        });

    }

    private void doesUserLikeThisImage(final int position) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
            query.whereEqualTo("objectId", model.getImages(position));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            JSONArray likingArray = j.getJSONArray("peopleVoted");
                            liked = likingArray != null && likingArray.toString().contains(ParseUser.getCurrentUser().getObjectId());
                        }
                } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            });
        } catch (IndexOutOfBoundsException e) {
            Log.e("indexoutofbounds", e.getMessage());
        }
    }

    private AlertDialog AskOption(final int i)
    {
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        previousPostsList.remove(i);
                        notifyDataSetChanged();
                        removePostFromParse();
                        removeLikesFromUser();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

    }

    private AlertDialog ReportDialog(final int i) {
        return new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Report Photo")
                .setMessage("This photo will be reported and reviewed within 24 hours for objectionable content. If the post is found to be unsuitable, it will be deleted.")
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                       addReportedImage(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void addReportedImage(final int i) {
        ParseObject reportedImage = new ParseObject("ReportedImages");
        reportedImage.put("userID", ParseUser.getCurrentUser());
        reportedImage.put("imageID", imageIds.get(i));
        reportedImage.saveInBackground();
    }

    private void removeLikesFromUser() {
        ParseQuery<ParseUser> points_query = ParseUser.getQuery();
        points_query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        points_query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects != null) {
                        for (ParseUser j : objects) {
                            int totalPoints;
                            int points = j.getInt("points");
                            if (points >= 200) {
                                totalPoints = points - 200;
                                j.put("points", totalPoints);
                            } else {
                                totalPoints = 0;
                                j.put("points", totalPoints);
                            }
                            j.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.e("success", "successfully removed points");
                                    } else {
                                        Log.e("failed", "failed" + e.getMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("objects", "is null");
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void removePostFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("objectId", model.getImageId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                objects.get(0).deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(context, "Post Successfully Deleted", Toast.LENGTH_SHORT).show();
                            Log.e("delete success", "successfully deleted post");
                            updateUserPosts();
                        } else {
                            Log.e("failed", "failed" + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void updateUserPosts() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                int posts = objects.get(0).getInt("posts");
                int newposts = posts - 1;
                objects.get(0).put("posts", newposts);
                objects.get(0).saveInBackground();
            }
        });
    }

    private void addLikesToUser() {
        ParseQuery<ParseObject> followUser = ParseQuery.getQuery("Follow");
        followUser.whereEqualTo("user", ParseUser.getCurrentUser());
        followUser.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        int likes = objects.get(0).getInt("likes");
                        int totalLikes = likes + 1;
                        if (likes == 0) {
                            objects.get(0).put("likes", 1);
                            objects.get(0).saveEventually();
                            liked = true;
                        } else {
                            objects.get(0).put("likes", totalLikes);
                            objects.get(0).saveEventually();
                            liked = true;
                        }
                    }
                    else {
                        Log.e("failed", "no objects match");
                    }
                } else {
                    Log.e("failed", "failed " + e.getMessage());
                }

            }
        });

    }

    private void notifyUser(final String imageId) {
        final ParseUser parseUser = (ParseUser) ParseUser.createWithoutData("_User", model.getCurrentUser());
        ParseObject notifications = new ParseObject("Notifications");
        notifications.put("action", "comment");
        notifications.put("actionUser", ParseUser.getCurrentUser());
        notifications.put("receivedUser", parseUser);
        notifications.put("photoLiked", imageId);
        notifications.saveInBackground();

        ParseQuery<ParseInstallation> parseQueryInstallation = ParseQuery.getQuery(ParseInstallation.class);
        parseQueryInstallation.whereEqualTo("user", parseUser);
        //sends Push Notification of Friend Request
        ParsePush push = new ParsePush();
        push.setMessage(ParseUser.getCurrentUser().getUsername() + " has liked one of your photos.");
        push.setQuery(parseQueryInstallation);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.e("SAVE SUCCESS", "");
                }else{
                    Log.e("ERROR", e.getMessage());
                }
            }
        });
    }

    @Override
    public RecyclerViewHolderAllFeeds onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerViewHolderAllFeeds holder;
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == 1) {
            // This method will inflate the custom layout and return as viewholde
            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.feed_and_search_feed_item_row, viewGroup, false);
            holder =  new RecyclerViewHolderAllFeeds(mainGroup);
        } else {
            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.progress_item, viewGroup, false);
            holder =  new ProgressViewHolder(mainGroup);
        }
        return  holder;
    }

    private void GoToComments(String objectId, String userId) {
        Log.e("comments click", "comments clicked");
        final Intent i = new Intent(context, CommentsActivity.class);
        i.putExtra("imageId", objectId);
        i.putExtra("selectedUser", userId);
        Log.e("selectedUserPrev", "" + userId);
        context.startActivity(i);
    }

    private String how_long_ago(String created_at) {
        DateTime sinceGraduation = new DateTime(created_at, GregorianChronology.getInstance());
        DateTime currentDate = new DateTime(); //current date

        Months diffInMonths = Months.monthsBetween(sinceGraduation, currentDate);
        Days diffInDays = Days.daysBetween(sinceGraduation, currentDate);
        Hours diffInHours = Hours.hoursBetween(sinceGraduation, currentDate);
        Minutes diffInMinutes = Minutes.minutesBetween(sinceGraduation, currentDate);
        Seconds seconds = Seconds.secondsBetween(sinceGraduation, currentDate);

        Log.d("since grad", "before if " + sinceGraduation);
        if (diffInDays.isGreaterThan(Days.days(31))) {
            time = diffInMonths.getMonths() + " months ago";
            if (diffInMonths.getMonths() == 1) {
                time = diffInMonths.getMonths() + " month ago";
            } else {
                time = diffInMonths.getMonths() + " months ago";
            }
            return time;
        } else if (diffInHours.isGreaterThan(Hours.hours(24))) {
            if (diffInDays.getDays() == 1) {
                time = diffInDays.getDays() + " day ago";
            } else {
                time = diffInDays.getDays() + " days ago";
            }
            return time;
        } else if (diffInMinutes.isGreaterThan(Minutes.minutes(60))) {
            if (diffInHours.getHours() == 1) {
                time = diffInHours.getHours() + " hour ago";
            } else {
                time = diffInHours.getHours() + " hours ago";
            }
            return time;
        } else if (seconds.isGreaterThan(Seconds.seconds(60))) {
            if (diffInMinutes.getMinutes() == 1) {
                time = diffInMinutes.getMinutes() + " minute ago";
            } else {
                time = diffInMinutes.getMinutes() + " minutes ago";
            }
            return time;
        } else if (seconds.isLessThan(Seconds.seconds(60))) {
            return time;
        }
        Log.d("since grad", "" + sinceGraduation);
        return time;
    }

    private void addLikeIcons(final RecyclerViewHolderAllFeeds mainHolder) {
        ImageView likes = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
        }
        else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        params.setMargins(0, 0, 0, 40);
        likes.setLayoutParams(params);
        likes.setBackgroundResource(R.drawable.like_transition);
        mainHolder.imageContainer.addView(likes);
        likes.setVisibility(View.INVISIBLE);
        like_icons.add(likes);
        //add by chang start
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FashionFeed");
        query.whereEqualTo("objectId",imageIds.get(mainHolder.getAdapterPosition()));
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
        List<ParseObject> objects = null;
        try {
            objects = query.find();

//                if(e==null){
                    for(ParseObject j :objects){
                        likingArray = j.getJSONArray("peopleVoted");
                        if(likingArray!=null){
                            if(likingArray.toString().contains(ParseUser.getCurrentUser().getObjectId())){
                                transition = (TransitionDrawable) like_icons.get(mainHolder.getAdapterPosition()).getBackground();
                                transition.startTransition(transitionTime);
                                liked = true;
                            }else {
                                liked = false;
                            }

                        }else {
                            liked = false;

                        }

//                    }

                }
    } catch (ParseException e) {
        e.printStackTrace();
    }

//            }
//        });
        //add by chang end

    }


    private void makeLikeIcons() {
        if (count1 == 0) {
            for (int i = 0; i < like_icons.size(); i++) {
                if (like_icons.get(i).getVisibility() == View.INVISIBLE) {
                    like_icons.get(i).setVisibility(View.VISIBLE);
                }
                else {
                    like_icons.get(i).setVisibility(View.INVISIBLE);
                }
            }
            count1 = 1;
        }
        else {
            for (int i = 0; i < like_icons.size(); i++) {
                if (like_icons.get(i).getVisibility() == View.INVISIBLE) {
                    like_icons.get(i).setVisibility(View.VISIBLE);
                }
                else {
                    like_icons.get(i).setVisibility(View.INVISIBLE);
                }
            }
            count1 = 0;
        }
    }

    private void getTagInformation(AllFeedsDataModel model, RecyclerViewHolderAllFeeds mainHolder) {
        if (model.getTagSize() == 0) {
            Log.e("No tags", "no tags for this image");
        }
        else {
            for (int o = 0; o < model.getTagSize(); o++) {
                CustomFontTextView tag = new CustomFontTextView(context);
                Resources r = context.getResources();
                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                DisplayMetrics displayMetrics = r.getDisplayMetrics();
                float width = displayMetrics.widthPixels / displayMetrics.density;
                float x = width - Float.parseFloat(model.getXpoints(o)) / 100 * width;
                float y = height - Float.parseFloat(model.getYpoints(o)) / 100 * height;
                Log.e("getPointX", "" + model.getXpoints(o) + "" + model.getYpoints(o));
                Log.e("x", "x" + x + "y" + y);
                tag.setX(x);
                tag.setY(y);
                Log.e("x", "" + tag.getX());
                Log.e("y", "" + tag.getY());
                tag.setText(model.getTagName(o) + " - " + model.getTagClothing(o));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tag.setLayoutParams(params);
                tag.setMaxLines(2);
                tag.setTextSize(11);
                tag.setClickable(true);
                tag.setHintTextColor(Color.WHITE);
                tag.setTextColor(Color.WHITE);
                tag.setBackgroundResource(R.drawable.tags_rounded_corners);
                mainHolder.imageContainer.addView(tag);
                tag.setVisibility(View.INVISIBLE);
                tags.add(tag);
            }
        }
    }

    private void addShadowViews(RecyclerViewHolderAllFeeds mainHolder) {
        View shadowView = new View(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        shadowView.setLayoutParams(params);
        shadowView.setBackgroundResource(R.drawable.gradient1);
        mainHolder.imageContainer.addView(shadowView);
        shadowView.setVisibility(View.INVISIBLE);
        shadows.add(shadowView);
    }

    private void makeTags(ArrayList<TextView> tags) {
        if (count == 0) {
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).getVisibility() == View.INVISIBLE) {
                    tags.get(i).setVisibility(View.VISIBLE);
                }
                else {
                    tags.get(i).setVisibility(View.INVISIBLE);
                }
            }
            count = 1;
        }
        else {
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).getVisibility() == View.INVISIBLE) {
                    tags.get(i).setVisibility(View.VISIBLE);
                }
                else {
                    tags.get(i).setVisibility(View.INVISIBLE);
                }
            }
            count = 0;
        }
    }

    private void makeShadowLayout(ArrayList<View> shadows) {
        if (count2 == 0) {
            for (int i = 0; i < shadows.size(); i++) {
                if (shadows.get(i).getVisibility() == View.INVISIBLE) {
                    shadows.get(i).setVisibility(View.VISIBLE);
                }
                else {
                    shadows.get(i).setVisibility(View.INVISIBLE);
                }
            }
            count2 = 1;
        }
        else {
            for (int i = 0; i < shadows.size(); i++) {
                if (shadows.get(i).getVisibility() == View.INVISIBLE) {
                    shadows.get(i).setVisibility(View.VISIBLE);
                }
                else {
                    shadows.get(i).setVisibility(View.INVISIBLE);
                }
            }
            count2 = 0;
        }
    }

    public void update(ArrayList<AllFeedsDataModel> updatesList) {
        previousPostsList.clear();
        previousPostsList.addAll(updatesList);
    }
}
