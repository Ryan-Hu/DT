package com.mistong.datatrack;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;

/**
 * Created by ryanh on 2017/9/24.
 */

public class ViewExposeTrackHelper {

    public static void register (View root) {

        root.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListenerImpl(root));
    }

    private static class OnGlobalLayoutListenerImpl implements ViewTreeObserver.OnGlobalLayoutListener {

        private WeakReference<View> mRootRef;

        private OnGlobalLayoutListenerImpl (View root) {
            mRootRef = new WeakReference<>(root);
        }

        @Override
        public void onGlobalLayout() {

            View root = mRootRef.get();
            if (root == null) {
                return;
            }

            ViewTreeTraverser.traverse(root, new ViewTreeTraverser.ViewProcessor() {
                @Override
                public void process(View view) {
                    if (view instanceof ScrollView) {
                        Log.e("TEST", "found " + view);
                    }
                }
            });
        }
    }
}
