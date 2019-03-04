package com.demo.service.impl;

import com.demo.dao.ContestMapper;
import com.demo.dao.QuestionMapper;
import com.demo.dao.SubjectMapper;
import com.demo.model.Contest;
import com.demo.model.Question;
import com.demo.model.Subject;
import com.demo.service.QuestionService;
import com.demo.util.FastJsonUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static Log LOG = LogFactory.getLog(QuestionServiceImpl.class);

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private Jedis jedis;

    @Override
    public int addQuestion(Question question) {
        jedis.del("questions");
        if (question.getContestId() == 0) {
            question.setState(1);
        } else {
            question.setState(0);
            Contest contest = contestMapper.getContestById(question.getContestId());
            contest.setTotalScore(contest.getTotalScore()+question.getScore());
            contestMapper.updateContestById(contest);
        }
        Subject subject = subjectMapper.getSubjectById(question.getSubjectId());
        subject.setQuestionNum(subject.getQuestionNum()+1);
        subjectMapper.updateSubjectById(subject);
        return questionMapper.insertQuestion(question);
    }

    @Override
    public boolean updateQuestion(Question question) {
        jedis.del("questions");
        if (question.getContestId() != 0) {
            Contest contest = contestMapper.getContestById(question.getContestId());
            Question sourceQuestion = questionMapper.getQuestionById(question.getId());
            contest.setTotalScore(contest.getTotalScore()-sourceQuestion.getScore()
                    +question.getScore());
            contestMapper.updateContestById(contest);
        }
        return questionMapper.updateQuestionById(question) > 0;
    }

    @Override
    public List<Question> getQuestionsByContestId(int contestId) {
        String json = jedis.hget("questions", String.valueOf(contestId));
        List<Question> questions=null;
        if (StringUtils.isNotEmpty(json)) {
            questions = FastJsonUtils.getJsonToList(json,Question.class);
        }else {
            questions=questionMapper.getQuestionByContestId(contestId);
            jedis.hset("questions",String.valueOf(contestId), FastJsonUtils.getBeanToJson(questions));
        }
        return questions;
    }

    @Override
    public Map<String, Object> getQuestionsByContent(int pageNum, int pageSize, String content) {
        Map<String, Object> data = new HashMap<>();
        int count = questionMapper.getCountByContent(content);
        if (count == 0) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", 1);
            data.put("totalPageSize", 0);
            data.put("questionsSize", 0);
            data.put("questions", new ArrayList<>());
            return data;
        }
        int totalPageNum = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        if (pageNum > totalPageNum) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", 0);
            data.put("questionsSize", 0);
            data.put("questions", new ArrayList<>());
            return data;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionMapper.getQuestionsByContent(content);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("totalPageNum", totalPageNum);
        data.put("totalPageSize", count);
        data.put("questionsSize", questions.size());
        data.put("questions", questions);
        return data;
    }

    @Override
    public boolean deleteQuestion(int id) {
        jedis.del("questions");
        return questionMapper.deleteQuestion(id) > 0;
    }

    @Override
    public Question getQuestionById(int id) {
        return questionMapper.getQuestionById(id);
    }

    @Override
    public Map<String, Object> getQuestionsByProblemsetIdAndContentAndDiffculty(int pageNum, int pageSize, int problemsetId, String content, int difficulty) {
        Map<String, Object> data = new HashMap<>();
        int count = questionMapper.getCountByProblemsetIdAndContentAndDiffculty(problemsetId,
                content, difficulty);
        if (count == 0) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", 1);
            data.put("totalPageSize", 0);
            data.put("questionsSize", 0);
            data.put("problemsetId", problemsetId);
            data.put("questions", new ArrayList<>());
            return data;
        }
        int totalPageNum = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        if (pageNum > totalPageNum) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", 0);
            data.put("questionsSize", 0);
            data.put("problemsetId", problemsetId);
            data.put("questions", new ArrayList<>());
            return data;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionMapper.getQuestionsByProblemsetIdAndContentAndDiffculty(
                problemsetId, content, difficulty);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("totalPageNum", totalPageNum);
        data.put("totalPageSize", count);
        data.put("questionsSize", questions.size());
        data.put("problemsetId", problemsetId);
        data.put("questions", questions);
        return data;
    }

    @Override
    public boolean updateQuestionsStateByContestId(int contestId, int state) {
        jedis.del("questions");
        return questionMapper.updateQuestionsStateByContestId(contestId, state) > 0;
    }
}
