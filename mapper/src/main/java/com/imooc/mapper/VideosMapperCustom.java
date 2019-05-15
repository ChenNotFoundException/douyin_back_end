package com.imooc.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {
    List <VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
                                   @Param("userId") String userId);

    /**
     * 点赞
     *
     * @param videoId
     */
    void addVideoLikeCount(String videoId);

    /**
     * 取消点赞
     *
     * @param videoId
     */
    void reduceVideoLikeCount(String videoId);

    List <VideosVO> queryMyFollowVideos(String userId);

    List <VideosVO> queryMyLikeVideos(String userId);
}