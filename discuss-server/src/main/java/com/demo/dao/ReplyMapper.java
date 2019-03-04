package com.demo.dao;

import com.demo.model.Reply;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ReplyMapper {

    int insertReply(@Param("reply") Reply reply);

    List<Reply> getReliesByPostId(@Param("postId") int postId);
}
