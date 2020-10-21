package icn.premierandroid.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.R;
import icn.premierandroid.misc.CircleTransform;
import icn.premierandroid.models.ModelWorldDataModel;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The Adapter for the recyclerviews inside ModelWorldFragment
 */

public class RecyclerViewAdapterModelWorld extends RecyclerView.Adapter<RecyclerViewAdapterModelWorld.RecyclerViewHolderModelWorld> {

    private ArrayList<ModelWorldDataModel> models = new ArrayList<>();
    private Context context;
    private ArrayList<String> instagramUrls = new ArrayList<>();
    private ArrayList<ArrayList<String>> imageUrls = new ArrayList<>();

    public RecyclerViewAdapterModelWorld(Context context,
                                         ArrayList<ModelWorldDataModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public RecyclerViewHolderModelWorld onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.model_world_item_row, viewGroup, false);
        return new RecyclerViewHolderModelWorld(mainGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolderModelWorld holder, int position) {
        ModelWorldDataModel model = models.get(position);
        holder.name.setText(model.getName());
        holder.username.setText(model.getUsername());
        Picasso.with(context).load(model.getProfilePicture()).transform(new CircleTransform()).into(holder.profile_picture);
        instagramUrls.add(model.getUsername());
        imageUrls.add(model.getImages());
        Picasso.with(context).load(model.getImageOne()).into(holder.first_image);
        Picasso.with(context).load(model.getImageTwo()).into(holder.second_image);
        Picasso.with(context).load(model.getImageThree()).into(holder.third_image);
    }

    @Override
    public int getItemCount() {
        return (null != models ? models.size() : 0);

    }


    class RecyclerViewHolderModelWorld extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView profile_picture;
        ImageView first_image;
        ImageView second_image;
        ImageView third_image;
        public TextView username;
        public TextView name;
        private RecyclerView recyclerView;
        private LinearLayoutManager mLayoutManager;

        RecyclerViewHolderModelWorld(View itemView) {
            super(itemView);

            this.profile_picture = (ImageView) itemView.findViewById(R.id.model_profile_picture);
            this.name = (TextView) itemView.findViewById(R.id.model_full_name);
            this.username = (TextView) itemView.findViewById(R.id.model_insta_username);
            this.first_image = (ImageView) itemView.findViewById(R.id.model_image_one);
            this.second_image = (ImageView) itemView.findViewById(R.id.model_image_two);
            this.third_image = (ImageView) itemView.findViewById(R.id.model_image_three);
            first_image.setOnClickListener(this);
            second_image.setOnClickListener(this);
            third_image.setOnClickListener(this);
            profile_picture.setOnClickListener(this);
            username.setOnClickListener(this);
            name.setOnClickListener(this);

        }

        private boolean isIntentAvailable(Context ctx, Intent intent) {
            final PackageManager packageManager = ctx.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.model_image_one:
                    showLikesDialog(imageUrls.get(getAdapterPosition()));
                    break;
                case R.id.model_image_two:
                    showLikesDialog(imageUrls.get(getAdapterPosition()));
                    break;
                case R.id.model_image_three:
                    showLikesDialog(imageUrls.get(getAdapterPosition()));
                    break;
                case R.id.model_insta_username:
                    Uri uri = Uri.parse("http://instagram.com/_u/" + instagramUrls.get(getAdapterPosition()));
                    Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                    insta.setPackage("com.instagram.android");
                    if (isIntentAvailable(context, insta)){
                        context.startActivity(insta);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + instagramUrls.get(getAdapterPosition()))));
                    }
                    break;
                case R.id.model_profile_picture:
                    Uri uri1 = Uri.parse("http://instagram.com/_u/" + instagramUrls.get(getAdapterPosition()));
                    Intent insta1 = new Intent(Intent.ACTION_VIEW, uri1);
                    insta1.setPackage("com.instagram.android");
                    if (isIntentAvailable(context, insta1)){
                        context.startActivity(insta1);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + instagramUrls.get(getAdapterPosition()))));
                    }
                    break;
                case R.id.model_full_name:
                    Uri uri2 = Uri.parse("http://instagram.com/_u/" + instagramUrls.get(getAdapterPosition()));
                    Intent insta2 = new Intent(Intent.ACTION_VIEW, uri2);
                    insta2.setPackage("com.instagram.android");
                    if (isIntentAvailable(context, insta2)){
                        context.startActivity(insta2);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + instagramUrls.get(getAdapterPosition()))));
                    }
                    break;
            }
        }

        private void showLikesDialog(ArrayList<String> urls) {
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fragment_blog_preview_image);
            recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            new DisplayImages(urls).execute();
            mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false );
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
            ArrayList<String> images = new ArrayList<>();

            DisplayImages(ArrayList<String> urls) {
                this.images = urls;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Log.e("imageurls", imageUrls.toString());
                RecyclerViewAdapterBlogImages adapter = new RecyclerViewAdapterBlogImages(context, images);
                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            }
        }
    }
}
