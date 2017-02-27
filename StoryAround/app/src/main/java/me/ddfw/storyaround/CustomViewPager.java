package me.ddfw.storyaround;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by apple on 2017/2/27.
 */

public class CustomViewPager extends ViewPager {
    public final int MAP_PAGE = 1;
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if(this.containsMap()){
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
    public boolean containsMap(){
        return getCurrentItem() == MAP_PAGE;
    }
}
