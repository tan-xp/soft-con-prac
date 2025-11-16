package com.softcon.mapper;

import com.softcon.entity.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaperMapper {
    List<Paper> getAllPapers();
    Paper getPaperById(@Param("id") Integer id);
    int insert(Paper paper);
    int update(Paper paper);
    int delete(@Param("id") Integer id);
}