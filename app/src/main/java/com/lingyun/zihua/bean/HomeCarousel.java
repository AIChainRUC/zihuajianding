package com.lingyun.zihua.bean;

import android.widget.ImageView;
//主页的viewPager实体类
public class HomeCarousel {
    private ImageView imageView;
    private String link;

    public ImageView getImageView() {
        return imageView;
    }

    public String getLink() {
        return link;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
