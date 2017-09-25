package com.mistong.datatrack;

import java.util.ArrayList;

/**
 * Created by ryanh on 2017/9/24.
 */

public class TrackGroup extends TrackObject {

    private ArrayList<TrackObject> mChildren = new ArrayList<>();

    public int indexOf (TrackObject trackObject) {
        for (int i = 0, size = mChildren.size(); i < size; i++) {
            if (trackObject == mChildren.get(i)) {  //TODO equals ?
                return i;
            }
        }
        return -1;
    }

    public void add (TrackObject trackObject) {
        if (indexOf(trackObject) >= 0) {
            throw new IllegalArgumentException("TrackObject is already added");
        }
        mChildren.add(trackObject);
    }

    public TrackObject remove (int index) {
        return mChildren.remove(index);
    }

    public boolean remove (TrackObject trackObject) {
        return mChildren.remove(trackObject);
    }
}
