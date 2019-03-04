package com.demo.controller;

import com.demo.common.ConstDatas;
import com.demo.dto.CommonResult;
import com.demo.model.Subject;
import com.demo.service.SubjectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/subject")
public class SubjectController {

    private static Log LOG = LogFactory.getLog(SubjectController.class);

    @Autowired
    private SubjectService subjectService;

    //添加课程
    @RequestMapping(value="/api/addSubject")
    public CommonResult addSubject(@RequestBody Subject subject) {
        subject.setImgUrl(ConstDatas.DEFAULT_SUBJECT_IMG_URL);
        subject.setQuestionNum(0);
        subjectService.addSubject(subject);
        return new CommonResult();
    }

    //更新课程
    @RequestMapping(value="/api/updateSubject")
    public CommonResult updateSubject(@RequestBody Subject subject) {
        subjectService.updateSubject(subject);
        return new CommonResult();
    }

    //删除课程
    @DeleteMapping("/api/deleteSubject/{id}")
    public CommonResult deleteSubject(@PathVariable int id) {
        CommonResult commonResult = new CommonResult();
        subjectService.deleteSubjectById(id);
        return new CommonResult();
    }

//    分页获取所有课程列表
    @RequestMapping(value = "/api/getSubjects")
    public CommonResult getSubjects(HttpServletRequest request, HttpServletResponse response) {
        return new CommonResult();
    }
}
