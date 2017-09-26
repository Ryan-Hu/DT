package com.mistong.datatrack;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public abstract class ScrollableTrackGroup<T extends View> extends TrackGroup<T> implements ViewTreeObserver.OnScrollChangedListener {

//    private WeakReference<T> mScrollableViewRef;
    private Rect mTmpRect = new Rect();
    private TrackGroup mTmpTrackGroup = new TrackGroup();

    @Override
    protected void onViewAttached(T view) {
        super.onViewAttached(view);
        if (getTrackObjectTree() != null) {
            view.getViewTreeObserver().addOnScrollChangedListener(this);
        }
    }

    @Override
    protected void onAttachedToTree(TrackObjectTree tree) {
        super.onAttachedToTree(tree);
        T scrollable = getAttachedView();
        if (scrollable != null) {
            scrollable.getViewTreeObserver().addOnScrollChangedListener(this);
        }
    }

    @Override
    protected void onDetachedFromTree(TrackObjectTree tree) {
        super.onDetachedFromTree(tree);
        T scrollable = getAttachedView();
        if (scrollable != null) {
            scrollable.getViewTreeObserver().removeOnScrollChangedListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onScrollChanged() {
        final View scrollable = getAttachedView();
        if (scrollable != null && detectScrollChanged()) {
//            findTrackObjects(scrollable, mTmpTrackGroup);
            TrackObjectTree.traverse(scrollable, mTmpTrackGroup);
            TrackObject realTrackObject = mTmpTrackGroup.get(0);
            TrackGroup realTrackGroup = realTrackObject instanceof TrackGroup ? (TrackGroup)realTrackObject : TrackGroup.EMPTY;
            compareAndNotifyChange(realTrackGroup, this);
            removeAll();
            for (int k = realTrackGroup.size() - 1; k >= 0; k--) {
                TrackObject child = realTrackGroup.remove(k);
                add(child);
            }
            mTmpTrackGroup.removeAll();
        }
    }

    public abstract boolean detectScrollChanged ();

    protected void findTrackObjects (View view, TrackGroup container) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;
            final int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                mTmpRect.set(0, 0, child.getWidth(), child.getHeight());
                if (viewGroup.getChildVisibleRect(child, mTmpRect, null)) {  //TODO 按面积比例?
//                    TrackObject trackObject = ViewTrackUtil.getTrackedObjectFromView(child);
                    TrackObject trackObject = TrackObjectMapper.getInstance().fromView(child);
                    if (trackObject != null) {
                        container.add(trackObject);
                    }
                    if (trackObject instanceof TrackGroup) {
                        TrackGroup trackGroup = (TrackGroup)trackObject;
                        findTrackObjects(child, trackGroup);
//                        compareAndNotifyChange(trackGroup, this);
//                        removeAll();
//                        for (int k = 0, size = trackGroup.size(); k < size; k++) {
//                            add(trackGroup.get(k));
//                        }
                    }
                }
            }
        }
    }

    private void compareAndNotifyChange (TrackGroup current, TrackGroup previous) {

//        StringBuilder sb = new StringBuilder("-----------------------  ");
//        for (int i = 0, size = previous.size(); i < size; i++) {
//            sb.append(previous.get(i)).append(",");
//        }
//        sb.append("   ->    ");
//        for (int i = 0, size = current.size(); i < size; i++) {
//            sb.append(current.get(i)).append(",");
//        }
//        Log.e("test", sb.toString());

        for (int i = 0, size = previous.size(); i < size; i++) {
            TrackObject prevChild = previous.get(i);
            int index = current.indexOf(prevChild);
            TrackObject currChild = index >= 0 ? current.get(index) : null;     //currChild.equals(prevChild), currChild != prevChild
            if (currChild == null) {    //not found, removed
                TrackObjectTree tree = getTrackObjectTree();
                if (tree != null) {
                    tree.notifyTrackObjectAdded(prevChild);
                }
            } else if (currChild instanceof TrackGroup && prevChild instanceof TrackGroup) {
                compareAndNotifyChange((TrackGroup)currChild, (TrackGroup)prevChild);
            }
        }

        for (int i = 0, size = current.size(); i < size; i++) {
            TrackObject currChild = current.get(i);
            if (!previous.contain(currChild)) {
                TrackObjectTree tree = getTrackObjectTree();
                if (tree != null) {
                    tree.notifyTrackObjectRemoved(currChild);
                }
            }
        }
    }

}
