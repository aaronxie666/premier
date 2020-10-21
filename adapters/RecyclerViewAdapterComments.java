package icn.premierandroid.adapters;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.chrono.GregorianChronology;

import java.util.ArrayList;

import icn.premierandroid.R;
import icn.premierandroid.models.CommentsDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside CommentsActivity
 */

public class RecyclerViewAdapterComments extends
        RecyclerView.Adapter<RecyclerViewHolderComments>{

    // recyclerview adapter
    private ArrayList<CommentsDataModel> latestCommentsList;
    private Context context;
    private String time = "less than a minute ago";
    private SwipeRefreshLayout swiper;

    public RecyclerViewAdapterComments(Context context,
                                       ArrayList<CommentsDataModel> commentsList, SwipeRefreshLayout swiper) {
        this.context = context;
        this.latestCommentsList = commentsList;
        this.swiper = swiper;
    }
    
    @Override
    public int getItemCount() {
        return (null != latestCommentsList ? latestCommentsList.size() : 0);

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderComments holder, int position) {
        final CommentsDataModel model = latestCommentsList.get(position);
        // setting title
        Log.d("created_at", "" + model.getCreated_at());
        if(model.getProfile_url().isEmpty()){
            Picasso.with(context).load(R.drawable.profile_nopic);
        }else {
            Picasso.with(context).load(model.getProfile_url()).into(holder.profile_image);
        }

        holder.user_name.setText(model.getUsername());
        holder.comment_display.setText(model.getComment());
        how_long_ago(model.getCreated_at());
        holder.createdAt.setText(time);
    }

    @Override
    public RecyclerViewHolderComments onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.comment_item_row, viewGroup, false);
        return new RecyclerViewHolderComments(mainGroup);

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
            return time;
        } else if (diffInHours.isGreaterThan(Hours.hours(24))) {
            time = diffInDays.getDays() + " days ago";
            return time;
        } else if (diffInMinutes.isGreaterThan(Minutes.minutes(60))) {
            time = diffInHours.getHours() + " hours ago";
            return time;
        } else if (seconds.isGreaterThan(Seconds.seconds(60))) {
            time = diffInMinutes.getMinutes() + " minutes ago";
            return time;
        } else if (seconds.isLessThan(Seconds.seconds(60))) {
            return time;
        }
        Log.d("since grad", "" + sinceGraduation);
        return time;
    }

    public void update(ArrayList<CommentsDataModel> updatesList) {
        latestCommentsList.clear();
        latestCommentsList.addAll(updatesList);
        swiper.setRefreshing(false);
    }

    public void addComment(String userId, String profile_url, String username, String created_at, String comment) {
        latestCommentsList.add(0, new CommentsDataModel(userId, profile_url, username, created_at, comment));
    }
}
