package com.mistong.datatrack;

/**
 * Created by ryanh on 2017/9/24.
 */

public interface TrackObject {

    void onExposed ();

    void sendEvent (String action);
}
