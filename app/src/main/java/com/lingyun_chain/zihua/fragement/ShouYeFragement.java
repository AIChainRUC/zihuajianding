package com.lingyun_chain.zihua.fragement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.base.BaseFragement;
import com.lingyun_chain.zihua.bean.HomeCarousel;
import com.lingyun_chain.zihua.constants.IntentConstants;
import com.lingyun_chain.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private String generatePublicKey = null;//公钥
    private String generatePrivateKey = null;//私钥
    private String generateCertificate = null;//证书
    private String generateFaceFeature = null;//人脸特征

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_shouye, container, false);
        main_list = (ListView) view.findViewById(R.id.main_list);
        vpHomeTitle = (ViewPager) view.findViewById(R.id.vp_home_title);
        pointGroup = (LinearLayout) view.findViewById(R.id.point_group);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreference.edit();
        if (sharedPreference != null) {
            generatePublicKey = sharedPreference.getString("generatePublicKey", "default");
            generatePrivateKey = sharedPreference.getString("generatePrivateKey", "default");
            generateCertificate = sharedPreference.getString("generateCertificate", "default");
            generateFaceFeature = sharedPreference.getString("generateFaceFeature", "default");
        }
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
        homeCarousel = new HomeCarousel(R.mipmap.logo, "www.baidu.com");
        picDatas.add(homeCarousel);
        // picDatas.add(new HomeCarousel(R.mipmap.ic_launcher, "www.baidu.com"));
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
                if (!TextUtils.equals(generatePublicKey, "default") && !TextUtils.equals(generatePrivateKey, "default")) {
                    startActivity(new Intent(getActivity(), StoreCalligraphyActivity.class));
                    //new AsyUserFeatureTask(getActivity(), "AsyUserFeatureTask", generateCertificate).execute();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("温馨提示");
//                    builder.setMessage("为了保证您的安全，我们建议您拍摄含眨眼动作的短视频进行人脸识别");
////                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        UiUtils.show("对不起，请您先进行人脸识别");
////                    }
////                });
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            startActivityForResult(new Intent(getActivity(), SignCertificateActivity.class), IntentConstants.GO_TO_FACE);//
//                        }
//                    });
//                    builder.create().show();
                    // startActivityForResult(new Intent(getActivity(), StoreCalligraphyActivity.class), GO_TO_FACE);//字画存链
                } else {
                    //还没有从服务器拿到证书
                    UiUtils.show("对不起，请您先生成证书");
                    startActivityForResult(new Intent(getActivity(), GenerateCertificateActivity.class), IntentConstants.GO_TO_KEY);
                }
                break;
            case 1:
                startActivity(new Intent(getActivity(), IdentifyCalligraphyActivity.class));//字画鉴定页面
                break;
            case 2:
                startActivity(new Intent(getActivity(), GenerateCertificateActivity.class));//生成证书
                break;
            default:
                //startActivity(new Intent(getActivity(), SignCertificateActivity.class));//
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IntentConstants.GO_TO_KEY) {
                UiUtils.show("生成证书成功");
                generatePublicKey = data.getStringExtra("generatePublicKey");
                generatePrivateKey = data.getStringExtra("generatePrivateKey");
                generateCertificate = data.getStringExtra("generateCertificate");
                generateFaceFeature = data.getStringExtra("generateFaceFeature");
                new AsyUserFeatureTask(getActivity(), "AsyUserFeatureTask", generateCertificate).execute();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("温馨提示");
//                builder.setMessage("为了保证您的安全，我们建议您拍摄含眨眼动作的短视频进行人脸识别");
////                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        UiUtils.show("对不起，请您先进行人脸识别");
////                    }
////                });
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new AsyUserFeatureTask(getActivity(), "AsyUserFeatureTask", generateCertificate).execute();
//                        //startActivityForResult(new Intent(getActivity(), SignCertificateActivity.class), IntentConstants.GO_TO_FACE);//
//                    }
//                });
//                builder.create().show();
                //startActivity(new Intent(getActivity(), StoreCalligraphyActivity.class));//字画存链
            } else if (requestCode == IntentConstants.GO_TO_FACE) {
                startActivity(new Intent(getActivity(), StoreCalligraphyActivity.class));
            } else {
                UiUtils.show("生成证书失败");
            }
        } else {
            UiUtils.show("拍照失败");
        }
    }

    public class AsyUserFeatureTask extends BaseAsyTask {//根据证书拿到人脸特征
        private String status = "-1";
        private String featureFace = "default";

        public AsyUserFeatureTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                featureFace = jsonObject.optString("feature");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.equals(s, "200")) {
                featureFace = generateFaceFeature;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("温馨提示");
            builder.setMessage("为了保证您的安全，我们建议您拍摄缓慢眨眼动作的短视频进行人脸识别");
////                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        UiUtils.show("对不起，请您先进行人脸识别");
////                    }
//              });
            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  new AsyUserFeatureTask(getActivity(), "AsyUserFeatureTask", generateCertificate).execute();
                    startActivityForResult(new Intent(getActivity(), SignCertificateActivity.class), IntentConstants.GO_TO_FACE);//
                }
            });
            builder.create().show();
            //  startActivityForResult(new Intent(getActivity(), SignCertificateActivity.class), IntentConstants.GO_TO_FACE);//
//            setResult(RESULT_OK);
//            finish();
        }
    }
}
