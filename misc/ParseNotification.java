package icn.premierandroid.misc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParsePushBroadcastReceiver;

import icn.premierandroid.R;

public class ParseNotification extends ParsePushBroadcastReceiver {

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon);
        super.getLargeIcon(context, intent);
        return icon;
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        super.getSmallIconId(context, intent);
        return R.drawable.small_icon;
    }
}
