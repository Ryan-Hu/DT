package com.mistong.datatrack;

import android.view.View;

/**
 * Created by Ryan Hu on 2017/9/26.
 */

public class SimpleScrollTrackGroup extends ScrollableTrackGroup<View> {

    private int scrollX;
    private int scrollY;

    @Override
    public boolean detectScrollChanged() {
        final int oldScrollX = scrollX;
        final int oldScrollY = scrollY;
        final View scrollable = getAttachedView();
        if (scrollable != null) {
            scrollX = scrollable.getScrollX();
            scrollY = scrollable.getScrollY();
            return oldScrollX != scrollX || oldScrollY != scrollY;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scrollX = scrollY = 0;
    }
}
