package com.demo.controller;

import com.demo.dto.CommonResult;
import com.demo.model.Comment;
import com.demo.service.CommentService;
import com.demo.service.PostService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/comment")
public class CommentController {

    private static Log LOG = LogFactory.getLog(CommentController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    //添加评论
    @PostMapping(value="/api/addComment")
    public CommonResult addComment(@RequestBody Comment comment) {
        CommonResult commonResult = new CommonResult();
        postService.updateReplyNumById(comment.getPostId());
        int commentId = commentService.addComment(comment);
        return new CommonResult().setData(commentId);
    }

    //删除评论
    @DeleteMapping("/api/deleteComment/{id}")
    public CommonResult deleteComment(@PathVariable int id) {
        CommonResult commonResult = new CommonResult();
        boolean result = commentService.deleteCommentById(id);
        return new CommonResult().setData(result);
    }
}
