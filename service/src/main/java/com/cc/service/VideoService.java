package com.cc.service;

import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

/**
 * @author chenchen
 * @date 2019/5/7 11:03
 * @Content:
 */
public interface VideoService {
    String saveVidoe(Videos video);

    void updateVideo(String videoId, String uploadPathDB);
    //分页查询
    PagedResult getAllVideos(Integer page, Integer pageSize);

}
