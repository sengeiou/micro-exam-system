package com.demo.controller;

import com.demo.dto.CommonResult;
import com.demo.model.Post;
import com.demo.service.PostService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/post")
public class PostController {

    private static Log LOG = LogFactory.getLog(PostController.class);

    @Autowired
    private PostService postService;

    //添加帖子
    @PostMapping(value="/api/addPost")
    public CommonResult addPost(@RequestBody Post post) {
        int postId = postService.addPost(post);
        return new CommonResult();
    }

    //更新帖子
    @PostMapping(value="/api/updatePost")
    public CommonResult updatePost(@RequestBody Post post) {
        boolean result = postService.updatePostById(post);
        return new CommonResult();
    }

    //删除帖子
    @DeleteMapping("/api/deletePost/{id}")
    public CommonResult deletePost(@PathVariable int id) {
        boolean result = postService.deletePostById(id);
        return new CommonResult();
    }
}
