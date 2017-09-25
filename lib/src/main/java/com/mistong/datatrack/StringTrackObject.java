package com.mistong.datatrack;

import android.util.Log;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public class StringTrackObject extends TrackObject {

    private String mContent;

    public StringTrackObject(String content) {
        mContent = content;
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Log.e("test", "TAG " + mContent + " " + visible);
    }

    @Override
    public void sendEvent(String action) {

    }

    @Override
    public int hashCode() {
        return mContent != null ? mContent.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StringTrackObject)) {
            return false;
        }
        return mContent != null && mContent.equals(((StringTrackObject)obj).mContent);
    }
}
