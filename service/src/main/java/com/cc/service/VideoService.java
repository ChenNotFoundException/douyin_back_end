package com.cc.service;

import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

import java.util.List;

/**
 * @author chenchen
 * @date 2019/5/7 11:03
 * @Content:
 */
public interface VideoService {
    String saveVidoe(Videos video);

    void updateVideo(String videoId, String uploadPathDB);
    //分页查询
    PagedResult getAllVideos(Videos video, Integer isSaveRecored, Integer page, Integer pageSize);

    //热搜查询
    List <String> getHotWords();

    //点赞视频
    void userLikeVideo(String userId, String videoId, String videoCreaterId);

    //取消点赞
    void userUnLikeVideo(String userId, String videoId, String videoCreaterId);
}
