package com.mistong.datatrack;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public class RecyclerViewTrackGroup extends ScrollableTrackGroup<RecyclerView> {

    private boolean mScrollChanged = false;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener () {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mScrollChanged = true;
        }
    };

    @Override
    protected void onViewAttached(RecyclerView view) {
        super.onViewAttached(view);
        view.addOnScrollListener(mOnScrollListener);
    }

    @Override
    protected void onAttachedToTree(TrackObjectTree tree) {
        super.onAttachedToTree(tree);
    }

    @Override
    protected void onDetachedFromTree(TrackObjectTree tree) {
        super.onDetachedFromTree(tree);
    }

    @Override
    public boolean detectScrollChanged() {
        boolean previousChanged = mScrollChanged;
        mScrollChanged = false;
        return previousChanged;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RecyclerView scrollable = getAttachedView();
        if (scrollable != null) {
            scrollable.removeOnScrollListener(mOnScrollListener);
        }
        mScrollChanged = false;
    }
}
