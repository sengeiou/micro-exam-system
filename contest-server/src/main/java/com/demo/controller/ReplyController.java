package com.demo.controller;

import com.demo.dto.CommonResult;
import com.demo.model.Reply;
import com.demo.service.ReplyService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/reply")
public class ReplyController {

    private static Log LOG = LogFactory.getLog(ReplyController.class);

    @Autowired
    private ReplyService replyService;

    //添加回复
    @PostMapping(value="/api/addReply")
    public CommonResult addReply(@RequestBody Reply reply) {
        int replyId = replyService.addReply(reply);
        return new CommonResult();
    }
}
