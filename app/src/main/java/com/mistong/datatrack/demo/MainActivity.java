package com.mistong.datatrack.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mistong.datatrack.ViewExposeTrackHelper;
import com.mistong.datatrack.ViewTreeScrollTracker;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    private ViewTreeScrollTracker mViewTreeScrollTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
//        ViewExposeTrackHelper.register(findViewById(android.R.id.content));
        mViewTreeScrollTracker = new ViewTreeScrollTracker(findViewById(android.R.id.content));
    }

    private void initViews () {
        ViewGroup list = (ViewGroup)findViewById(R.id.list);
        for (int i = 0; i < 10; i++) {
            TextView tv = new TextView(this);
            tv.setText(String.format("%d. hahahahhhh", i));
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
        }

        @Override
        public int getItemCount() {
            return 100;
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
