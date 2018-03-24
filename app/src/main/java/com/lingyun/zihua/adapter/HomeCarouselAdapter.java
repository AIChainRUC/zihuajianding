package com.lingyun.zihua.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.lingyun.zihua.bean.HomeCarousel;

import java.util.List;

/**
 * 首页viewPager的适配器
 */
public class HomeCarouselAdapter extends PagerAdapter{
    private List<HomeCarousel> picDatas;
    private Context context;
    public HomeCarouselAdapter(List<HomeCarousel> picDatas, Context context){
        this.picDatas = picDatas;
        this.context = context;
    }
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
