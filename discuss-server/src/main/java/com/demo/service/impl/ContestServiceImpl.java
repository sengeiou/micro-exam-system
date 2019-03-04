package com.demo.service.impl;

import com.demo.dao.ContestMapper;
import com.demo.dao.SubjectMapper;
import com.demo.model.Contest;
import com.demo.model.Subject;
import com.demo.service.ContestService;
import com.demo.util.FastJsonUtils;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

@Service("contestService")
public class ContestServiceImpl implements ContestService {

    private static Log LOG = LogFactory.getLog(ContestServiceImpl.class);

    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private Jedis jedis;
    @Override
    public int addContest(Contest contest) {
        contest.setTotalScore(0);
        contest.setState(0);
        jedis.del("contests");
        return contestMapper.insertContest(contest);
    }

    @Override
    public boolean updateContest(Contest contest) {
        jedis.del("contests");
        return contestMapper.updateContestById(contest) > 0;
    }

    @Override
    public Contest getContestById(int id) {
        return contestMapper.getContestById(id);
    }

    @Override
    public Map<String, Object> getContests(int pageNum, int pageSize) {
        String json = jedis.get("contests");
        if (StringUtils.isNotEmpty(json)){
            return FastJsonUtils.getJsonToBean(json,HashMap.class);
        }else {
            Map<String, Object> data = new HashMap<>();
            int count = contestMapper.getCount();
            if (count == 0) {
                data.put("pageNum", 0);
                data.put("pageSize", 0);
                data.put("totalPageNum", 1);
                data.put("totalPageSize", 0);
                data.put("contests", new ArrayList<>());
                return data;
            }
            int totalPageNum = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
            if (pageNum > totalPageNum) {
                data.put("pageNum", 0);
                data.put("pageSize", 0);
                data.put("totalPageNum", totalPageNum);
                data.put("totalPageSize", 0);
                data.put("contests", new ArrayList<>());
                return data;
            }
            List<Subject> subjects = subjectMapper.getSubjects();
            PageHelper.startPage(pageNum, pageSize);
            List<Contest> contests = contestMapper.getContests();
            Map<Integer, String> subjectId2name = subjects.stream().
                    collect(Collectors.toMap(Subject::getId, Subject::getName));
            for (Contest contest : contests) {
                contest.setSubjectName(subjectId2name.
                        getOrDefault(contest.getSubjectId(), "未知科目"));
            }
            data.put("pageNum", pageNum);
            data.put("pageSize", pageSize);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", count);
            data.put("contests", contests);
            jedis.set("contests", FastJsonUtils.getBeanToJson(data));
            return data;
        }
    }

    @Override
    public boolean deleteContest(int id) {
        jedis.del("contests");
        return contestMapper.deleteContest(id) > 0;
    }

    @Override
    public boolean updateStateToStart() {
        return contestMapper.updateStateToStart(new Date()) > 0;
    }

    @Override
    public boolean updateStateToEnd() {
        return contestMapper.updateStateToEnd(new Date()) > 0;
    }

    @Override
    public List<Contest> getContestsByContestIds(Set<Integer> contestIds) {
        return contestMapper.getContestsByContestIds(contestIds);
    }
}
