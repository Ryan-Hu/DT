package com.mistong.datatrack;

import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by ryanh on 2017/9/24.
 */

public class TrackObject<T extends View> {

    private WeakReference<T> mViewRef;
    private TrackGroup mParent;
    private Object mData;
    private TrackObjectTree mTree;

    /*package*/ void attachView (T view) {
        mViewRef = new WeakReference<>(view);
        onViewAttached(view);
    }

    protected void onViewAttached (T view) {
    }

    @Nullable
    public T getAttachedView () {
        return mViewRef.get();
    }

    /*package*/ void setParent (TrackGroup parent) {

        if (parent != null && mParent != null) {
            throw new IllegalStateException("TrackObject already has a parent, remove it first");
        }

        mParent = parent;
        TrackObjectTree myTree = getTrackObjectTree();
        TrackObjectTree parentTree = mParent != null ? mParent.getTrackObjectTree() : null;
        if (parentTree != myTree) {
            dispatchTrackObjectTreeChange(parentTree, myTree);
        }
    }

    public TrackGroup getParent () {
        return mParent;
    }

    protected void dispatchTrackObjectTreeChange (TrackObjectTree tree, TrackObjectTree oldTree) {
        if (oldTree != null) {
            onDetachedFromTree(oldTree);
        }
        if (tree != null) {
            onAttachedToTree(tree);
        }
    }

    protected void onAttachedToTree (TrackObjectTree tree) {
        mTree = tree;
    }

    protected void onDetachedFromTree (TrackObjectTree tree) {
        mTree = null;
    }

    public TrackObjectTree getTrackObjectTree () {
        return mTree;
    }

    public void setData (Object data) {
        mData = data;
    }

    public Object getData () {
        return mData;
    }

    public void onVisibilityChanged (boolean visible) {

    }

    public void onDestroy () {
    }

    @Override
    public int hashCode() {
        return mData != null ? mData.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrackObject) {
            TrackObject trackObject = (TrackObject)obj;
            return mData == trackObject.mData || (mData != null && mData.equals(trackObject.mData));
        }
        return false;
    }

    @Override
    public String toString() {
        return mData != null ? mData.toString() : "NO DATA";
    }

}
