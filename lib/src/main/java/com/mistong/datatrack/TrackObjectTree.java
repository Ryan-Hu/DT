package com.mistong.datatrack;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public class TrackObjectTree extends TrackGroup {

//    private TrackGroup mRoot;
//    private WeakReference<View> mRootView;
    private ArrayList<TrackObjectTreeListener> mListeners = new ArrayList<>();
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            rebuild();
        }
    };

    public static TrackObjectTree buildFromView (View rootView) {

        TrackObjectTree tree = new TrackObjectTree();
//        tree.mRootView = new WeakReference<>(rootView);
//        tree.mRoot = new TrackGroup();
        tree.attachView(rootView);
        tree.rebuild();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(tree.mOnGlobalLayoutListener);
        return tree;
    }

    @Override
    public TrackObjectTree getTrackObjectTree() {
        return this;
    }

    private void rebuild () {
        final View rootView = getAttachedView();
        removeAll();
        if (rootView != null) {
            traverse(rootView, this);
        }
    }

    public static void traverse (View view, TrackGroup parentTrackGroup) {

        if (view.getVisibility() == View.VISIBLE
                && ViewTrackUtil.isViewOnScreen(view)) {
            TrackObject trackObject = TrackObjectMapper.getInstance().fromView(view);
            if (trackObject != null) {
                parentTrackGroup.add(trackObject);
            }

            if (view instanceof ViewGroup &&
                    (trackObject == null || trackObject instanceof TrackGroup)) {

                if (trackObject != null) {
                    parentTrackGroup = (TrackGroup)trackObject;
                }
                ViewGroup viewGroup = (ViewGroup)view;
                for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
                    traverse(viewGroup.getChildAt(i), parentTrackGroup);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListeners.clear();
        final View rootView = getAttachedView();
        if (rootView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            }
        }
    }

    public void print () {
    }


    public void addTrackObjectTreeListener (TrackObjectTreeListener listener) {
        mListeners.add(listener);
    }

    public void removeTrackObjectTreeListener (TrackObjectTreeListener listener) {
        mListeners.remove(listener);
    }

    /*package*/ void notifyTrackObjectAdded (TrackObject trackObject) {
        for (TrackObjectTreeListener listener : mListeners) {
            listener.onTrackObjectAdded(trackObject);
        }
    }

    /*package*/ void notifyTrackObjectRemoved (TrackObject trackObject) {
        for (TrackObjectTreeListener listener : mListeners) {
            listener.onTrackObjectRemoved(trackObject);
        }
    }

    /*package*/ void notifyTrackObjectPositionChanged (TrackObject trackObject, int oldPosition, int position) {
        for (TrackObjectTreeListener listener : mListeners) {
            listener.onTrackObjectPositionChanged(trackObject, oldPosition, position);
        }
    }

    /**
     *
     */
    public interface TrackObjectTreeListener {

        void onTrackObjectAdded (TrackObject trackObject);

        void onTrackObjectRemoved (TrackObject trackObject);

        void onTrackObjectPositionChanged (TrackObject trackObject, int oldPosition, int position);
    }
}
