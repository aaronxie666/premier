package icn.premierandroid.misc;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomFontTabLayout extends TabLayout {
    private Typeface mTypeface;

    public CustomFontTabLayout(Context context) {
        super(context);
        init();
    }

    public CustomFontTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFontTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Portrait-Regular2.otf");
    }

    @Override
    public void addTab(Tab tab) {
        super.addTab(tab);
        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());
        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);
            }
        }
    }

}