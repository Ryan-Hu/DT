package com.mistong.datatrack;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by ryanh on 2017/9/24.
 */

public class TrackGroup<T extends View> extends TrackObject<T> {

    public static final TrackGroup EMPTY = new TrackGroup();

    private ArrayList<TrackObject> mChildren = new ArrayList<>();

    public int indexOf (TrackObject trackObject) {
        if (trackObject != null) {
            for (int i = 0, size = mChildren.size(); i < size; i++) {
                if (trackObject.equals(mChildren.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int size () {
        return mChildren.size();
    }

    public TrackObject get (int index) {
        return mChildren.get(index);
    }

    public boolean contain (TrackObject trackObject) {
        return indexOf(trackObject) >= 0;
    }

    public void add (TrackObject trackObject) {
        if (indexOf(trackObject) >= 0) {
            throw new IllegalArgumentException("TrackObject is already added");
        }
        trackObject.setParent(this);
        mChildren.add(trackObject);
    }

    public TrackObject remove (int index) {
        TrackObject removed = mChildren.remove(index);
        removed.setParent(null);
        return removed;
    }

    public boolean remove (TrackObject trackObject) {
        boolean result = mChildren.remove(trackObject);
        if (result) {
            trackObject.setParent(null);
        }
        return result;
    }

    public void removeAll () {
        for (int i = mChildren.size() - 1; i >= 0; i--) {
            TrackObject removed = mChildren.remove(i);
            removed.setParent(null);
        }
    }

    @Override
    protected void dispatchTrackObjectTreeChange(TrackObjectTree tree, TrackObjectTree oldTree) {
        super.dispatchTrackObjectTreeChange(tree, oldTree);
        for (TrackObject child : mChildren) {
            child.dispatchTrackObjectTreeChange(tree, oldTree);
        }
    }
}
