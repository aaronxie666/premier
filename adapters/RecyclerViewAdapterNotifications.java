package icn.premierandroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import icn.premierandroid.fragments.StreetStyleFragment;
import icn.premierandroid.interfaces.isCurrentUserProfile;
import icn.premierandroid.models.NotificationsDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside StreetStyleNotificationsFragment
 */

public class RecyclerViewAdapterNotifications extends
        RecyclerView.Adapter<RecyclerViewHolderNotifications>{

    // recyclerview adapter
    private ArrayList<NotificationsDataModel> latestUpdatesList;
    private Context context;
    private RecyclerViewHolderNotifications mainHolder;
    private String time = "less than a minute ago";

    public RecyclerViewAdapterNotifications(Context context,
                                    ArrayList<NotificationsDataModel> latestUpdatesList) {
        this.context = context;
        this.latestUpdatesList = latestUpdatesList;
    }

    @Override
    public int getItemCount() {
        return (null != latestUpdatesList ? latestUpdatesList.size() : 0);

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderNotifications holder, int position) {
        final NotificationsDataModel model = latestUpdatesList.get(position);

        mainHolder = holder;// holder
        // setting title
        Log.d("created_at", "" + model.getCreated_at());
        if (model.getProfile_url().equals("null")) {
            Picasso.with(context).load(R.drawable.profile_nopic).into(mainHolder.profile_image);
        } else {
            Picasso.with(context).load(model.getProfile_url()).into(mainHolder.profile_image);
        }
        mainHolder.user_name.setText(model.getUsername());
        mainHolder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StreetStyleFragment.setObjectId(model.getCurrentObjectId());
                Log.e("ObjectID", "" + StreetStyleFragment.getObjectId());
                StreetStyleFragment.type = isCurrentUserProfile.type.no;
                StreetStyleFragment.bottomBar.selectTabAtPosition(3);
            }
        });
        mainHolder.action.setText(model.getAction());
        how_long_ago(model.getCreated_at());
        mainHolder.createdAt.setText(time);
    }

    @Override
    public RecyclerViewHolderNotifications onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.notifications_item_row, viewGroup, false);
        return new RecyclerViewHolderNotifications(mainGroup);

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
}
