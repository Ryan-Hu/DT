package com.mistong.datatrack;

import android.view.View;

/**
 * Created by ryanh on 2017/9/24.
 */

public class ViewExposeTracker {

    private ViewExposeObserver mObserver;

    public ViewExposeTracker (ViewExposeObserver observer) {
        mObserver = observer;
    }

    public ViewExposeObserver getViewExposeObserver () {
        return mObserver;
    }

    public void notifyExposed (View view) {
        mObserver.onExposed(view);
    }
}
