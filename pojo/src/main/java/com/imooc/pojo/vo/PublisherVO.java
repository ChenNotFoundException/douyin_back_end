package com.imooc.pojo.vo;

import io.swagger.annotations.ApiModel;


@ApiModel(value = "视频对象", description = "this is视频传输对象")
public class PublisherVO {
    public UsersVO publiser;
    boolean UserLikeVideo;

    public UsersVO getPubliser() {
        return publiser;
    }

    public void setPubliser(UsersVO publiser) {
        this.publiser = publiser;
    }

    public boolean isUserLikeVideo() {
        return UserLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        UserLikeVideo = userLikeVideo;
    }
}