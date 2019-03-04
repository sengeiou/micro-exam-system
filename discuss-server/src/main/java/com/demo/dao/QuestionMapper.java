package com.demo.dao;

import com.demo.model.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface QuestionMapper {

    int insertQuestion(@Param("question") Question question);

    int deleteQuestion(@Param("id") int id);

    int updateQuestionById(@Param("question") Question question);

    Question getQuestionById(@Param("id") int id);

    int getCountByContent(@Param("content") String content);

    List<Question> getQuestionsByContent(@Param("content") String content);

    List<Question> getQuestionByContestId(@Param("contestId") int contestId);

    int getCountByProblemsetIdAndContentAndDiffculty(@Param("problemsetId") int problemsetId,
                                                     @Param("content") String content,
                                                     @Param("difficulty") int diffculty);

    List<Question> getQuestionsByProblemsetIdAndContentAndDiffculty(@Param("problemsetId") int problemsetId,
                                                                    @Param("content") String content,
                                                                    @Param("difficulty") int diffculty);

    int updateQuestionsStateByContestId(@Param("contestId") int contestId,
                                        @Param("state") int state);
}
