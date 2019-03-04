package com.demo.dao;

import com.demo.model.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CommentMapper {

    int insertComment(@Param("comment") Comment comment);

    List<Comment> getCommentsByPostId(@Param("postId") int postId);

    int getCount();

    List<Comment> getComments();

    int deleteCommentById(@Param("id") int id);
}
