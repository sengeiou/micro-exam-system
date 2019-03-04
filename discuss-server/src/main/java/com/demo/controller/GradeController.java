package com.demo.controller;

import com.demo.common.ConstDatas;
import com.demo.dto.CommonResult;
import com.demo.model.Account;
import com.demo.model.Grade;
import com.demo.model.Question;
import com.demo.service.GradeService;
import com.demo.service.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "/grade")
public class GradeController {

    private static Log LOG = LogFactory.getLog(GradeController.class);

    @Autowired
    private GradeService gradeService;
    @Autowired
    private QuestionService questionService;

    //提交试卷
    @PostMapping(value="/api/submitContest")
    public CommonResult submitContest(HttpServletRequest request, @RequestBody Grade grade, Account currentAccount) {
        CommonResult commonResult = new CommonResult();
        List<String> answerStrs = Arrays.asList(grade.getAnswerJson().split(ConstDatas.SPLIT_CHAR));
        AtomicInteger autoResult = new AtomicInteger(0);
        List<Question> questions = questionService.getQuestionsByContestId(grade.getContestId());

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (question.getQuestionType() <= 1 && question.getAnswer()
                    .equals(answerStrs.get(i))) {
                autoResult.getAndAdd(question.getScore());
            }
        }
        grade.setStudentId(currentAccount.getId());
        grade.setResult(autoResult.intValue());
        grade.setAutoResult(autoResult.intValue());
        grade.setManulResult(0);
        int gradeId = gradeService.addGrade(grade);
        return new CommonResult();
    }

    //完成批改试卷
    @PostMapping(value="/api/finishGrade")
    public CommonResult finishGrade(@RequestBody Grade grade) {
        CommonResult commonResult = new CommonResult();
        grade.setResult(grade.getAutoResult()+grade.getManulResult());
        grade.setFinishTime(new Date());
        grade.setState(1);
        boolean result = gradeService.updateGrade(grade);
        return new CommonResult();
    }
}
