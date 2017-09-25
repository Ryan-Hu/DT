package com.mistong.datatrack;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public class ViewTreeScrollTracker {

    private WeakReference<View> mRootRef;
    private WeakHashMap<View, ScrollInfo> mScrollInfos = new WeakHashMap<>();
    private Rect mTmpRect = new Rect();


    public ViewTreeScrollTracker (View root) {
        mRootRef = new WeakReference<>(root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                restartTrack();
            }
        });
        root.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                trackScroll();
            }
        });
    }

    public void dispose () {
        mRootRef = null;
        recycleScrollInfos();
    }

    public void restartTrack() {

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

                    ScrollInfo scrollInfo = ScrollInfoPool.getInstance().acquire(view);
                    if (scrollInfo != null) {
                        scrollInfo.attach(view);
                        mScrollInfos.put(view, scrollInfo);
                    }
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
            if (scrollInfo.detectScrollChange()) {
                processScrollingView(view, scrollInfo.currentTrackObjects, scrollInfo.previousTrackObjects);
            }
        }
    }

    private void processScrollingView (View scrollingView, Set<TrackObject> currentTrackObjects, Set<TrackObject> previousTrackObjects) {
        findTrackedObjects(scrollingView, currentTrackObjects);
        compareAndUpdate(currentTrackObjects, previousTrackObjects);
    }

    private void findTrackedObjects (View view, Set<TrackObject> trackObjects) {

        if (view instanceof ViewGroup) {
            final ViewGroup parent = (ViewGroup) view;
            final int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                mTmpRect.set(0, 0, child.getWidth(), child.getHeight());
                if (parent.getChildVisibleRect(child, mTmpRect, null)) {  //TODO 按面积比例?
                    TrackObject trackObject = getTrackedObjectFromView(child);
                    if (trackObject != null) {
                        trackObjects.add(trackObject);
                    }
                    findTrackedObjects(child, trackObjects);
                }
            }
        } else {

        }
    }

    private TrackObject getTrackedObjectFromView (View view) {
        return view.getTag() != null ? new StringTrackObject(view.getTag().toString()) : null;
    }

    private void compareAndUpdate (Set<TrackObject> currentTrackObjects, Set<TrackObject> previousTrackObjects) {

        for (TrackObject trackObject : currentTrackObjects) {
            if (!previousTrackObjects.contains(trackObject)) {
                trackObject.onVisibilityChanged(true);
            }
        }
        for (TrackObject trackObject : previousTrackObjects) {
            if (!currentTrackObjects.contains(trackObject)) {
                trackObject.onVisibilityChanged(false);
            }
        }
        previousTrackObjects.clear();
        previousTrackObjects.addAll(currentTrackObjects);
        currentTrackObjects.clear();
    }

    private void recycleScrollInfos () {
        Iterator<Map.Entry<View, ScrollInfo>> iterator = mScrollInfos.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<View, ScrollInfo> entry = iterator.next();
            ScrollInfo scrollInfo = entry.getValue();
            ScrollInfoPool.getInstance().release(scrollInfo);
            iterator.remove();
        }
    }

    public static abstract class ScrollInfo<T extends View> {

        private WeakReference<T> mAttachedView;

        Set<TrackObject> previousTrackObjects = new HashSet<>();
        Set<TrackObject> currentTrackObjects = new HashSet<>();

        public void attach (T view) {
            mAttachedView = new WeakReference<T>(view);
        }

        @Nullable
        public T getAttachedView () {
            return mAttachedView.get();
        }

        public abstract boolean detectScrollChange();

        public void cleanUp () {
            previousTrackObjects.clear();
            currentTrackObjects.clear();
        }
    }

    public static class SimpleScrollInfo extends ScrollInfo<View> {

        private int scrollX;
        private int scrollY;

        @Override
        public boolean detectScrollChange() {
            final int oldScrollX = scrollX;
            final int oldScrollY = scrollY;
            final View view = getAttachedView();
            if (view != null) {
                scrollX = view.getScrollX();
                scrollY = view.getScrollY();
                return oldScrollX != scrollX || oldScrollY != scrollY;
            }
            return false;
        }

        @Override
        public void cleanUp() {
            super.cleanUp();
            scrollX = scrollY = 0;
        }
    }

    public static class RecyclerScrollInfo extends ScrollInfo<RecyclerView> {

        private boolean mScrollChanged = false;
        private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener () {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollChanged = true;
            }
        };

        @Override
        public void attach(RecyclerView view) {
            super.attach(view);
            view.addOnScrollListener(mOnScrollListener);
        }

        @Override
        public boolean detectScrollChange() {
            boolean previousChanged = mScrollChanged;
            mScrollChanged = false;
            return previousChanged;
        }

        @Override
        public void cleanUp() {
            super.cleanUp();
            mScrollChanged = false;
        }
    }
}
