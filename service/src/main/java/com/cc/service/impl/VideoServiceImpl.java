package com.cc.service.impl;

import com.cc.service.VideoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.List;

/**
 * @author chenchen
 * @date 2019/5/7 11:05
 * @Content:
 */
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private VideosMapperCustom videosMapperCustom;
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.保存用户和视频点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);

        usersLikeVideosMapper.insert(ulv);

        //2、数量累加
        videosMapperCustom.addVideoLikeCount(videoId);
        //3、用户收到点赞累加
        usersMapper.addReceiveLikeCount(userId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.删除用户和视频点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        usersLikeVideosMapper.deleteByExample(example);

        //2、数量--
        videosMapperCustom.reduceVideoLikeCount(videoId);
        //3、用户收到点赞--
        usersMapper.reduceReceiveLikeCount(userId);
    }



    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVidoe(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);
        return id;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List <String> getHotWords() {

        return searchRecordsMapper.getHotWords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecored, Integer page, Integer pageSize) {
//保存热搜词
        String desc = video.getVideoDesc();
        if (isSaveRecored != null && isSaveRecored == 1) {
            SearchRecords record = new SearchRecords();
            record.setId(sid.nextShort());
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page, pageSize);
        List <VideosVO> list = videosMapperCustom.queryAllVideos(desc);

        PageInfo <VideosVO> pagelist = new PageInfo <>(list);

        PagedResult result = new PagedResult();
        result.setPage(page);
        result.setTotal(pagelist.getPages());
        result.setRows(list);
        result.setRecords(pagelist.getTotal());

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String uploadPathDB) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(uploadPathDB);
        videosMapper.updateByPrimaryKeySelective(video);
    }
}
