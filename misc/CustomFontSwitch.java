package icn.premierandroid.misc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Switch;

import icn.premierandroid.R;

/**
 * Created by ICN on 14/11/2016.
 */

public class CustomFontSwitch extends Switch {

    public CustomFontSwitch(Context context) {
        super(context);
    }

    public CustomFontSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomFontSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        if (customFont == null) customFont = "fonts/portrait-light.ttf";
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("switch", "Could not get typeface", e);
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
