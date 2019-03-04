package com.demo.dao;

import com.demo.model.Contest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface ContestMapper {

    int insertContest(@Param("contest") Contest contest);

    int updateContestById(@Param("contest") Contest contest);

    Contest getContestById(@Param("id") int id);

    int getCount();

    List<Contest> getContests();

    int deleteContest(@Param("id") int id);

    int updateStateToStart(@Param("currentTime") Date currentTime);

    int updateStateToEnd(@Param("currentTime") Date currentTime);

    List<Contest> getContestsByContestIds(@Param("contestIds") Set<Integer> contestIds);
}
