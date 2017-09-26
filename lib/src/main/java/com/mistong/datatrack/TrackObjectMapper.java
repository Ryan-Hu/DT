package com.mistong.datatrack;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * Created by Ryan Hu on 2017/9/26.
 */

public class TrackObjectMapper {

    private static volatile TrackObjectMapper sInstance;

    public static TrackObjectMapper getInstance () {
        if (sInstance == null) {
            synchronized (TrackObjectMapper.class) {
                if (sInstance == null) {
                    sInstance = new TrackObjectMapper();
                }
            }
        }
        return sInstance;
    }

    private TrackObjectMapper () {
    }

    public TrackObject fromView (View view) {

        Object data = findDataFromView(view);
        if (data == null) {
            return null;
        }

        TrackObject result = null;
        if (view instanceof RecyclerView) {
            result = TrackObjectPool.obtain(RecyclerViewTrackGroup.class);
        } else if (view instanceof ScrollView
                || view instanceof AbsListView
                || view instanceof NestedScrollView
                || view instanceof ViewPager) {
            result = TrackObjectPool.obtain(SimpleScrollTrackGroup.class);
        } else {
            result =  TrackObjectPool.obtain(TrackGroup.class);
        }

        if (result != null) {
            result.attachView(view);
            result.setData(data);
        }

        return result;
    }

    public Object findDataFromView (View view) {
        return view.getTag();
    }
}
