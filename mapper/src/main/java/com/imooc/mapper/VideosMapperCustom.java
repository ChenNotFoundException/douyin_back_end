package com.imooc.mapper;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {
    List <VideosVO> queryAllVideos();
}