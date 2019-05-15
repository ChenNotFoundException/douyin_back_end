package com.imooc.mapper;

import com.imooc.pojo.Comments;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper <Comments> {
    List <CommentsVO> queryComments(String videoId);
}