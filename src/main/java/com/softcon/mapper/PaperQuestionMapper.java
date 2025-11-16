package com.softcon.mapper;

import com.softcon.entity.PaperQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaperQuestionMapper {
    List<PaperQuestion> getByPaperId(@Param("paperId") Integer paperId);
    int insert(PaperQuestion pq);
    int batchInsert(@Param("items") List<PaperQuestion> items);
    int deleteByPaperId(@Param("paperId") Integer paperId);
}