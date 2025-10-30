package com.softcon.mapper;

import com.softcon.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业Mapper接口
 */
@Mapper
public interface AssignmentMapper {
    /**
     * 获取所有作业列表
     */
    List<Assignment> getAllAssignments();
    
    /**
     * 根据ID获取作业详情
     */
    Assignment getAssignmentById(@Param("id") Integer id);
    
    /**
     * 新增作业
     */
    int insert(Assignment assignment);
    
    /**
     * 更新作业信息
     */
    int update(Assignment assignment);
    
    /**
     * 删除作业
     */
    int delete(@Param("id") Integer id);
}