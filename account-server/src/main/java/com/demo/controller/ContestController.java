package com.demo.controller;

import com.demo.dto.CommonResult;
import com.demo.model.Contest;
import com.demo.service.ContestService;
import com.demo.service.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/contest")
public class ContestController {

    private static Log LOG = LogFactory.getLog(ContestController.class);

    @Autowired
    private ContestService contestService;
    @Autowired
    private QuestionService questionService;

    //添加考试
    @PostMapping(value="/api/addContest")
    public CommonResult addContest(@RequestBody Contest contest) {
        CommonResult commonResult = new CommonResult();
        int contestId = contestService.addContest(contest);
        return new CommonResult().setData(contestId);
    }

    //更新考试信息
    @PostMapping(value="/api/updateContest")
    public CommonResult updateContest(@RequestBody Contest contest) {
        CommonResult commonResult = new CommonResult();
        boolean result = contestService.updateContest(contest);
        return new CommonResult().setData(result);
    }

    //删除考试信息
    @DeleteMapping("/api/deleteContest/{id}")
    public CommonResult deleteContest(@PathVariable int id) {
        CommonResult commonResult = new CommonResult();
        boolean result = contestService.deleteContest(id);
        return new CommonResult().setData(result);
    }

    //完成考试批改
    @PostMapping(value="/api/finishContest/{id}")
    public CommonResult finishContest(@PathVariable int id) {
        CommonResult commonResult = new CommonResult();
        Contest contest = contestService.getContestById(id);
        contest.setState(3);
        questionService.updateQuestionsStateByContestId(id, 1);
        boolean result = contestService.updateContest(contest);
        return new CommonResult().setData(result);
    }
}
