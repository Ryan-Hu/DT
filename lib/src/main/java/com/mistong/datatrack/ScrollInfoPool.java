package com.mistong.datatrack;

import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

/**
 * Created by Ryan Hu on 2017/9/25.
 */

public class ScrollInfoPool {

    private static final String TAG = ScrollInfoPool.class.getSimpleName();

    private static final int MAX_POOL_SIZE_PER_TYPE = 10;

    private HashMap<Class<? extends ViewTreeScrollTracker.ScrollInfo>, Pools.SimplePool<ViewTreeScrollTracker.ScrollInfo>> mPools = new HashMap<>();

    private static volatile ScrollInfoPool sInstance;

    private ScrollInfoPool () {
    }

    public static ScrollInfoPool getInstance () {

        if (sInstance == null) {
            synchronized (ScrollInfoPool.class) {
                if (sInstance == null) {
                    sInstance = new ScrollInfoPool();
                }
            }
        }

        return sInstance;
    }

    @Nullable
    public ViewTreeScrollTracker.ScrollInfo acquire (View view) {

        Class<? extends ViewTreeScrollTracker.ScrollInfo> type = getScrollInfoType(view);

        if (type != null) {

            Pools.SimplePool<ViewTreeScrollTracker.ScrollInfo> pool = mPools.get(type);
            if (pool == null) {
                pool = new Pools.SimplePool<>(MAX_POOL_SIZE_PER_TYPE);
                mPools.put(type, pool);
            }

            ViewTreeScrollTracker.ScrollInfo scrollInfo = pool.acquire();
            if (scrollInfo != null) {
                return scrollInfo;
            }

            try {
                return type.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Error creating ScrollInfo for type <" + type.getName() + ">", e);
            }
        }

        return null;
    }

    public void release (ViewTreeScrollTracker.ScrollInfo scrollInfo) {

        Class<? extends ViewTreeScrollTracker.ScrollInfo> type = scrollInfo.getClass();
        if (type != null) {
            Pools.SimplePool<ViewTreeScrollTracker.ScrollInfo> pool = mPools.get(type);
            if (pool == null) {
                pool = new Pools.SimplePool<>(MAX_POOL_SIZE_PER_TYPE);
                mPools.put(type, pool);
            }
            scrollInfo.cleanUp();
            pool.release(scrollInfo);
        }
    }

    public Class<? extends ViewTreeScrollTracker.ScrollInfo> getScrollInfoType (View view) {
        if (view instanceof RecyclerView) {
            return ViewTreeScrollTracker.RecyclerScrollInfo.class;
        } else {
            return ViewTreeScrollTracker.SimpleScrollInfo.class;
        }
    }
}
