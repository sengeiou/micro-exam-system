package com.demo.dao;

import com.demo.model.Subject;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SubjectMapper {

    int insertSubject(@Param("subject") Subject subject);

    int updateSubjectById(@Param("subject") Subject subject);

    Subject getSubjectById(@Param("id") int id);

    int getCount();

    List<Subject> getSubjects();

    int deleteSubjectById(@Param("id") int id);
}
