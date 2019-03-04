package com.demo.service;

import com.demo.model.Reply;

import java.util.List;

public interface ReplyService {

    int addReply(Reply reply);

    List<Reply> getReliesByPostId(int postId);
}
