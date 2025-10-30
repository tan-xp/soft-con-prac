package com.softcon.mapper;

import com.softcon.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目Mapper接口
 */
@Mapper
public interface QuestionMapper {
    /**
     * 根据作业ID获取题目列表
     */
    List<Question> getQuestionsByAssignmentId(@Param("assignmentId") Integer assignmentId);
    
    /**
     * 获取所有题目
     */
    List<Question> getAllQuestions();
    
    /**
     * 分页获取所有题目
     */
    List<Question> getAllQuestionsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    /**
     * 获取所有题目的总数
     */
    int getTotalQuestions();
    
    /**
     * 根据ID获取单个题目
     */
    Question getQuestionById(@Param("id") Integer id);
    
    /**
     * 插入单个题目
     */
    int insert(Question question);
    
    /**
     * 批量插入题目
     */
    int batchInsert(@Param("questions") List<Question> questions);
    
    /**
     * 更新题目
     */
    int update(Question question);
    
    /**
     * 根据ID删除题目
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据作业ID删除所有相关题目
     */
    int deleteByAssignmentId(@Param("assignmentId") Integer assignmentId);
    
    /**
     * 重置该作业的所有题目的assignment_id为null（设为未分配状态）
     */
    int resetAssignmentId(@Param("assignmentId") Integer assignmentId);
    
    /**
     * 根据题目内容模糊查询
     */
    List<Question> searchQuestionsByContent(@Param("content") String content);
    
    /**
     * 分页根据题目内容模糊查询
     */
    List<Question> searchQuestionsByContentPage(@Param("content") String content, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    /**
     * 根据题目内容模糊查询的总数
     */
    int getSearchTotalQuestions(@Param("content") String content);
    
    /**
     * 获取未分配的试题（assignment_id为空的试题）
     */
    List<Question> getUnassignedQuestions();
    
    /**
     * 根据运算符类型获取未分配的试题
     */
    List<Question> getUnassignedQuestionsByOperator(@Param("operator") String operator);
    
    /**
     * 更新单个试题的作业ID
     */
    void updateAssignmentId(@Param("id") Integer id, @Param("assignmentId") Integer assignmentId);
    
    /**
     * 批量更新试题的作业ID
     */
    void batchUpdateAssignmentId(@Param("ids") List<Integer> ids, @Param("assignmentId") Integer assignmentId);
}
