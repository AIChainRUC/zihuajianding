package com.lingyun_chain.traceGoodsSeller.bean;

//主页的viewPager实体类
public class HomeCarousel {
    private int imageView;
    private String link;

    public HomeCarousel(int imageView,String link){
        this.imageView = imageView;
        this.link = link;
    }
    public int getImageView() {
        return imageView;
    }

    public String getLink() {
        return link;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
