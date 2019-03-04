package com.demo.dao;

import com.demo.model.Post;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


public interface PostMapper {

    int insertPost(@Param("post") Post post);

    int updatePostById(@Param("post") Post post);

    Post getPostById(@Param("id") int id);

    int deletePostById(@Param("id") int id);

    int getCount();

    List<Post> getPosts();

    int updateReplyNumById(@Param("id") int id, @Param("lastReplyTime") Date lastReplyTime);

    int getCountByAuthorId(@Param("authorId") int authorId);

    List<Post> getPostsByAuthorId(@Param("authorId") int authorId);
}
