package com.demo.controller;

import com.demo.dto.CommonResult;
import com.demo.model.Question;
import com.demo.service.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/question")
public class QuestionController {

    private static Log LOG = LogFactory.getLog(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    //添加题目
    @PostMapping(value="/api/addQuestion")
    public CommonResult addQuestion(@RequestBody Question question) {
        int questionId = questionService.addQuestion(question);
        return new CommonResult();
    }

    //更新题目信息
    @PostMapping(value="/api/updateQuestion")
    public CommonResult updateQuestion(@RequestBody Question question) {
        boolean result = questionService.updateQuestion(question);
        return new CommonResult();
    }

    //删除题目信息
    @DeleteMapping("/api/deleteQuestion/{id}")
    public CommonResult deleteContest(@PathVariable int id) {
        boolean result = questionService.deleteQuestion(id);
        return new CommonResult();
    }
}
