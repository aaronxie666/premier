package icn.premierandroid.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by ICN on 13/10/2016.
 */

public class CustomRelativeLayout extends RelativeLayout implements Target {

    public CustomRelativeLayout(Context context) {
        super(context);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        //Set your error drawable
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        //Set your placeholder
    }
}