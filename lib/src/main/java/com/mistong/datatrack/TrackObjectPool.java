package com.mistong.datatrack;

import android.support.v4.util.Pools;

import java.util.HashMap;

/**
 * Created by Ryan Hu on 2017/9/26.
 */

public class TrackObjectPool {

    private static final int DEFAULT_POOL_SIZE = 20;
    private static HashMap<Class<? extends TrackObject>, Pools.SimplePool<TrackObject>> mPools = new HashMap<>();

    public static final <T extends TrackObject> T obtain (Class<T> cls) {

        Pools.SimplePool<TrackObject> pool = mPools.get(cls);
        T instance = pool != null ? (T)pool.acquire() : null;

        try {
            return instance != null ? instance : cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Creating TrackObject error:", e);
        }
    }

    public static final void recycle (TrackObject trackObject) {

        if (trackObject == null) {
            return;
        }

        Pools.SimplePool<TrackObject> pool = mPools.get(trackObject.getClass());
        if (pool == null) {
            pool = new Pools.SimplePool<>(DEFAULT_POOL_SIZE);
        }

        pool.release(trackObject);
    }

}
