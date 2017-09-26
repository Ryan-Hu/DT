package com.mistong.datatrack.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mistong.datatrack.TrackObject;
import com.mistong.datatrack.TrackObjectTree;
import com.mistong.datatrack.ViewTreeScrollTracker;

public class MainActivity extends Activity {

    private TrackObjectTree mTrackObjectTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        mTrackObjectTree = TrackObjectTree.buildFromView(findViewById(android.R.id.content));
        mTrackObjectTree.addTrackObjectTreeListener(new TrackObjectTree.TrackObjectTreeListener() {
            @Override
            public void onTrackObjectAdded(TrackObject trackObject) {
                Log.e("test", "TrackObject " + trackObject.getData() + " is removed");
            }

            @Override
            public void onTrackObjectRemoved(TrackObject trackObject) {
                Log.e("test", "TrackObject " + trackObject.getData() + " is added");
            }

            @Override
            public void onTrackObjectPositionChanged(TrackObject trackObject, int oldPosition, int position) {

            }
        });
    }

    private void initViews () {
        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackObjectTree.print();
            }
        });
        ViewGroup list = (ViewGroup)findViewById(R.id.list);
        for (int i = 0; i < 50; i++) {
            TextView tv = new TextView(this);
            tv.setText(String.format("%d. hahahahhhh", i));
            tv.setTag("S" + i);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(100, 100, 100, 100);
            list.addView(tv);
        }

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new AdapterImpl());
    }

    private class AdapterImpl extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(100, 100, 100, 100);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(String.format("%d. hahahahhhh", position));
            holder.text.setTag("R" + position);
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text;

        public ViewHolder (View itemView) {
            super(itemView);
            text = (TextView)itemView;
        }
    }
}
