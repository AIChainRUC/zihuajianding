package com.lingyun_chain.zihua.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.activity.GenerateCertificateActivity;
import com.lingyun_chain.zihua.activity.IdentifyCalligraphyActivity;
import com.lingyun_chain.zihua.activity.SignCertificateActivity;
import com.lingyun_chain.zihua.activity.StoreCalligraphyActivity;
import com.lingyun_chain.zihua.adapter.HomeCarouselAdapter;
import com.lingyun_chain.zihua.adapter.MyHomeListAdapter;
import com.lingyun_chain.zihua.base.BaseFragement;
import com.lingyun_chain.zihua.bean.HomeCarousel;
import com.lingyun_chain.zihua.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页的Fragement 上面是一个ViewPager，下面是一个ListView，包含字画存链，鉴定，证书的生成和使用
 */
public class ShouYeFragement extends BaseFragement implements AdapterView.OnItemClickListener {
    private ViewPager vpHomeTitle;//图片轮播条ViewPager
    LinearLayout pointGroup;//轮播条指示点
    private View view;//整个界面的view
    private List<HomeCarousel> picDatas;//图片地址和链接的集合
    private ListView main_list;
    /**
     * 上一个页面的位置
     */
    protected int lastPosition = 0;
    /**
     * 判断是否自动滚动
     */
    private boolean isRunning = false;
    private AuToRunTask runTask;
    private MyHomeListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_shouye, container, false);
        main_list = (ListView) view.findViewById(R.id.main_list);
        vpHomeTitle = (ViewPager)view.findViewById(R.id.vp_home_title);
        pointGroup = (LinearLayout) view.findViewById(R.id.point_group);
        initListView();
        if (picDatas == null) {
            //如果没有数据，就从网络中获取
            initImageUrl();
        } else {
            //如果有数据，去除掉所有的指示点，再次初始化原点坐标
            pointGroup.removeAllViews();
            initIndicator();
        }
        return view;
    }
    @Override
    public void onPause() {
        if (runTask != null) {
            runTask.stop();
        }
        isRunning = false;
        super.onPause();
    }
    @Override
    public void onDestroyView() {
        if (runTask != null) {
            runTask = null;
        }
        isRunning = false;
        super.onDestroyView();
    }
    /**
     * 初始化原点指示器
     */
    private void initIndicator() {
        UiUtils.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < picDatas.size(); i++) {
                    ImageView point = new ImageView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 20;
                    params.bottomMargin = 10;
                    point.setLayoutParams(params);
                    point.setBackgroundResource(R.drawable.point_bg);
                    if (lastPosition == i) {
                        point.setEnabled(true);
                    } else {
                        point.setEnabled(false);
                    }
                    pointGroup.addView(point);
                }
                vpHomeTitle.setAdapter(new HomeCarouselAdapter(picDatas, getActivity()));
                vpHomeTitle.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        position = position % picDatas.size();
                        //改变指示点的状态
                        //把当前点enbale 为true
                        pointGroup.getChildAt(position).setEnabled(true);
                        //把上一个点设为false
                        pointGroup.getChildAt(lastPosition).setEnabled(false);
                        lastPosition = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                vpHomeTitle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                runTask.stop();
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                runTask.start();
                                break;
                        }
                        return false;//true的话消费掉了
                    }
                });
                runTask = new AuToRunTask();
                runTask.start();
            }
        });

    }

    private void initImageUrl() {
        //List<HomeCarousel> list = new ArrayList<>();
        picDatas = new ArrayList<>();
        HomeCarousel homeCarousel;
        homeCarousel = new HomeCarousel(R.mipmap.next_icon, "www.baidu.com");
        picDatas.add(homeCarousel);
        picDatas.add(new HomeCarousel(R.mipmap.ic_launcher,"www.baidu.com"));
        initIndicator();
//        homeCarousel.setImageView(R.mipmap.background);
//        homeCarousel.setLink("www.baidu.com");
//        picDatas.add(homeCarousel);
//        initIndicator();
    }

    private void initListView() {
        adapter = new MyHomeListAdapter(getActivity());
        main_list.setAdapter(adapter);
        main_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(getActivity(), StoreCalligraphyActivity.class));//字画存链
                break;
            case 1:
                startActivity(new Intent(getActivity(), IdentifyCalligraphyActivity.class));//字画鉴定页面
                break;
            case 2:
                startActivity(new Intent(getActivity(), GenerateCertificateActivity.class));
                break;
            default:
                startActivity(new Intent(getActivity(), SignCertificateActivity.class));
                break;
        }
    }

    /**
     * viewpager自动轮询的任务
     */
    public class AuToRunTask implements Runnable {

        @Override
        public void run() {
            if (isRunning) {
                int currentItem = vpHomeTitle.getCurrentItem();
                if (currentItem < picDatas.size() - 1) {
                    currentItem++;
                    vpHomeTitle.setCurrentItem(currentItem);
                } else {
                    //返回第一个条目
                    vpHomeTitle.setCurrentItem(0);
                }
                //延迟执行当前任务，递归调用
                UiUtils.postDelayed(this, 3000);
            } else {
                //如果标示变化，取消这个任务
                UiUtils.cancel(this);
            }
        }

        /**
         * 判断之前是否正在轮播，如果是，就不用再开始了
         * 如果不是，就再次开始执行任务的run方法
         */
        public void start() {
            if (!isRunning) {
                UiUtils.cancel(this);//先取消之前的任务
                isRunning = true;
                UiUtils.postDelayed(this, 3000);
            }
        }

        public void stop() {
            if (isRunning) {
                isRunning = false;
                UiUtils.cancel(this);
            }
        }
    }
}
