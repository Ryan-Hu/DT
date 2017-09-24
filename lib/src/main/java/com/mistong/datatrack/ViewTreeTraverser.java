package com.mistong.datatrack;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ryanh on 2017/9/24.
 */

public class ViewTreeTraverser {

    public static void traverse (View root, ViewProcessor processor) {

        processor.process(root);

        if (root instanceof ViewGroup) {
            final int count = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < count; i++) {
                traverse(((ViewGroup) root).getChildAt(i), processor);
            }
        }
    }

    public interface ViewProcessor {
        void process (View view);
    }
}
