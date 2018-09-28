package com.lingyun_chain.traceGoodsSeller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lingyun_chain.traceGoodsSeller.bean.HomeCarousel;

import java.util.List;

/**
 * 首页viewPager的适配器
 */
public class HomeCarouselAdapter extends PagerAdapter {
    private List<HomeCarousel> picDatas;
    private Context context;

    public HomeCarouselAdapter(List<HomeCarousel> picDatas, Context context) {
        this.picDatas = picDatas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return picDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(picDatas.get(position).getImageView()).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
