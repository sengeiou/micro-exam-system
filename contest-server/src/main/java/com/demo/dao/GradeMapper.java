package com.demo.dao;

import com.demo.model.Grade;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface GradeMapper {

    int insertGrade(@Param("grade") Grade grade);

    int deleteGrade(@Param("id") int id);

    int updateGradeById(@Param("grade") Grade grade);

    Grade getGradeById(@Param("id") int id);

    int getCountByStudentId(@Param("studentId") int studentId);

    List<Grade> getGradesByStudentId(@Param("studentId") int studentId);

    List<Grade> getGradesByContestId(@Param("contestId") int contestId);
}
