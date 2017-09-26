package com.mistong.datatrack;

import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public class ViewTrackUtil {

    private static Rect sRect = new Rect();

    public static boolean isViewOnScreen (View view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            sRect.set(0, 0, view.getWidth(), view.getHeight());
            return parent.getChildVisibleRect(view, sRect, null);
        }
        return false;
    }

    public static boolean canViewScroll (View view) {
        return view instanceof ScrollView
                || view instanceof AbsListView
                || view instanceof RecyclerView
                || view instanceof NestedScrollView
                || view instanceof ViewPager;
    }

}
