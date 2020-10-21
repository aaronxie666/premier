package icn.premierandroid.adapters;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import icn.premierandroid.R;
import icn.premierandroid.misc.CustomFontTextView;

class ProgressViewHolder extends RecyclerViewHolderAllFeeds {
    public ProgressBar progressBar;
    public TextView description;
    public RelativeLayout container;

    ProgressViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        description = (TextView) itemView.findViewById(R.id.description_progress);
        container = (RelativeLayout) itemView.findViewById(R.id.progress_bar_container);

    }
}