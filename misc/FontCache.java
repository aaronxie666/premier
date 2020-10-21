package icn.premierandroid.misc;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by ICN on 14/11/2016.
 */

class FontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);
        if (typeface == null) {
//            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontname);
//            } catch (Exception e) {
//                Log.e("failed", e.getMessage());
//            }
            fontCache.put(fontname, typeface);
        }
        return typeface;
    }
}
