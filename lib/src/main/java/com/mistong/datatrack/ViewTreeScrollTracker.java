package com.mistong.datatrack;

import android.support.v4.util.Pools;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by ryanh on 2017/9/24.
 */

public class ViewTreeScrollTracker {

    private WeakReference<View> mRootRef;
    private WeakHashMap<View, ScrollInfo> mScrollInfos = new WeakHashMap<>();
    private static Pools.SimplePool<ScrollInfo> sScrollInfoPool = new Pools.SimplePool<>(20);
    private ArrayList<View> mScrollingView = new ArrayList<>();


    public ViewTreeScrollTracker (View root) {
        mRootRef = new WeakReference<>(root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                reset();
            }
        });
        root.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                trackScroll();
            }
        });
    }

    public void reset () {

        recycleScrollInfos();

        final View root = mRootRef.get();
        if (root == null) {
            return;
        }

        ViewTreeTraverser.traverse(root, new ViewTreeTraverser.ViewProcessor() {
            @Override
            public void process(View view) {

                if (view.getVisibility() == View.VISIBLE
                        && isViewOnScreen(view)
                        && canViewScroll(view)) {

                    ScrollInfo scrollInfo = sScrollInfoPool.acquire();
                    if (scrollInfo == null) {
                        scrollInfo = new ScrollInfo();
                    }

                    scrollInfo.setFromView(view);
                    mScrollInfos.put(view, scrollInfo);
                }
            }
        });
    }

    private boolean canViewScroll (View view) {
        return view instanceof ScrollView
                || view instanceof AbsListView
                || view instanceof RecyclerView
                || view instanceof NestedScrollView
                || view instanceof ViewPager;
    }

    private boolean isViewOnScreen (View view) {
        return true;    //TODO
    }

    public void trackScroll () {

        for (Map.Entry<View, ScrollInfo> entry : mScrollInfos.entrySet()) {
            View view = entry.getKey();
            ScrollInfo scrollInfo = entry.getValue();
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            Log.e("test", view.getClass().getSimpleName() + String.format("%d, %d -> %d, %d", scrollInfo.scrollX, scrollInfo.scrollY, scrollX, scrollY));
            if (scrollX != scrollInfo.scrollX || scrollY != scrollInfo.scrollY) {
                mScrollingView.add(view);
                scrollInfo.scrollX = scrollX;
                scrollInfo.scrollY = scrollY;
            }
        }

        processScrollingViews(mScrollingView);
        mScrollingView.clear();
    }

    private void processScrollingViews (ArrayList<View> scrollingViews) {
        for (View view : scrollingViews) {
            Log.e("test",
                    "View " + view.getClass().getSimpleName() + " is scrolling -> "
                            + view.getScrollX() + ", " + view.getScrollY());
        }
    }

    private void recycleScrollInfos () {
        Iterator<Map.Entry<View, ScrollInfo>> iterator = mScrollInfos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<View, ScrollInfo> entry = iterator.next();
            ScrollInfo scrollInfo = entry.getValue();
            sScrollInfoPool.release(scrollInfo);
            iterator.remove();
        }
    }

    private class ScrollInfo {

        int scrollX;
        int scrollY;

        public void setFromView (View view) {
            scrollX = view.getScrollX();
            scrollY = view.getScrollY();
        }
    }

}
