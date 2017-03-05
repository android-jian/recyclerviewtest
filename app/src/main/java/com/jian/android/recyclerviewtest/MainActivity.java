package com.jian.android.recyclerviewtest;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mDatas;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private LinearLayoutManager layoutManager;

    int lastVisibleItem;        //最后可见条目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatas=new ArrayList<String>();

        for (int i=0;i<20;i++){
            mDatas.add("这是第"+i+"条数据");
        }

        swipe_refresh= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final MyAdapter adapter=new MyAdapter(mDatas);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));

        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(2000);

                            for (int i=0;i<5;i++){
                                mDatas.add(0,"这是新增的第"+i+"条数据");
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                swipe_refresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 ==adapter.getItemCount()
                        && adapter.getLoadMoreStatus()!=MyAdapter.LOADING_MORE) {
                    adapter.changeMoreStatus(MyAdapter.LOADING_MORE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            List<String> newDatas = new ArrayList<String>();
                            for (int i = 0; i< 5; i++) {
                                int index = i +1;
                                newDatas.add("more item" + index);
                            }
                            mDatas.addAll(newDatas);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter.notifyDataSetChanged();
                                    adapter.changeMoreStatus(MyAdapter.PULLUP_LOAD_MORE);
                                }
                            });
                        }
                    }).start();

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem =layoutManager.findLastVisibleItemPosition();
            }
        });
    }
}
